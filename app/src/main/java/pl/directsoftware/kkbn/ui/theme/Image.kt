package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap


@Composable
@NonRestartableComposable
fun NetworkImage(image: ImageBitmap?, modifier: Modifier = Modifier) {
    image?.let {
        Image(bitmap = it, contentDescription = null, modifier = modifier)
    }
}