package pl.directsoftware.kkbn.ui.theme

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.directsoftware.kkbn.AppViewModel
import pl.directsoftware.kkbn.R

@Composable
fun GrayButton(
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier,
    loader: Boolean = false,
    onClick: () -> Unit,
) {
    var isLoading by remember { mutableStateOf(false) }
    Button(
        onClick = {
            if (loader) isLoading = true
            onClick()
        },
        modifier = modifier,
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(0xFFE0E0E0),
            contentColor = Color.Black
        ),
        shape = RoundedCornerShape(12.dp),
        elevation = ButtonDefaults.buttonElevation(
            defaultElevation = 2.dp,
            pressedElevation = 6.dp
        ),
        enabled = enabled
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = Color.Black,
                    modifier = Modifier.size(16.dp),
                    strokeWidth = 2.dp
                )
            }
            Text(
                text = if (isLoading) stringResource(R.string.please_wait) else text,
                style = MaterialTheme.typography.labelLarge
            )
        }
    }
}

@Composable
fun SyncButton(viewModel: AppViewModel) {
    var rotation by remember { mutableFloatStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = ""
    )

    Icon(
        imageVector = Icons.Default.Sync,
        contentDescription = null,
        modifier = Modifier
            .rotate(animatedRotation)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                rotation += 360f
                viewModel.refresh()
            }
    )
}