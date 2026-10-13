package org.blackcandy.android

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.launch
import org.blackcandy.android.compose.player.MiniPlayer
import org.blackcandy.android.compose.player.PlayerScreen

private const val PLAYER_TRANSITION_THRESHOLD = 0.15f

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(windowSizeClass: WindowSizeClass) {
    val navController = rememberNavController()
    val scaffoldState = rememberBottomSheetScaffoldState()
    val sheetState = scaffoldState.bottomSheetState
    val scope = rememberCoroutineScope()
    val density = LocalDensity.current

    val isCompactWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact
    val isExpandedWidth = windowSizeClass.widthSizeClass == WindowWidthSizeClass.Expanded
    var navigationBarHeight by remember { mutableStateOf(0.dp) }
    val sheetPeekHeight =
        dimensionResource(R.dimen.mini_player_height) +
            if (isCompactWidth) navigationBarHeight else WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val sheetHeight =
            if (isCompactWidth) maxHeight else min(maxHeight, dimensionResource(R.dimen.player_bottom_sheet_max_height))
        val collapsedOffset = with(density) { (maxHeight - sheetPeekHeight).toPx() }
        val expandedOffset = with(density) { (maxHeight - sheetHeight).toPx() }

        val openFraction = {
            if (sheetState.hasPartiallyExpandedState) {
                ((collapsedOffset - sheetState.requireOffset()) / (collapsedOffset - expandedOffset)).coerceIn(0f, 1f)
            } else {
                0f
            }
        }

        BottomSheetScaffold(
            scaffoldState = scaffoldState,
            sheetPeekHeight = sheetPeekHeight,
            sheetMaxWidth = Dp.Unspecified,
            sheetDragHandle = null,
            sheetContent = {
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(sheetHeight),
                ) {
                    if (sheetState.currentValue == SheetValue.Expanded || sheetState.targetValue == SheetValue.Expanded) {
                        Box(
                            modifier =
                                Modifier.graphicsLayer {
                                    alpha = ((openFraction() - PLAYER_TRANSITION_THRESHOLD) / PLAYER_TRANSITION_THRESHOLD).coerceIn(0f, 1f)
                                },
                        ) {
                            PlayerScreen(windowSizeClass = windowSizeClass)
                        }
                    }

                    if (sheetState.currentValue == SheetValue.PartiallyExpanded ||
                        sheetState.targetValue == SheetValue.PartiallyExpanded
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .graphicsLayer {
                                        alpha = (1f - openFraction() / PLAYER_TRANSITION_THRESHOLD).coerceIn(0f, 1f)
                                    }.clickable { scope.launch { sheetState.expand() } },
                        ) {
                            MiniPlayer(windowSizeClass = windowSizeClass)
                        }
                    }
                }
            },
        ) { contentPadding ->
            Row(modifier = Modifier.windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))) {
                when (windowSizeClass.widthSizeClass) {
                    WindowWidthSizeClass.Medium -> MainNavigationRail(navController)
                    WindowWidthSizeClass.Expanded -> MainExpandedNavigationRail(navController)
                }

                if (!isCompactWidth) {
                    VerticalDivider()
                }

                MainNavHost(
                    navController = navController,
                    usesExpandedNavigationRail = isExpandedWidth,
                    modifier =
                        Modifier
                            .padding(contentPadding)
                            .consumeWindowInsets(contentPadding),
                )
            }
        }

        if (isCompactWidth) {
            MainNavigationBar(
                navController = navController,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .onSizeChanged { navigationBarHeight = with(density) { it.height.toDp() } }
                        .graphicsLayer {
                            val fraction = openFraction()
                            translationY = size.height * fraction
                            alpha = 1f - fraction
                        },
            )
        }
    }
}

@Composable
private fun MainNavigationBar(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationBar(modifier = modifier) {
        MainTab.entries.forEach { tab ->
            NavigationBarItem(
                selected = currentDestination.isInTab(tab),
                onClick = { navController.navigateToTab(tab) },
                icon = { Icon(painterResource(tab.iconResId), contentDescription = null) },
                label = { Text(stringResource(tab.titleResId)) },
            )
        }
    }
}

@Composable
private fun MainNavigationRail(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    NavigationRail {
        Spacer(modifier = Modifier.weight(1f))

        MainTab.entries.forEach { tab ->
            NavigationRailItem(
                selected = currentDestination.isInTab(tab),
                onClick = { navController.navigateToTab(tab) },
                icon = { Icon(painterResource(tab.iconResId), contentDescription = null) },
                label = { Text(stringResource(tab.titleResId)) },
            )
        }

        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun MainExpandedNavigationRail(navController: NavHostController) {
    val currentDestination = navController.currentBackStackEntryAsState().value?.destination

    WideNavigationRail(state = rememberWideNavigationRailState(WideNavigationRailValue.Expanded)) {
        WideNavigationRailItem(
            selected = currentDestination.isInTab(MainTab.HomeTab),
            onClick = { navController.navigateToTab(MainTab.HomeTab) },
            icon = { Icon(painterResource(MainTab.HomeTab.iconResId), contentDescription = null) },
            label = { Text(stringResource(MainTab.HomeTab.titleResId)) },
            railExpanded = true,
        )

        Text(
            text = stringResource(R.string.library),
            modifier = Modifier.padding(start = 36.dp, top = 20.dp, bottom = 8.dp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            style = MaterialTheme.typography.titleSmall,
        )

        WideNavigationRailItem(
            selected = currentDestination?.route == MainRoute.Albums.name,
            onClick = { navController.navigateToLibraryItem(MainRoute.Albums) },
            icon = { Icon(painterResource(R.drawable.baseline_album_24), contentDescription = null) },
            label = { Text(stringResource(R.string.albums)) },
            railExpanded = true,
        )

        WideNavigationRailItem(
            selected = currentDestination?.route == MainRoute.Artists.name,
            onClick = { navController.navigateToLibraryItem(MainRoute.Artists) },
            icon = { Icon(painterResource(R.drawable.baseline_mic_24), contentDescription = null) },
            label = { Text(stringResource(R.string.artists)) },
            railExpanded = true,
        )

        WideNavigationRailItem(
            selected = currentDestination?.route == MainRoute.Songs.name,
            onClick = { navController.navigateToLibraryItem(MainRoute.Songs) },
            icon = { Icon(painterResource(R.drawable.baseline_music_note_24), contentDescription = null) },
            label = { Text(stringResource(R.string.songs)) },
            railExpanded = true,
        )

        WideNavigationRailItem(
            selected = currentDestination?.route == MainRoute.Playlists.name,
            onClick = { navController.navigateToLibraryItem(MainRoute.Playlists) },
            icon = { Icon(painterResource(R.drawable.baseline_queue_music_24), contentDescription = null) },
            label = { Text(stringResource(R.string.playlists)) },
            railExpanded = true,
        )
    }
}
