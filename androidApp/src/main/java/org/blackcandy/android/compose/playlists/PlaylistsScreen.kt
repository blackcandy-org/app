package org.blackcandy.android.compose.playlists

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import org.blackcandy.android.R
import org.blackcandy.android.compose.LoadingIndicator
import org.blackcandy.android.compose.ScreenAppBar
import org.blackcandy.android.utils.SnackbarUtil.Companion.ShowSnackbar
import org.blackcandy.shared.models.Playlist
import org.blackcandy.shared.viewmodels.PlaylistsViewModel
import org.koin.androidx.compose.koinViewModel

private const val LOAD_MORE_THRESHOLD = 6

@Composable
fun PlaylistsScreen(
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: PlaylistsViewModel = koinViewModel(),
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()

    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleIndex =
                listState.layoutInfo.visibleItemsInfo
                    .lastOrNull()
                    ?.index ?: return@derivedStateOf false

            lastVisibleIndex >= listState.layoutInfo.totalItemsCount - LOAD_MORE_THRESHOLD
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
                title = stringResource(R.string.playlists),
                navigateUp = navigateUp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        // The activity already keeps the content clear of the bottom bars and display cutouts.
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            items(uiState.playlists, key = { it.id }) { playlist ->
                PlaylistItem(playlist = playlist)
            }

            if (uiState.isLoading) {
                item {
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

@Composable
private fun PlaylistItem(playlist: Playlist) {
    ListItem(
        leadingContent = {
            if (playlist.isFavorite) {
                Icon(
                    painter = painterResource(R.drawable.baseline_favorite_24),
                    contentDescription = null,
                    tint = Color.Red,
                )
            } else {
                Icon(
                    painter = painterResource(R.drawable.baseline_queue_music_24),
                    contentDescription = null,
                )
            }
        },
        headlineContent = {
            Text(
                text = playlist.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
    )
}
