package com.jcbsh.weather.core.testing.fake

import com.jcbsh.weather.core.common.logging.Logger

/** [Logger] that keeps every message so tests can assert on them. */
class RecordingLogger : Logger {
    val messages = mutableListOf<String>()

    override fun debug(message: String) {
        messages += message
    }

    override fun warn(message: String, throwable: Throwable?) {
        messages += message
    }

    override fun error(message: String, throwable: Throwable?) {
        messages += message
    }
}
