package com.revakovskyi.vartovyi.utils

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.revakovskyi.vartovyi.contract.CrashReporter

private const val TAG = "CrashReporter"

class CrashlyticsCrashReporter : CrashReporter {

    override fun report(throwable: Throwable) {
        Log.e(TAG, throwable.message ?: throwable.javaClass.simpleName, throwable)
        FirebaseCrashlytics.getInstance().recordException(throwable)
    }

}
