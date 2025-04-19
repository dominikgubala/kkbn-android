package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.directsoftware.kkbn.api.data.Committee
import pl.directsoftware.kkbn.repository.CommitteeRepository
import pl.directsoftware.kkbn.utils.FileHelper.loadImageFromUrl

@Composable
fun CommitteeScreen(navController: NavController) {

    val isLoading = remember { mutableStateOf(true) }
    val avatarList = remember { mutableMapOf<Committee,ImageBitmap?>() }

    LaunchedEffect(Committee::class) {
        val committeeResource = CommitteeRepository.getCommittee()
        committeeResource?.forEach{ committee ->
            avatarList[committee] =
                if (committee.avatar != null)
                    loadImageFromUrl("https://kkbn.pl/${committee.avatar}")
                else null
        }
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
            items(avatarList.entries.toList()) { obj ->
                val committee = obj.key
                val avatar = obj.value
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    if (avatar != null) {
                        NetworkImage(
                            image = avatar,
                            modifier = Modifier
                                .height(200.dp)
                                .padding(4.dp)
                                .align(Alignment.CenterHorizontally)
                        )
                    }
                    committee.role?.let {
                        Text(
                            text = it,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                textDecoration = TextDecoration.Underline
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                    Text(
                        text = committee.fullName,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = committee.work,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }
        }
    }
}