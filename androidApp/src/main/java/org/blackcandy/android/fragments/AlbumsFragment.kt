package org.blackcandy.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireFragment
import org.blackcandy.android.compose.albums.AlbumsScreen
import org.blackcandy.android.databinding.FragmentAlbumsBinding

@HotwireDestinationDeepLink(uri = "hotwire://fragment/albums")
class AlbumsFragment : HotwireFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentAlbumsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentAlbumsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.albumsComposeView.setContent {
            Mdc3Theme {
                AlbumsScreen(
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
