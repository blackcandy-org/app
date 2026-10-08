package org.blackcandy.android.compose.albums

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
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
import org.blackcandy.shared.viewmodels.AlbumsViewModel
import org.koin.androidx.compose.koinViewModel

private const val LOAD_MORE_THRESHOLD = 6

@Composable
fun AlbumsScreen(
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: AlbumsViewModel = koinViewModel(),
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
                title = stringResource(R.string.albums),
                navigateUp = navigateUp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
            items(uiState.albums, key = { it.id }) { album ->
                CoverCard(
                    name = album.name,
                    imageUrl = album.imageUrls.medium,
                    contentDescription = stringResource(R.string.album_cover),
                    subtitle = album.artistName,
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
