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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.Profile

@Composable
fun YourDataScreen(
    loadData: suspend () -> Profile?,
    goToEvent: (Event) -> Unit = {},
) {
    val isLoading = remember { mutableStateOf(true) }
    val profile = remember { mutableStateOf<Profile?>(null) }

    LaunchedEffect(Profile::class) {
        profile.value = loadData()
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (profile.value != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.title_your_data),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))

                DataRow(
                    labelId = R.string.full_name,
                    value = "${profile.value!!.firstName} ${profile.value!!.lastName}"
                )

                Spacer(modifier = Modifier.height(8.dp))

                DataRow(
                    labelId = R.string.company,
                    value = profile.value!!.companyName
                )

                Spacer(modifier = Modifier.height(8.dp))

                DataRow(
                    labelId = R.string.email_address,
                    value = profile.value!!.email
                )

                Spacer(modifier = Modifier.height(8.dp))

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )

                Spacer(modifier = Modifier.height(8.dp))

                if (profile.value!!.hostEvents?.isNotEmpty() == true) {
                    DataRow(labelId = R.string.my_workshops)

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        for (event in profile.value!!.hostEvents!!) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        goToEvent(event)
                                    }
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${event.date} | ${event.dateTimeBegin} - ${event.dateTimeEnd}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )

                                Spacer(modifier = Modifier.height(2.dp))
                                val participants = stringResource(id = R.string.label_participants, event.currentParticipants)
                                val availablePlaces = stringResource(id = R.string.label_available_places, event.getAvailablePlaces())
                                Text(
                                    text = "$participants | $availablePlaces",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (profile.value!!.events?.isNotEmpty() == true) {
                    DataRow(labelId = R.string.workshops)

                    Spacer(modifier = Modifier.height(8.dp))

                    Column(
                        modifier = Modifier.padding(start = 16.dp)
                    ) {
                        for (event in profile.value!!.events!!) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                                    .clickable {
                                        goToEvent(event)
                                    }
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "${event.date} | ${event.dateTimeBegin} - ${event.dateTimeEnd}",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.Light,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataRow(labelId: Int, value: String? = null) {
    Row {
        Text(
            text = stringResource(labelId) + ":",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Light,
            modifier = Modifier
                .padding(4.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))

        Text(
            text = value ?: "",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Normal,
            modifier = Modifier
                .padding(4.dp)
        )
    }
}