package com.jcbsh.weather.core.common.logging

/**
 * App-wide logging entry point. Inject it instead of calling a logging library directly so
 * release builds can drop logs and tests can assert on them.
 */
interface Logger {
    fun debug(message: String)
    fun warn(message: String, throwable: Throwable? = null)
    fun error(message: String, throwable: Throwable? = null)
}
