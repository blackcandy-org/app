package org.blackcandy.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireFragment
import org.blackcandy.android.compose.artists.ArtistsScreen
import org.blackcandy.android.databinding.FragmentArtistsBinding

@HotwireDestinationDeepLink(uri = "hotwire://fragment/artists")
class ArtistsFragment : HotwireFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentArtistsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.artistsComposeView.setContent {
            Mdc3Theme {
                ArtistsScreen(
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
