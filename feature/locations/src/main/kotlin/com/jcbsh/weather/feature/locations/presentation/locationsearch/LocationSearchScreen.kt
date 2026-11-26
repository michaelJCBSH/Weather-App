package com.jcbsh.weather.feature.locations.presentation.locationsearch

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
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

@Composable
fun LocationSearchRoute(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: LocationSearchViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LocationSearchScreen(
        uiState = uiState,
        onQueryChange = viewModel::onQueryChange,
        onRetry = viewModel::onRetry,
        onLocationSelected = viewModel::onLocationSelected,
        onSaveErrorShown = viewModel::onSaveErrorShown,
        onBack = onBack,
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationSearchScreen(
    uiState: LocationSearchUiState,
    onQueryChange: (String) -> Unit,
    onRetry: () -> Unit,
    onLocationSelected: (Location) -> Unit,
    onSaveErrorShown: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val saveFailedMessage = stringResource(R.string.location_search_save_failed)
    LaunchedEffect(uiState.saveStatus) {
        when (uiState.saveStatus) {
            SaveStatus.Saved -> onBack()
            SaveStatus.Failed -> {
                snackbarHostState.showSnackbar(saveFailedMessage)
                onSaveErrorShown()
            }
            SaveStatus.Idle -> Unit
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.location_search_title)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(WeatherIcons.ArrowBack),
                            contentDescription = stringResource(R.string.location_search_back),
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentAlignment = Alignment.TopCenter,
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 640.dp)
                    .fillMaxSize(),
            ) {
                SearchField(
                    query = uiState.query,
                    onQueryChange = onQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = WeatherTheme.spacing.Medium, vertical = WeatherTheme.spacing.Small),
                )
                SearchResultsContent(
                    results = uiState.results,
                    onRetry = onRetry,
                    onLocationSelected = onLocationSelected,
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    query: String,
    onQueryChange: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    val focusRequester = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current
    LaunchedEffect(focusRequester) { focusRequester.requestFocus() }

    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = modifier.focusRequester(focusRequester),
        placeholder = { Text(stringResource(R.string.location_search_placeholder)) },
        leadingIcon = { Icon(painterResource(WeatherIcons.Search), contentDescription = null) },
        trailingIcon = if (query.isNotEmpty()) {
            {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        painter = painterResource(WeatherIcons.Close),
                        contentDescription = stringResource(R.string.location_search_clear),
                    )
                }
            }
        } else {
            null
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { keyboard?.hide() }),
    )
}

@Composable
private fun SearchResultsContent(
    results: SearchResults,
    onRetry: () -> Unit,
    onLocationSelected: (Location) -> Unit,
) {
    when (results) {
        SearchResults.Idle -> Message(
            text = pluralStringResource(
                R.plurals.location_search_hint,
                LocationSearchViewModel.MinQueryLength,
                LocationSearchViewModel.MinQueryLength,
            ),
        )
        SearchResults.Loading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(WeatherTheme.spacing.Large),
            contentAlignment = Alignment.Center,
        ) {
            CircularProgressIndicator()
        }
        SearchResults.Error -> Message(text = stringResource(R.string.location_search_error)) {
            Button(onClick = onRetry) {
                Text(stringResource(R.string.location_search_retry))
            }
        }
        is SearchResults.Empty -> Message(text = stringResource(R.string.location_search_no_results, results.query))
        is SearchResults.Content -> LazyColumn(modifier = Modifier.fillMaxSize()) {
            items(results.results, key = { it.location.id }) { result ->
                SearchResultRow(result = result, onClick = { onLocationSelected(result.location) })
            }
        }
    }
}

@Composable
private fun SearchResultRow(
    result: SearchResult,
    onClick: () -> Unit,
) {
    ListItem(
        headlineContent = { Text(result.location.name) },
        supportingContent = result.location.regionAndCountry()?.let { detail -> { Text(detail) } },
        trailingContent = if (result.isSaved) {
            { Text(stringResource(R.string.location_search_saved), style = WeatherTheme.typography.labelLarge) }
        } else {
            null
        },
        modifier = if (result.isSaved) Modifier else Modifier.clickable(onClick = onClick),
    )
}

@Composable
private fun Message(
    text: String,
    action: (@Composable () -> Unit)? = null,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(WeatherTheme.spacing.Large),
        verticalArrangement = Arrangement.spacedBy(WeatherTheme.spacing.Medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = text,
            style = WeatherTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
        action?.invoke()
    }
}

@Preview
@Composable
private fun LocationSearchContentPreview() {
    WeatherThemeForPreview {
        LocationSearchScreen(
            uiState = LocationSearchUiState(
                query = "Spring",
                results = SearchResults.Content(
                    listOf(
                        SearchResult(
                            Location(1, "Springfield", "Missouri", "United States", "US", 37.2, -93.3, null),
                            isSaved = true,
                        ),
                        SearchResult(
                            Location(2, "Springfield", "Illinois", "United States", "US", 39.8, -89.6, null),
                            isSaved = false,
                        ),
                    ),
                ),
            ),
            onQueryChange = {},
            onRetry = {},
            onLocationSelected = {},
            onSaveErrorShown = {},
            onBack = {},
        )
    }
}

@Preview
@Composable
private fun LocationSearchIdlePreview() {
    WeatherThemeForPreview {
        LocationSearchScreen(
            uiState = LocationSearchUiState(),
            onQueryChange = {},
            onRetry = {},
            onLocationSelected = {},
            onSaveErrorShown = {},
            onBack = {},
        )
    }
}
