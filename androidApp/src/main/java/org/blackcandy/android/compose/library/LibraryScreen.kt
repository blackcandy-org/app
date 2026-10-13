package org.blackcandy.android.compose.library

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import org.blackcandy.android.R
import org.blackcandy.android.compose.ScreenAppBar

@Composable
fun LibraryScreen(
    navigateToAlbums: () -> Unit,
    navigateToArtists: () -> Unit,
    navigateToSongs: () -> Unit,
    navigateToPlaylists: () -> Unit,
) {
    Scaffold(
        topBar = {
            ScreenAppBar(title = stringResource(R.string.library))
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
        ) {
            LibraryItem(R.string.albums, R.drawable.baseline_album_24, navigateToAlbums)
            LibraryItem(R.string.artists, R.drawable.baseline_mic_24, navigateToArtists)
            LibraryItem(R.string.songs, R.drawable.baseline_music_note_24, navigateToSongs)
            LibraryItem(R.string.playlists, R.drawable.baseline_queue_music_24, navigateToPlaylists)
        }
    }
}

@Composable
private fun LibraryItem(
    @StringRes titleResId: Int,
    @DrawableRes iconResId: Int,
    onClick: () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        leadingContent = {
            Icon(
                painter = painterResource(iconResId),
                contentDescription = null,
            )
        },
        headlineContent = { Text(stringResource(titleResId)) },
    )
}
