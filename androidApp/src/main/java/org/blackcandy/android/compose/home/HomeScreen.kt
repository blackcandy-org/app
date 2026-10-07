package org.blackcandy.android.compose.home

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import org.blackcandy.android.R
import org.blackcandy.android.compose.CoverCard
import org.blackcandy.android.compose.ScreenAppBar
import org.blackcandy.android.utils.SnackbarUtil.Companion.ShowSnackbar
import org.blackcandy.shared.models.Album
import org.blackcandy.shared.viewmodels.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: HomeViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.load()
    }

    Scaffold(
        topBar = {
            ScreenAppBar(
                title = stringResource(R.string.home),
                canNavigateBack = canNavigateBack,
                navigateUp = navigateUp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // The activity already keeps the content clear of the bottom bars and display cutouts.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(dimensionResource(R.dimen.cover_grid_min_width)),
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
            contentPadding = PaddingValues(dimensionResource(R.dimen.padding_small)),
            horizontalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
            verticalArrangement = Arrangement.spacedBy(dimensionResource(R.dimen.padding_small)),
        ) {
            albumSection(R.string.recently_played, uiState.recentlyPlayedAlbums)
            albumSection(R.string.recently_added, uiState.recentlyAddedAlbums)
        }

        uiState.alertMessage?.let { alertMessage ->
            ShowSnackbar(alertMessage, snackbarHostState, onAction = { viewModel.alertActionPerformed(it) }) {
                viewModel.alertMessageShown()
            }
        }
    }
}

private fun LazyGridScope.albumSection(
    @StringRes titleResId: Int,
    albums: List<Album>,
) {
    if (albums.isEmpty()) return

    item(span = { GridItemSpan(maxLineSpan) }) {
        Text(
            text = stringResource(titleResId),
            style = MaterialTheme.typography.titleMedium,
        )
    }

    items(albums) { album ->
        CoverCard(
            name = album.name,
            imageUrl = album.imageUrls.medium,
            contentDescription = stringResource(R.string.album_cover),
            subtitle = album.artistName,
        )
    }
}
