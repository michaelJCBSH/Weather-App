package com.jcbsh.weather.feature.locations.presentation.locationsearch

import android.content.Context
import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertHasNoClickAction
import androidx.compose.ui.test.assertIsFocused
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.core.testing.data.TestLocations
import com.jcbsh.weather.design.WeatherThemeForPreview
import com.jcbsh.weather.feature.locations.R
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class LocationSearchScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val queries = mutableListOf<String>()
    private val selected = mutableListOf<Location>()
    private var retries = 0
    private var backs = 0
    private var saveErrorsShown = 0

    private fun setContent(uiState: LocationSearchUiState) {
        composeRule.setContent {
            WeatherThemeForPreview {
                LocationSearchScreen(
                    uiState = uiState,
                    onQueryChange = { queries += it },
                    onRetry = { retries++ },
                    onLocationSelected = { selected += it },
                    onSaveErrorShown = { saveErrorsShown++ },
                    onBack = { backs++ },
                )
            }
        }
    }

    @Test
    fun `search field is focused and reports typing`() {
        setContent(LocationSearchUiState())

        val field = composeRule.onNode(hasSetTextAction())
        field.assertIsFocused()
        field.performTextInput("Syd")

        assertEquals(listOf("Syd"), queries)
    }

    @Test
    fun `clear button empties the query`() {
        setContent(LocationSearchUiState(query = "Syd"))

        composeRule.onNodeWithContentDescription(context.getString(R.string.location_search_clear)).performClick()

        assertEquals(listOf(""), queries)
    }

    @Test
    fun `tapping an unsaved result selects it`() {
        setContent(contentState())

        composeRule.onNodeWithText("Illinois, United States").assertHasClickAction().performClick()

        assertEquals(listOf(TestLocations.SpringfieldIllinois), selected)
    }

    @Test
    fun `saved results are labelled and cannot be tapped`() {
        setContent(contentState())

        composeRule.onNodeWithText(context.getString(R.string.location_search_saved)).assertExists()
        composeRule.onNodeWithText("Missouri, United States").assertHasNoClickAction()
    }

    @Test
    fun `error offers retry`() {
        setContent(LocationSearchUiState(query = "Sydney", results = SearchResults.Error))

        composeRule.onNodeWithText(context.getString(R.string.location_search_retry)).performClick()

        assertEquals(1, retries)
    }

    @Test
    fun `back button navigates back`() {
        setContent(LocationSearchUiState())

        composeRule.onNodeWithContentDescription(context.getString(R.string.location_search_back)).performClick()

        assertEquals(1, backs)
    }

    @Test
    fun `a successful save closes the screen`() {
        setContent(LocationSearchUiState(saveStatus = SaveStatus.Saved))

        composeRule.waitForIdle()

        assertEquals(1, backs)
    }

    @Test
    fun `a failed save shows a message`() {
        setContent(LocationSearchUiState(saveStatus = SaveStatus.Failed))

        composeRule.onNodeWithText(context.getString(R.string.location_search_save_failed)).assertExists()
    }

    private fun contentState() = LocationSearchUiState(
        query = "Springfield",
        results = SearchResults.Content(
            listOf(
                SearchResult(TestLocations.SpringfieldMissouri, isSaved = true),
                SearchResult(TestLocations.SpringfieldIllinois, isSaved = false),
            ),
        ),
    )
}
