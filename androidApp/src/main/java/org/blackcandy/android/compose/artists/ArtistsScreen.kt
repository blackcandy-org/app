package org.blackcandy.android.compose.artists

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import org.blackcandy.android.R
import org.blackcandy.android.compose.CoverCard
import org.blackcandy.android.compose.LoadingIndicator
import org.blackcandy.android.compose.ScreenAppBar
import org.blackcandy.android.utils.SnackbarUtil.Companion.ShowSnackbar
import org.blackcandy.shared.viewmodels.ArtistsViewModel
import org.koin.androidx.compose.koinViewModel

private const val LOAD_MORE_THRESHOLD = 6

@Composable
fun ArtistsScreen(
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: ArtistsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val gridState = rememberLazyGridState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex =
                gridState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: return@derivedStateOf false

            lastVisibleIndex >= gridState.layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
        }
    }

    LaunchedEffect(Unit) {
        viewModel.loadFirstPage()
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.loadNextPage()
        }
    }

    Scaffold(
        topBar = {
            ScreenAppBar(
                title = stringResource(R.string.artists),
                navigateUp = navigateUp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // The activity already keeps the content clear of the bottom bars and display cutouts.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        LazyVerticalGrid(
            state = gridState,
            columns = GridCells.Adaptive(dimensionResource(R.dimen.cover_grid_min_width)),
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            items(uiState.artists, key = { it.id }) { artist ->
                CoverCard(
                    name = artist.name,
                    imageUrl = artist.imageUrls.medium,
                    contentDescription = stringResource(R.string.artist_image),
                )
            }

            if (uiState.isLoading) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    LoadingIndicator()
                }
            }
        }

        uiState.alertMessage?.let { alertMessage ->
            ShowSnackbar(alertMessage, snackbarHostState, onAction = { viewModel.alertActionPerformed(it) }) {
                viewModel.alertMessageShown()
            }
        }
    }
}
