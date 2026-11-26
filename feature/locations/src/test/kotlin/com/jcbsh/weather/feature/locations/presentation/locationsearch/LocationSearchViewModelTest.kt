package com.jcbsh.weather.feature.locations.presentation.locationsearch

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.jcbsh.weather.core.domain.model.SavedLocation
import com.jcbsh.weather.core.domain.result.DataError
import com.jcbsh.weather.core.domain.result.DataResult
import com.jcbsh.weather.core.testing.MainDispatcherRule
import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.core.testing.fake.FakeLocationSearchRepository
import com.jcbsh.weather.core.testing.fake.FakeSavedLocationRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Instant

@OptIn(ExperimentalCoroutinesApi::class)
class LocationSearchViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private val searchRepository = FakeLocationSearchRepository()
    private val savedRepository = FakeSavedLocationRepository()

    private fun viewModel(savedStateHandle: SavedStateHandle = SavedStateHandle()) =
        LocationSearchViewModel(searchRepository, savedRepository, savedStateHandle)

    @Test
    fun `starts idle with an empty query`() = runTest {
        viewModel().uiState.test {
            assertEquals(LocationSearchUiState(), awaitItem())
        }
    }

    @Test
    fun `queries shorter than the minimum do not search`() = runTest {
        val viewModel = viewModel()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("S ")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            assertEquals(LocationSearchUiState(query = "S "), expectMostRecentItem())
            assertTrue(searchRepository.searches.isEmpty())
        }
    }

    @Test
    fun `searches once typing pauses, then shows results`() = runTest {
        searchRepository.result = DataResult.Success(listOf(TestLocations.SpringfieldMissouri))
        val viewModel = viewModel()

        viewModel.uiState.test {
            skipItems(1)
            viewModel.onQueryChange("Spr")
            viewModel.onQueryChange("Spring")
            viewModel.onQueryChange("Springfield ")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            assertEquals(
                SearchResults.Content(listOf(SearchResult(TestLocations.SpringfieldMissouri, isSaved = false))),
                expectMostRecentItem().results,
            )
            assertEquals(listOf("Springfield"), searchRepository.searches.map { it.first })
        }
    }

    @Test
    fun `results already saved are marked`() = runTest {
        savedRepository.save(TestLocations.SpringfieldMissouri)
        searchRepository.result = DataResult.Success(
            listOf(TestLocations.SpringfieldMissouri, TestLocations.SpringfieldIllinois),
        )
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onQueryChange("Springfield")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            assertEquals(
                SearchResults.Content(
                    listOf(
                        SearchResult(TestLocations.SpringfieldMissouri, isSaved = true),
                        SearchResult(TestLocations.SpringfieldIllinois, isSaved = false),
                    ),
                ),
                expectMostRecentItem().results,
            )
        }
    }

    @Test
    fun `no matches is Empty with the query`() = runTest {
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onQueryChange("zzqqxx")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            assertEquals(SearchResults.Empty("zzqqxx"), expectMostRecentItem().results)
        }
    }

    @Test
    fun `failure is Error, and retry searches again`() = runTest {
        searchRepository.result = DataResult.Error(DataError.Network)
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onQueryChange("Sydney")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)
            assertEquals(SearchResults.Error, expectMostRecentItem().results)

            searchRepository.result = DataResult.Success(listOf(TestLocations.Sydney))
            viewModel.onRetry()
            testScheduler.runCurrent()

            assertEquals(
                SearchResults.Content(listOf(SearchResult(TestLocations.Sydney, isSaved = false))),
                expectMostRecentItem().results,
            )
            assertEquals(2, searchRepository.searches.size)
        }
    }

    @Test
    fun `selecting a result saves it`() = runTest {
        searchRepository.result = DataResult.Success(listOf(TestLocations.Sydney))
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onQueryChange("Sydney")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            viewModel.onLocationSelected(TestLocations.Sydney)
            testScheduler.runCurrent()

            assertEquals(SaveStatus.Saved, expectMostRecentItem().saveStatus)
            assertEquals(listOf(TestLocations.Sydney), savedRepository.savedLocations.map { it.location })
        }
    }

    @Test
    fun `selecting an already saved result does nothing`() = runTest {
        val existing = SavedLocation(TestLocations.Sydney, Instant.EPOCH)
        val savedRepository = FakeSavedLocationRepository(listOf(existing)).apply { failWith = DataError.Unknown }
        searchRepository.result = DataResult.Success(listOf(TestLocations.Sydney))
        val viewModel = LocationSearchViewModel(searchRepository, savedRepository, SavedStateHandle())

        viewModel.uiState.test {
            viewModel.onQueryChange("Sydney")
            advanceTimeBy(LocationSearchViewModel.DebounceMillis + 1)

            viewModel.onLocationSelected(TestLocations.Sydney)
            testScheduler.runCurrent()

            assertEquals(SaveStatus.Idle, expectMostRecentItem().saveStatus)
        }
    }

    @Test
    fun `a failed save is reported until shown`() = runTest {
        savedRepository.failWith = DataError.Unknown
        val viewModel = viewModel()

        viewModel.uiState.test {
            viewModel.onLocationSelected(TestLocations.Sydney)
            testScheduler.runCurrent()
            assertEquals(SaveStatus.Failed, expectMostRecentItem().saveStatus)

            viewModel.onSaveErrorShown()
            testScheduler.runCurrent()
            assertEquals(SaveStatus.Idle, expectMostRecentItem().saveStatus)
        }
    }

    @Test
    fun `query survives process death`() = runTest {
        val viewModel = viewModel(SavedStateHandle(mapOf("query" to "Syd")))

        viewModel.uiState.test {
            assertEquals("Syd", awaitItem().query)
        }
    }
}
