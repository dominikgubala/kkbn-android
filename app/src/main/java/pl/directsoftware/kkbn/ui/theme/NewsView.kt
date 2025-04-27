package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import pl.directsoftware.kkbn.api.data.News
import pl.directsoftware.kkbn.common.HtmlData
import pl.directsoftware.kkbn.repository.NewsRepository
import pl.directsoftware.kkbn.utils.FileHelper.loadImageFromUrl

@Composable
fun NewsScreen(
    loadData: suspend () -> List<News>?,
    onClick: (News) -> Unit = {},
) {
    val isLoading = remember { mutableStateOf(true) }
    val newsList = remember { mutableStateOf<List<News>?>(null) }

    LaunchedEffect(NewsRepository::class) {
        newsList.value = loadData()
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            if (newsList.value != null) {
                items(newsList.value!!) { news ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .verticalScroll(rememberScrollState())
                            .clickable(
                                onClick = { onClick(news) }
                            )
                        ,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NetworkImage(
                            image = news.bitmap,
                            modifier = Modifier
                                .width(90.dp)
                                .height(90.dp)
                                .padding(4.dp)
                        )
                        Text(
                            text = news.title,
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                    )
                }
            }
        }
    }
}

@Composable
fun NewsByIdScreen(
    onLoad: suspend () -> News?,
) {
    val isLoading = remember { mutableStateOf(true) }
    val news = remember { mutableStateOf<News?>(null) }

    val data = remember { mutableStateOf<HtmlData?>(null) }

    LaunchedEffect(News::class) {
        news.value = onLoad()
        val htmlData = HtmlData(news.value!!.content)

        val urlList = mutableMapOf<Int,String>()

        for ((index, _) in htmlData.parts.withIndex()) {
            if (index < htmlData.matches.size) {
                urlList[index] = "https://kkbn.pl/${htmlData.matches[index]}"
            }
        }

        for ((key, url) in urlList) {
            htmlData.bitmapList[key] = loadImageFromUrl(url)
        }
        data.value = htmlData
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                )
                Text(text = news.value!!.date.getDateStr())
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = news.value!!.title,
                fontWeight = FontWeight.SemiBold,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            HtmlContent(
                data.value!!
            )
        }
    }
}