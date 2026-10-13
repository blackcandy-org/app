package org.blackcandy.android

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import org.blackcandy.android.compose.albums.AlbumsScreen
import org.blackcandy.android.compose.artists.ArtistsScreen
import org.blackcandy.android.compose.home.HomeScreen
import org.blackcandy.android.compose.library.LibraryScreen
import org.blackcandy.android.compose.playlists.PlaylistsScreen
import org.blackcandy.android.compose.songs.SongsScreen

enum class MainTab(
    @StringRes val titleResId: Int,
    @DrawableRes val iconResId: Int,
    val libraryRoute: MainRoute? = null,
) {
    HomeTab(R.string.home, R.drawable.baseline_home_24),
    LibraryTab(R.string.library, R.drawable.baseline_library_music_24),
    AlbumsTab(R.string.albums, R.drawable.baseline_album_24, MainRoute.Albums),
    ArtistsTab(R.string.artists, R.drawable.baseline_mic_24, MainRoute.Artists),
    SongsTab(R.string.songs, R.drawable.baseline_music_note_24, MainRoute.Songs),
    PlaylistsTab(R.string.playlists, R.drawable.baseline_queue_music_24, MainRoute.Playlists),
}

enum class MainRoute {
    Home,
    Library,
    Albums,
    Artists,
    Songs,
    Playlists,
}

@Composable
fun MainNavHost(
    navController: NavHostController,
    usesExpandedNavigationRail: Boolean,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = MainTab.HomeTab.name,
        modifier = modifier,
    ) {
        navigation(route = MainTab.HomeTab.name, startDestination = MainRoute.Home.name) {
            composable(MainRoute.Home.name) {
                HomeScreen()
            }
        }

        navigation(route = MainTab.LibraryTab.name, startDestination = MainRoute.Library.name) {
            composable(MainRoute.Library.name) {
                LibraryScreen(
                    navigateToAlbums = { navController.navigate(MainRoute.Albums.name) },
                    navigateToArtists = { navController.navigate(MainRoute.Artists.name) },
                    navigateToSongs = { navController.navigate(MainRoute.Songs.name) },
                    navigateToPlaylists = { navController.navigate(MainRoute.Playlists.name) },
                )
            }

            composable(MainRoute.Albums.name) {
                AlbumsScreen(navigateUp = { navController.navigateUp() })
            }

            composable(MainRoute.Artists.name) {
                ArtistsScreen(navigateUp = { navController.navigateUp() })
            }

            composable(MainRoute.Songs.name) {
                SongsScreen(navigateUp = { navController.navigateUp() })
            }

            composable(MainRoute.Playlists.name) {
                PlaylistsScreen(navigateUp = { navController.navigateUp() })
            }
        }

        composable(MainTab.AlbumsTab.name) {
            AlbumsScreen(navigateUp = null)
        }

        composable(MainTab.ArtistsTab.name) {
            ArtistsScreen(navigateUp = null)
        }

        composable(MainTab.SongsTab.name) {
            SongsScreen(navigateUp = null)
        }

        composable(MainTab.PlaylistsTab.name) {
            PlaylistsScreen(navigateUp = null)
        }
    }

    LaunchedEffect(usesExpandedNavigationRail) {
        val currentDestination = navController.currentDestination
        val libraryRoute = MainTab.entries.firstOrNull { currentDestination.isInTab(it) }?.libraryRoute

        if (!usesExpandedNavigationRail && libraryRoute != null) {
            navController.navigateToTab(MainTab.LibraryTab)
            navController.navigate(libraryRoute.name) {
                popUpTo(MainRoute.Library.name)
            }
        } else if (usesExpandedNavigationRail && currentDestination.isInTab(MainTab.LibraryTab)) {
            navController.navigateToTab(
                MainTab.entries.firstOrNull { it.libraryRoute?.name == currentDestination?.route } ?: MainTab.AlbumsTab,
            )
        }
    }
}

// Each tab keeps its own back stack, saved while another tab is showing.
fun NavController.navigateToTab(tab: MainTab) {
    navigate(tab.name) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}

fun NavDestination?.isInTab(tab: MainTab): Boolean = this?.hierarchy?.any { it.route == tab.name } == true
