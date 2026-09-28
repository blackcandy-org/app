package org.blackcandy.android.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import com.google.android.material.snackbar.Snackbar
import dev.hotwire.navigation.destinations.HotwireDestinationDeepLink
import dev.hotwire.navigation.fragments.HotwireFragment
import kotlinx.coroutines.launch
import org.blackcandy.android.R
import org.blackcandy.android.compose.artists.ArtistsScreen
import org.blackcandy.android.databinding.FragmentArtistsBinding
import org.blackcandy.android.utils.SnackbarUtil.Companion.showSnackbar
import org.blackcandy.shared.viewmodels.ArtistsViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

@HotwireDestinationDeepLink(uri = "hotwire://fragment/artists")
class ArtistsFragment : HotwireFragment() {
    private val viewModel: ArtistsViewModel by viewModel()

    @Suppress("ktlint:standard:backing-property-naming")
    private var _binding: FragmentArtistsBinding? = null
    private val binding get() = _binding!!

    private var errorSnackbar: Snackbar? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect {
                    if (it.alertMessage != null) {
                        errorSnackbar =
                            showSnackbar(
                                activity = requireActivity(),
                                message = it.alertMessage!!,
                                actionText = getString(R.string.retry),
                                onAction = { viewModel.retry() },
                            ) {
                                viewModel.alertMessageShown()
                            }
                    }
                }
            }
        }
    }

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

        toolbarForNavigation()?.setTitle(R.string.artists)

        binding.artistsComposeView.setContent {
            Mdc3Theme {
                ArtistsScreen(viewModel = viewModel)
            }
        }
    }

    override fun onStop() {
        super.onStop()

        // The snackbar is attached to the activity, not to this fragment's view.
        errorSnackbar?.dismiss()
        errorSnackbar = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
