package com.revakovskyi.vartovyi.controllers.alarm

import com.revakovskyi.vartovyi.contract.ElapsedRealtimeProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val ZERO_MILLIS = 0L
private const val COOLDOWN_TICK_INTERVAL_MILLIS = 1000L

class AlarmRetriggerCooldownStateHolder(
    private val elapsedRealtimeProvider: ElapsedRealtimeProvider,
) {

    private val cooldownLock = Any()
    private var monitoringServiceScope: CoroutineScope? = null
    private var cooldownTickerJob: Job? = null
    private var cooldownUntilElapsedRealtimeMillis: Long = ZERO_MILLIS

    private val _remainingCooldownMillis = MutableStateFlow(ZERO_MILLIS)
    val remainingCooldownMillis: StateFlow<Long> = _remainingCooldownMillis.asStateFlow()

    fun bindMonitoringScope(scope: CoroutineScope) {
        synchronized(cooldownLock) {
            monitoringServiceScope = scope
            restartTickerLocked()
        }
    }

    fun clearAndUnbind() {
        synchronized(cooldownLock) {
            cooldownTickerJob?.cancel()
            cooldownTickerJob = null
            monitoringServiceScope = null
            cooldownUntilElapsedRealtimeMillis = ZERO_MILLIS
            _remainingCooldownMillis.update { ZERO_MILLIS }
        }
    }

    fun setCooldownUntilElapsedRealtimeMillis(untilElapsedRealtimeMillis: Long) {
        synchronized(cooldownLock) {
            cooldownUntilElapsedRealtimeMillis =
                untilElapsedRealtimeMillis.coerceAtLeast(ZERO_MILLIS)
            restartTickerLocked()
        }
    }

    private fun restartTickerLocked() {
        cooldownTickerJob?.cancel()

        val scope = monitoringServiceScope ?: return
        if (cooldownUntilElapsedRealtimeMillis <= elapsedRealtimeProvider.now()) {
            cooldownUntilElapsedRealtimeMillis = ZERO_MILLIS
            _remainingCooldownMillis.update { ZERO_MILLIS }
            return
        }

        cooldownTickerJob = scope.launch {
            while (isActive) {
                val remainingMillis = synchronized(cooldownLock) {
                    (cooldownUntilElapsedRealtimeMillis - elapsedRealtimeProvider.now())
                        .coerceAtLeast(ZERO_MILLIS)
                }

                _remainingCooldownMillis.update { remainingMillis }

                if (remainingMillis == ZERO_MILLIS) {
                    synchronized(cooldownLock) {
                        cooldownUntilElapsedRealtimeMillis = ZERO_MILLIS
                        cooldownTickerJob = null
                    }
                    break
                }

                delay(COOLDOWN_TICK_INTERVAL_MILLIS)
            }
        }
    }

}
