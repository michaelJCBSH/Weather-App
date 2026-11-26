package com.jcbsh.weather.core.common.logging

import timber.log.Timber
import javax.inject.Inject

/** [Logger] for debug builds. The app plants a [Timber.DebugTree] at startup. */
class TimberLogger @Inject constructor() : Logger {
    override fun debug(message: String) = Timber.d(message)

    override fun warn(message: String, throwable: Throwable?) = Timber.w(throwable, message)

    override fun error(message: String, throwable: Throwable?) = Timber.e(throwable, message)
}
