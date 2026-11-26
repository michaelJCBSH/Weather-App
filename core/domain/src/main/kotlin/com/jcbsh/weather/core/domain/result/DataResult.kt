package com.jcbsh.weather.core.domain.result

/** Outcome of a one-shot data operation. Repositories catch exceptions and return [Error] instead. */
sealed interface DataResult<out T> {
    data class Success<T>(val data: T) : DataResult<T>

    data class Error(val error: DataError) : DataResult<Nothing>
}

/** Why a [DataResult] failed, in terms the presentation layer can act on. */
enum class DataError {
    /** No connection, timeout or similar transport failure; retrying may help. */
    Network,

    /** The server answered with an error status. */
    Server,

    /** Anything else, including local storage failures. */
    Unknown,
}
