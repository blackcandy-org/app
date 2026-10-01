package org.blackcandy.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireFragment
import org.blackcandy.android.compose.playlists.PlaylistsScreen
import org.blackcandy.android.databinding.FragmentPlaylistsBinding

@HotwireDestinationDeepLink(uri = "hotwire://fragment/playlists")
class PlaylistsFragment : HotwireFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentPlaylistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentPlaylistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.playlistsComposeView.setContent {
            Mdc3Theme {
                PlaylistsScreen(
                    canNavigateBack = !navigator.isAtStartDestination(),
                    navigateUp = { navigator.pop() },
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
