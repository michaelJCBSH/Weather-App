package com.jcbsh.weather.core.testing

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class MainDispatcherRuleTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `work launched on Main runs on the test dispatcher`() = runTest {
        var ran = false

        CoroutineScope(Dispatchers.Main).launch { ran = true }

        assertTrue(ran)
    }
}
