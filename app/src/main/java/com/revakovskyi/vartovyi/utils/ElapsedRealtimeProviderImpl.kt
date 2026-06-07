package com.revakovskyi.vartovyi.utils

import android.os.SystemClock
import com.revakovskyi.vartovyi.contract.ElapsedRealtimeProvider

class ElapsedRealtimeProviderImpl : ElapsedRealtimeProvider {
    override fun now(): Long = SystemClock.elapsedRealtime()
}
