package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun TextWithShadow(
    text: String,
    modifier: Modifier
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.DarkGray,
        modifier = modifier
            .fillMaxWidth()
            .offset(
                x = 2.dp,
                y = 2.dp
            )
            .alpha(0.75f),
        fontSize = 24.sp
    )
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = modifier,
        fontSize = 24.sp
    )
}

@Composable
fun ImageWithText(painter: Painter, text: String, contentDescription: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        TextWithShadow(
            text = text,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        )
    }
}