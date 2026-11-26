package com.jcbsh.weather.core.common.logging

/** [Logger] for release builds: discards everything. */
object NoOpLogger : Logger {
    override fun debug(message: String) = Unit

    override fun warn(message: String, throwable: Throwable?) = Unit

    override fun error(message: String, throwable: Throwable?) = Unit
}
