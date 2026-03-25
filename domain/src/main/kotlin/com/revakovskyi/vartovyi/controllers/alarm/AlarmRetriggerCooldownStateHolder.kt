package com.revakovskyi.vartovyi.controllers.alarm

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val ZERO_MILLIS = 0L
private const val COOLDOWN_TICK_INTERVAL_MILLIS = 1000L

class AlarmRetriggerCooldownStateHolder {

    private val cooldownLock = Any()
    private var monitoringServiceScope: CoroutineScope? = null
    private var cooldownTickerJob: Job? = null
    private var cooldownUntilEpochMillis: Long = ZERO_MILLIS

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
            cooldownUntilEpochMillis = ZERO_MILLIS
            _remainingCooldownMillis.value = ZERO_MILLIS
        }
    }

    fun setCooldownUntilEpochMillis(untilEpochMillis: Long) {
        synchronized(cooldownLock) {
            cooldownUntilEpochMillis = untilEpochMillis.coerceAtLeast(ZERO_MILLIS)
            restartTickerLocked()
        }
    }

    private fun restartTickerLocked() {
        cooldownTickerJob?.cancel()

        val scope = monitoringServiceScope ?: return
        if (cooldownUntilEpochMillis <= System.currentTimeMillis()) {
            cooldownUntilEpochMillis = ZERO_MILLIS
            _remainingCooldownMillis.value = ZERO_MILLIS
            return
        }

        cooldownTickerJob = scope.launch {
            while (isActive) {
                val remainingMillis = (cooldownUntilEpochMillis - System.currentTimeMillis())
                    .coerceAtLeast(ZERO_MILLIS)

                _remainingCooldownMillis.value = remainingMillis

                if (remainingMillis == ZERO_MILLIS) {
                    synchronized(cooldownLock) {
                        cooldownUntilEpochMillis = ZERO_MILLIS
                        cooldownTickerJob = null
                    }
                    break
                }

                delay(COOLDOWN_TICK_INTERVAL_MILLIS)
            }
        }
    }

}

