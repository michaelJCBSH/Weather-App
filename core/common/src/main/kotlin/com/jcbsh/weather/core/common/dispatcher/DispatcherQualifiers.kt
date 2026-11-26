package com.jcbsh.weather.core.common.dispatcher

import javax.inject.Qualifier

/** Qualifies a `CoroutineDispatcher` for blocking IO (disk, network). */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class IoDispatcher

/** Qualifies a `CoroutineDispatcher` for CPU-bound work. */
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class DefaultDispatcher
