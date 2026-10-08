package org.blackcandy.shared

import org.blackcandy.shared.di.appModule
import org.blackcandy.shared.viewmodels.AlbumsViewModel
import org.blackcandy.shared.viewmodels.ArtistsViewModel
import org.blackcandy.shared.viewmodels.HomeViewModel
import org.blackcandy.shared.viewmodels.LoginViewModel
import org.blackcandy.shared.viewmodels.MainViewModel
import org.blackcandy.shared.viewmodels.MusicServiceViewModel
import org.blackcandy.shared.viewmodels.PlayerViewModel
import org.blackcandy.shared.viewmodels.PlaylistsViewModel
import org.blackcandy.shared.viewmodels.SongsViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.get
import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(appModule())
    }
}

class KoinHelper : KoinComponent {
    fun getMainViewModel(): MainViewModel = get()

    fun getLoginViewModel(): LoginViewModel = get()

    fun getPlayerViewModel(): PlayerViewModel = get()

    fun getMusicServiceViewModel(): MusicServiceViewModel = get()

    fun getAlbumsViewModel(): AlbumsViewModel = get()

    fun getArtistsViewModel(): ArtistsViewModel = get()

    fun getSongsViewModel(): SongsViewModel = get()

    fun getPlaylistsViewModel(): PlaylistsViewModel = get()

    fun getHomeViewModel(): HomeViewModel = get()
}
