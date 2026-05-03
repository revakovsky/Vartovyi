package com.revakovskyi.vartovyi.utils

import android.os.SystemClock

class ElapsedRealtimeProviderImpl : ElapsedRealtimeProvider {
    override fun now(): Long = SystemClock.elapsedRealtime()
}
