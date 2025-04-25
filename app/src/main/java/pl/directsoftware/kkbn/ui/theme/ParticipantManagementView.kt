package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import pl.directsoftware.kkbn.AppViewModel
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.common.Route

@Composable
fun ParticipantManagementScreen(
    viewModel: AppViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val isLoading = remember { mutableStateOf(true) }
    val participants = remember { mutableStateOf<List<EventHost>?>(null) }

    LaunchedEffect(Event::class) {
        participants.value = viewModel.getEventParticipants()
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (participants.value != null) {
            val rowHeight = 64.dp
            LazyColumn {
                items(participants.value!!) { participant ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                navController.navigate(
                                    Route.PARTICIPANTS_MANAGEMENT_BY_OBJECT.setObject(
                                        participant
                                    )
                                )
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
//                            Text(
//                                text = event.getTime(),
//                                style = MaterialTheme.typography.bodyMedium,
//                                modifier = Modifier
//                                    .weight(1f)
//                                    .padding(end = 8.dp)
//                            )

                            Column(
                                modifier = Modifier
                                    .weight(3f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = "${participant.firstName} ${participant.lastName}",
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = "Aktywny: " + (if (participant.active) "tak" else "nie") + " | Kod dostępu: ${participant.accessCode}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ParticipantManagementScreen(
    participant: EventHost,
    navController: NavController,
    onSave: (EventHost) -> Unit = {}
) {
    var active by remember { mutableStateOf(participant.active) }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(stringResource(R.string.edit_participant), style = MaterialTheme.typography.headlineSmall)

        Text(
            text = stringResource(R.string.label_full_name) + ": ${participant.firstName} ${participant.lastName}",
            style = MaterialTheme.typography.bodyLarge
        )

        HorizontalDivider(modifier = Modifier.padding(horizontal = 2.dp), color = Color.Transparent)

        Text(
            text = "Firma: ${participant.companyName}",
            style = MaterialTheme.typography.bodyLarge
        )

        SwitchBoolean(initialValue = active) {
            active = it
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(stringResource(R.string.cancel))
            }

            Button(
                onClick = {
                    onSave(
                        participant.copy(
                            active = active
                        )
                    )
                },
                modifier = Modifier
                    .weight(1f)
            ) {
                Text(stringResource(R.string.save))
            }
        }
    }

}