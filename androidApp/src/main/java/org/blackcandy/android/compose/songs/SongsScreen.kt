package org.blackcandy.android.compose.songs

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import coil.compose.AsyncImage
import org.blackcandy.android.R
import org.blackcandy.android.compose.LoadingIndicator
import org.blackcandy.android.compose.ScreenAppBar
import org.blackcandy.android.utils.SnackbarUtil.Companion.ShowSnackbar
import org.blackcandy.shared.models.Song
import org.blackcandy.shared.viewmodels.SongsViewModel
import org.koin.androidx.compose.koinViewModel

private const val LOAD_MORE_THRESHOLD = 6

@Composable
fun SongsScreen(
    navigateUp: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: SongsViewModel = koinViewModel(),
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
                title = stringResource(R.string.songs),
                navigateUp = navigateUp,
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { innerPadding ->
        LazyColumn(
            state = listState,
            modifier =
                Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
        ) {
            items(uiState.songs, key = { it.id }) { song ->
                SongItem(
                    song = song,
                    onClick = { viewModel.playNow(song.id) },
                    onPlayNext = { viewModel.playNext(song.id) },
                    onPlayLast = { viewModel.playLast(song.id) },
                )
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
private fun SongItem(
    song: Song,
    onClick: () -> Unit,
    onPlayNext: () -> Unit,
    onPlayLast: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            AsyncImage(
                model = song.albumImageUrls.small,
                contentDescription = stringResource(R.string.album_cover),
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .size(dimensionResource(R.dimen.song_cover_size))
                        .clip(RoundedCornerShape(dimensionResource(R.dimen.corner_radius_small))),
            )
        },
        headlineContent = {
            Text(
                text = song.name,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        supportingContent = {
            Text(
                text = song.artistName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        trailingContent = {
            SongMenu(onPlayNext = onPlayNext, onPlayLast = onPlayLast)
        },
    )
}

@Composable
private fun SongMenu(
    onPlayNext: () -> Unit,
    onPlayLast: () -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box {
        IconButton(onClick = { expanded = true }) {
            Icon(
                painter = painterResource(R.drawable.baseline_more_vert_24),
                contentDescription = stringResource(R.string.more),
            )
        }

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            DropdownMenuItem(
                text = { Text(stringResource(R.string.play_next)) },
                onClick = {
                    expanded = false
                    onPlayNext()
                },
            )

            DropdownMenuItem(
                text = { Text(stringResource(R.string.play_last)) },
                onClick = {
                    expanded = false
                    onPlayLast()
                },
            )
        }
    }
}
