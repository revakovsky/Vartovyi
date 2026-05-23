package com.revakovskyi.vartovyi.contract

interface CrashReporter {
    fun report(throwable: Throwable)
}
