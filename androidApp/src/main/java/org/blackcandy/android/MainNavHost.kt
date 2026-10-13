package org.blackcandy.android

import androidx.activity.compose.BackHandler
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
import androidx.navigation.compose.currentBackStackEntryAsState
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
) {
    HomeTab(R.string.home, R.drawable.baseline_home_24),
    LibraryTab(R.string.library, R.drawable.baseline_library_music_24),
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
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination
    val navigateUp: (() -> Unit)? = if (usesExpandedNavigationRail) null else navController::navigateUp

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
                AlbumsScreen(navigateUp = navigateUp)
            }

            composable(MainRoute.Artists.name) {
                ArtistsScreen(navigateUp = navigateUp)
            }

            composable(MainRoute.Songs.name) {
                SongsScreen(navigateUp = navigateUp)
            }

            composable(MainRoute.Playlists.name) {
                PlaylistsScreen(navigateUp = navigateUp)
            }
        }
    }

    BackHandler(enabled = usesExpandedNavigationRail && currentDestination.isInTab(MainTab.LibraryTab)) {
        navController.navigateToTab(MainTab.HomeTab)
    }

    LaunchedEffect(usesExpandedNavigationRail) {
        if (usesExpandedNavigationRail && navController.currentDestination?.route == MainRoute.Library.name) {
            navController.navigateToLibraryItem(MainRoute.Albums)
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

fun NavController.navigateToLibraryItem(route: MainRoute) {
    navigateToTab(MainTab.LibraryTab)
    navigate(route.name) {
        popUpTo(MainRoute.Library.name)
    }
}

fun NavDestination?.isInTab(tab: MainTab): Boolean = this?.hierarchy?.any { it.route == tab.name } == true
