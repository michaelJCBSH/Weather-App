package com.jcbsh.weather.design.experimental

/**
 * Marks a shared component whose design isn't final yet. New components start in
 * `experimental/` with this annotation and move to `stable/` once settled.
 */
@RequiresOptIn(
    message = "This Weather design component is experimental and may change.",
    level = RequiresOptIn.Level.WARNING,
)
@Retention(AnnotationRetention.BINARY)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
annotation class ExperimentalWeatherDesignApi
