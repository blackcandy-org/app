package org.blackcandy.android.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import org.blackcandy.android.R

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .padding(dimensionResource(R.dimen.padding_small)),
        contentAlignment = Alignment.Center,
    ) {
        CircularProgressIndicator()
    }
}
