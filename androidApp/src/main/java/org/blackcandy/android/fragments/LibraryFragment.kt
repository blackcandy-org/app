package org.blackcandy.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireFragment
import org.blackcandy.android.compose.library.LibraryScreen
import org.blackcandy.android.databinding.FragmentLibraryBinding
import java.net.URI

@HotwireDestinationDeepLink(uri = "hotwire://fragment/library")
class LibraryFragment : HotwireFragment() {
    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentLibraryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentLibraryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.libraryComposeView.setContent {
            Mdc3Theme {
                LibraryScreen(
                    canNavigateBack = !navigator.isAtStartDestination(),
                    navigateUp = { navigator.pop() },
                    navigateTo = { path -> navigator.route(URI(location).resolve(path).toString()) },
                )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
