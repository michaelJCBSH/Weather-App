package com.jcbsh.weather.feature.locations.presentation.savedlocations

import android.content.Context
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.hasAnyDescendant
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.test.core.app.ApplicationProvider
import com.jcbsh.weather.design.WeatherThemeForPreview
import com.jcbsh.weather.feature.locations.R
import com.jcbsh.weather.feature.locations.testdata.TestLocations
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class SavedLocationsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val context: Context = ApplicationProvider.getApplicationContext()
    private val removed = mutableListOf<Long>()
    private var addClicks = 0

    private fun setContent(uiState: SavedLocationsUiState) {
        composeRule.setContent {
            WeatherThemeForPreview {
                SavedLocationsScreen(
                    uiState = uiState,
                    onAddLocation = { addClicks++ },
                    onRemoveLocation = { removed += it },
                )
            }
        }
    }

    @Test
    fun `add button opens Location Search`() {
        setContent(SavedLocationsUiState.Empty)

        // The FAB's label is not merged into its clickable node, so target the label itself.
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_add), useUnmergedTree = true)
            .performClick()

        assertEquals(1, addClicks)
    }

    @Test
    fun `empty state explains how to add a location`() {
        setContent(SavedLocationsUiState.Empty)

        composeRule.onNodeWithText(context.getString(R.string.saved_locations_empty_title)).assertExists()
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_empty_body)).assertExists()
    }

    @Test
    fun `rows show name with region and country`() {
        setContent(SavedLocationsUiState.Content(listOf(TestLocations.Sydney, TestLocations.Singapore)))

        composeRule.onNodeWithText("New South Wales, Australia").assertExists()
        // Singapore has no region: its name and its country line are both just "Singapore".
        composeRule.onAllNodesWithText("Singapore", useUnmergedTree = true).assertCountEquals(2)
    }

    @Test
    fun `delete action asks for confirmation, then removes`() {
        setContent(SavedLocationsUiState.Content(listOf(TestLocations.Sydney)))

        deleteViaAccessibilityAction("Sydney")
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_remove_title, "Sydney")).assertExists()
        assertTrue(removed.isEmpty())

        composeRule.onNodeWithText(context.getString(R.string.saved_locations_remove_confirm)).performClick()

        assertEquals(listOf(TestLocations.Sydney.id), removed)
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_remove_title, "Sydney"))
            .assertDoesNotExist()
    }

    @Test
    fun `cancelling the dialog keeps the location`() {
        setContent(SavedLocationsUiState.Content(listOf(TestLocations.Sydney)))

        deleteViaAccessibilityAction("Sydney")
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_remove_cancel)).performClick()

        assertTrue(removed.isEmpty())
        composeRule.onNodeWithText(context.getString(R.string.saved_locations_remove_title, "Sydney"))
            .assertDoesNotExist()
        composeRule.onNodeWithText("Sydney").assertExists()
    }

    private fun deleteViaAccessibilityAction(name: String) {
        val deleteLabel = context.getString(R.string.saved_locations_delete_action)
        val row = composeRule
            .onNode(SemanticsMatcher.keyIsDefined(SemanticsActions.CustomActions) and hasAnyDescendant(hasText(name)))
            .fetchSemanticsNode()
        composeRule.runOnUiThread {
            row.config[SemanticsActions.CustomActions].first { it.label == deleteLabel }.action()
        }
    }
}
