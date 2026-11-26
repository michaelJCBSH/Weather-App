package com.jcbsh.weather.feature.locations.presentation.savedlocations

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.jcbsh.weather.core.domain.model.Location
import com.jcbsh.weather.design.WeatherIcons
import com.jcbsh.weather.design.WeatherTheme
import com.jcbsh.weather.design.WeatherThemeForPreview
import com.jcbsh.weather.feature.locations.R
import com.jcbsh.weather.feature.locations.presentation.regionAndCountry
import kotlinx.coroutines.launch

@Composable
fun SavedLocationsRoute(
    onAddLocation: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SavedLocationsViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    SavedLocationsScreen(
        uiState = uiState,
        onAddLocation = onAddLocation,
        onRemoveLocation = viewModel::onRemoveLocation,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedLocationsScreen(
    uiState: SavedLocationsUiState,
    onAddLocation: () -> Unit,
    onRemoveLocation: (locationId: Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var pendingRemovalId by rememberSaveable { mutableStateOf<Long?>(null) }

    Scaffold(
        modifier = modifier,
        topBar = { TopAppBar(title = { Text(stringResource(R.string.saved_locations_title)) }) },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onAddLocation,
                icon = { Icon(painterResource(WeatherIcons.Add), contentDescription = null) },
                text = { Text(stringResource(R.string.saved_locations_add)) },
            )
        },
    ) { padding ->
        when (uiState) {
            SavedLocationsUiState.Loading -> Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator()
            }
            SavedLocationsUiState.Empty -> Message(
                title = stringResource(R.string.saved_locations_empty_title),
                body = stringResource(R.string.saved_locations_empty_body),
                modifier = Modifier.padding(padding),
            )
            SavedLocationsUiState.Error -> Message(
                title = stringResource(R.string.saved_locations_error),
                body = null,
                modifier = Modifier.padding(padding),
            )
            is SavedLocationsUiState.Content -> SavedLocationsList(
                uiState = uiState,
                onRequestRemoval = { pendingRemovalId = it.id },
                contentPadding = padding,
            )
        }
    }

    val pendingRemoval = (uiState as? SavedLocationsUiState.Content)
        ?.locations
        ?.firstOrNull { it.id == pendingRemovalId }
    if (pendingRemoval != null) {
        RemoveLocationDialog(
            location = pendingRemoval,
            onConfirm = {
                onRemoveLocation(pendingRemoval.id)
                pendingRemovalId = null
            },
            onDismiss = { pendingRemovalId = null },
        )
    }
}

@Composable
private fun SavedLocationsList(
    uiState: SavedLocationsUiState.Content,
    onRequestRemoval: (Location) -> Unit,
    contentPadding: PaddingValues,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter,
    ) {
        LazyColumn(
            modifier = Modifier
                .widthIn(max = 640.dp)
                .fillMaxSize(),
            // Leave room below the last row so the FAB doesn't cover it.
            contentPadding = PaddingValues(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding() + 88.dp,
            ),
        ) {
            items(uiState.locations, key = { it.id }) { location ->
                SavedLocationRow(
                    location = location,
                    onRequestRemoval = { onRequestRemoval(location) },
                    modifier = Modifier.animateItem(),
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SavedLocationRow(
    location: Location,
    onRequestRemoval: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val dismissState = rememberSwipeToDismissBoxState()
    val scope = rememberCoroutineScope()
    val deleteLabel = stringResource(R.string.saved_locations_delete_action)

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        onDismiss = { value ->
            if (value == SwipeToDismissBoxValue.EndToStart) {
                onRequestRemoval()
            }
            // The row only goes away once the user confirms in the dialog.
            scope.launch { dismissState.reset() }
        },
        backgroundContent = {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(WeatherTheme.colorScheme.errorContainer)
                    .padding(horizontal = WeatherTheme.spacing.Large),
                contentAlignment = Alignment.CenterEnd,
            ) {
                Icon(
                    painter = painterResource(WeatherIcons.Delete),
                    contentDescription = null,
                    tint = WeatherTheme.colorScheme.onErrorContainer,
                )
            }
        },
        modifier = modifier.semantics {
            customActions = listOf(
                CustomAccessibilityAction(deleteLabel) {
                    onRequestRemoval()
                    true
                },
            )
        },
    ) {
        ListItem(
            headlineContent = { Text(location.name) },
            supportingContent = location.regionAndCountry()?.let { detail -> { Text(detail) } },
        )
    }
}

@Composable
private fun RemoveLocationDialog(
    location: Location,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.saved_locations_remove_title, location.name)) },
        text = { Text(stringResource(R.string.saved_locations_remove_body)) },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text(stringResource(R.string.saved_locations_remove_confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.saved_locations_remove_cancel))
            }
        },
    )
}

@Composable
private fun Message(
    title: String,
    body: String?,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(WeatherTheme.spacing.Large),
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.spacing.Small, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Icon(
            painter = painterResource(WeatherIcons.Location),
            contentDescription = null,
            modifier = Modifier.size(48.dp),
            tint = WeatherTheme.colorScheme.primary,
        )
        Text(
            text = title,
            style = WeatherTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(),
        )
        if (body != null) {
            Text(
                text = body,
                style = WeatherTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

@Preview
@Composable
private fun SavedLocationsContentPreview() {
    WeatherThemeForPreview {
        SavedLocationsScreen(
            uiState = SavedLocationsUiState.Content(
                listOf(
                    Location(1, "Sydney", "New South Wales", "Australia", "AU", -33.87, 151.21, "Australia/Sydney"),
                    Location(2, "Singapore", null, "Singapore", "SG", 1.29, 103.85, "Asia/Singapore"),
                ),
            ),
            onAddLocation = {},
            onRemoveLocation = {},
        )
    }
}

@Preview
@Composable
private fun SavedLocationsEmptyPreview() {
    WeatherThemeForPreview {
        SavedLocationsScreen(uiState = SavedLocationsUiState.Empty, onAddLocation = {}, onRemoveLocation = {})
    }
}
