package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Partner

@Composable
fun PartnersScreen(
    loadData: suspend () -> List<Partner>?,
    onClick: (Partner) -> Unit
) {
    val isLoading = remember { mutableStateOf(true) }
    val partners = remember { mutableStateOf<List<Partner>?>(null) }

    LaunchedEffect(Partner::class) {
        partners.value = loadData()
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
                .padding(8.dp)
        ) {
            if (partners.value != null) {
                items(partners.value!!) { partner ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp)
                            .clickable(
                                onClick = {
                                    onClick(partner)
                                }
                            ),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                            modifier = Modifier
                                .padding(4.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(0),
                                    clip = true
                                )
                            ,
                            model = "https://kkbn.pl/${partner.avatar}",
                            contentDescription = null,
                        )
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}

@Composable
fun PartnerScreen(partner: Partner) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(4.dp)
            ,
            model = "https://kkbn.pl/${partner.avatar}",
            contentDescription = null,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            modifier = Modifier
                .padding(4.dp)
            ,
            text = partner.name ?: "",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        if (partner.url != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Hyperlink(partner.url, R.string.visit_partner_site)
        }
    }
}