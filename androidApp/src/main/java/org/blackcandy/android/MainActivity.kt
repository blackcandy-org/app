package org.blackcandy.android

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.accompanist.themeadapter.material3.Mdc3Theme
import kotlinx.coroutines.launch
import org.blackcandy.shared.viewmodels.MainViewModel
import org.blackcandy.shared.viewmodels.MusicServiceViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {
    private val viewModel: MainViewModel by viewModel()

    private val musicServiceViewModel: MusicServiceViewModel by viewModel()

    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (requireLogin()) {
            switchToLoginActivity()
            return
        }

        enableEdgeToEdge()
        musicServiceViewModel.setupMusicServiceController()

        setContent {
            Mdc3Theme {
                MainScreen(windowSizeClass = calculateWindowSizeClass(this))
            }
        }
    }

    override fun onRestart() {
        super.onRestart()

        musicServiceViewModel.getCurrentPlaylist()
    }

    private fun requireLogin(): Boolean {
        viewModel.currentUser ?: return true

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.currentUserFlow.collect {
                    if (it == null) {
                        switchToLoginActivity()
                    }
                }
            }
        }

        return false
    }

    private fun switchToLoginActivity() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)

        startActivity(intent)
    }
}
