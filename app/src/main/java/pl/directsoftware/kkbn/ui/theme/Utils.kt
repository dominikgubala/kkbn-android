package pl.directsoftware.kkbn.ui.theme

import android.widget.Toast
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.utils.FileHelper.downloadPdfToCache
import pl.directsoftware.kkbn.utils.FileHelper.openPdf

@Composable
fun Hyperlink(link: String, textId: Int) {
    val text = stringResource(textId)
    val annotatedLinkString: AnnotatedString = remember {
        buildAnnotatedString {
            withLink(LinkAnnotation.Url(url = link)) {
                append(text)
            }
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp)
            .padding(horizontal = 8.dp)
        ,
    ) {
        Icon(
            painter = painterResource(R.drawable.link),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = annotatedLinkString,
            style = TextStyle(
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
            ),
            modifier = Modifier
                .weight(1f),
        )
    }
}

@Composable
fun DownloadAndOpenPdfButton(
    url: String,
    filename: String
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    Button(onClick = {
        coroutineScope.launch {
            try {
                val file = downloadPdfToCache(context, url, filename)
                openPdf(context, file)
            } catch (e: Exception) {
                Toast.makeText(context, "Błąd podczas pobierania", Toast.LENGTH_SHORT).show()
            }
        }
    }) {
        Icon(Icons.Default.Create, contentDescription = null) // todo: zmiana na ikone wg pliku
        Spacer(Modifier.width(8.dp))
        Text("Zobacz PDF")
    }
}

@Composable
fun ProgressIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}