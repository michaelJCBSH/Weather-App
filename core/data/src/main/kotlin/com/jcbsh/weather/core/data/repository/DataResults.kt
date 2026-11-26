package com.jcbsh.weather.core.data.repository

import com.jcbsh.weather.core.common.logging.Logger
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.domain.result.DataResult
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

/** Runs [block], turning any exception except cancellation into a [DataResult.Error]. */
@Suppress("TooGenericExceptionCaught")
internal suspend fun <T> Logger.runCatchingData(operation: String, block: suspend () -> T): DataResult<T> =
    try {
        DataResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        warn("$operation failed", e)
        DataResult.Error(e.toDataError())
    }

private fun Exception.toDataError() = when (this) {
    is IOException -> DataError.Network
    is HttpException -> DataError.Server
    else -> DataError.Unknown
}
