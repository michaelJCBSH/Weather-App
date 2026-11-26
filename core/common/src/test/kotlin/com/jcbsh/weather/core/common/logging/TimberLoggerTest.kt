package com.jcbsh.weather.core.common.logging

import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import timber.log.Timber

class TimberLoggerTest {
    private val tree = RecordingTree()
    private val logger = TimberLogger()

    @Before
    fun plant() = Timber.plant(tree)

    @After
    fun uproot() = Timber.uprootAll()

    @Test
    fun `forwards each level to Timber`() {
        val failure = IllegalStateException("boom")

        logger.debug("debug message")
        logger.warn("warn message")
        logger.error("error message", failure)

        assertEquals(
            listOf(
                Entry(android.util.Log.DEBUG, "debug message", null),
                Entry(android.util.Log.WARN, "warn message", null),
                Entry(android.util.Log.ERROR, "error message", failure),
            ),
            tree.entries,
        )
    }

    private data class Entry(val priority: Int, val message: String, val throwable: Throwable?)

    private class RecordingTree : Timber.Tree() {
        val entries = mutableListOf<Entry>()

        override fun log(priority: Int, tag: String?, message: String, t: Throwable?) {
            entries += Entry(priority, message.substringBefore('\n'), t)
        }
    }
}
