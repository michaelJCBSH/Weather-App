package com.jcbsh.weather.feature.locations.presentation.savedlocations

import app.cash.turbine.test
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.testing.MainDispatcherRule
import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.core.testing.fake.FakeSavedLocationRepository
import com.jcbsh.weather.core.testing.fake.RecordingLogger
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import java.time.Instant

class SavedLocationsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val logger = RecordingLogger()

    @Test
    fun `no saved locations is Empty`() = runTest {
        val viewModel = SavedLocationsViewModel(FakeSavedLocationRepository(), logger)

        viewModel.uiState.test {
            assertEquals(SavedLocationsUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `saved locations are shown in saved order`() = runTest {
        val repository = FakeSavedLocationRepository(
            listOf(
                SavedLocation(TestLocations.Sydney, Instant.ofEpochSecond(1)),
                SavedLocation(TestLocations.Singapore, Instant.ofEpochSecond(2)),
            ),
        )
        val viewModel = SavedLocationsViewModel(repository, logger)

        viewModel.uiState.test {
            assertEquals(
                SavedLocationsUiState.Content(listOf(TestLocations.Sydney, TestLocations.Singapore)),
                awaitItem(),
            )
        }
    }

    @Test
    fun `failing to load is Error`() = runTest {
        val repository = FakeSavedLocationRepository().apply {
            savedLocationsFlow = flow { throw IllegalStateException("db closed") }
        }
        val viewModel = SavedLocationsViewModel(repository, logger)

        viewModel.uiState.test {
            assertEquals(SavedLocationsUiState.Error, awaitItem())
        }
    }

    @Test
    fun `removing the last location shows Empty`() = runTest {
        val repository = FakeSavedLocationRepository(
            listOf(SavedLocation(TestLocations.Sydney, Instant.ofEpochSecond(1))),
        )
        val viewModel = SavedLocationsViewModel(repository, logger)

        viewModel.uiState.test {
            assertEquals(SavedLocationsUiState.Content(listOf(TestLocations.Sydney)), awaitItem())

            viewModel.onRemoveLocation(TestLocations.Sydney.id)

            assertEquals(SavedLocationsUiState.Empty, awaitItem())
        }
    }

    @Test
    fun `a failed removal is logged and keeps the location`() = runTest {
        val repository = FakeSavedLocationRepository(
            listOf(SavedLocation(TestLocations.Sydney, Instant.ofEpochSecond(1))),
        ).apply { failWith = DataError.Unknown }
        val viewModel = SavedLocationsViewModel(repository, logger)

        viewModel.onRemoveLocation(TestLocations.Sydney.id)

        assertEquals(listOf(TestLocations.Sydney), repository.savedLocations.map { it.location })
        assertEquals(1, logger.messages.size)
    }
}
