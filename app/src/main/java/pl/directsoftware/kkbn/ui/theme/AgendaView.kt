package pl.directsoftware.kkbn.ui.theme

import android.widget.Toast
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.StateFlow
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventDay
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.EventParticipant
import pl.directsoftware.kkbn.common.HtmlData
import pl.directsoftware.kkbn.utils.SessionManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgendaScreen(
    loadData: suspend () -> List<EventDay>?,
    goToEvent: (Event) -> Unit = {},
) {
    val isLoading = remember { mutableStateOf(true) }
    val events = remember { mutableStateOf<List<EventDay>?>(null) }

    LaunchedEffect(EventDay::class) {
        events.value = loadData()
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (events.value == null) return

        var selectedFilter by remember { mutableStateOf(events.value!!.first()) }
        var expanded by remember { mutableStateOf(false) }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    value = selectedFilter.description,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.choose_date)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true)
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    events.value!!.forEach { event ->
                        DropdownMenuItem(
                            text = { Text(event.description) },
                            onClick = {
                                selectedFilter = event
                                expanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            EventDayScreen(
                day = selectedFilter,
                onClick = {
                    goToEvent(it)
                }
            )
        }
    }
}

@Composable
fun EventDayScreen(
    day: EventDay,
    onClick: (Event) -> Unit = {},
) {
    val rowHeight = 64.dp
    LazyColumn {
        items(day.events) { event ->
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(rowHeight)
                        .padding(horizontal = 8.dp)
                        .clickable {
                            onClick(event)
                        },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        Modifier
                            .padding(
                                horizontal = 4.dp,
                            )
                            .fillMaxWidth(0.2f)
                    ) {
                        Text(
                            text = event.dateTimeBegin ?: "",
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                        Text(
                            text = event.getDuration(),
                            style = TextStyle(
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = event.title,
                        style = TextStyle(
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface,
                        ),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 2.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                )
            }
        }
    }
}

@Composable
fun EventScreen(
    event: Event,
    isLoggedIn: StateFlow<Boolean>,
    isRegistered: suspend () -> Boolean,
    onRegister: (Int) -> Unit,
    onUnregister: (Int) -> Unit,
    loadParticipants: (Int) -> List<EventParticipant>?,
    onDeleteParticipant: (Int, Int) -> Boolean,
) {
    val isLoading = remember { mutableStateOf(true) }
    val registered = remember { mutableStateOf(false) }
    val participants = remember { mutableStateListOf<EventParticipant>() }
    val isLogged = isLoggedIn.collectAsState()
    val canRegister = event.host == null || event.host.email != SessionManager.identifier!!

    LaunchedEffect(EventDay::class) {
        registered.value = isRegistered()
        loadParticipants(event.id)?.let { participants.addAll(it) }
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            Text(
                text = event.title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                ),
                modifier = Modifier
                    .padding(4.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.DateRange,
                    contentDescription = null,
                    modifier = Modifier
                        .size(14.dp),
                    tint = Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.dateTimeBegin} - ${event.dateTimeEnd}, ${event.getDuration()}",
                    style = TextStyle(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.Gray,
                    ),
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(4.dp)
            ) {
                Text(
                    text = stringResource(R.string.label_host),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = event.host?.toString() ?: "",
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Normal,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            val context = LocalContext.current

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(4.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.available_places, event.maxParticipants - event.currentParticipants, event.maxParticipants),
                    style = TextStyle(
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    ),
                )

                Spacer(modifier = Modifier.weight(1f))

                if (canRegister && isLogged.value) {
                    val enabled = (event.currentParticipants < event.maxParticipants && !registered.value) || registered.value
                    val message = if (registered.value) R.string.withdraw else R.string.sign_up
                    GrayButton(
                        text = stringResource(message),
                        enabled = enabled,
                        loader = true,
                        onClick = {
                            if (registered.value)
                                onUnregister(event.id)
                            else
                                onRegister(event.id)
                        }
                    )
                }
            }

            if (isLogged.value && event.longDescription != null) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(4.dp)
                ) {
                    HtmlContent(
                        HtmlData(event.longDescription)
                    )
                }
            }

            if (isLogged.value && event.host != null && event.host.email == SessionManager.identifier && participants.isNotEmpty()) {
                Box {
                    LazyColumn {
                        item {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.label_events_participants),
                                    style = TextStyle(
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = MaterialTheme.colorScheme.onSurface,
                                    ),
                                )
                            }
                        }
                        itemsIndexed(participants) { index, participant ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = participant.description,
                                        style = MaterialTheme.typography.bodyLarge
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = participant.companyName.orEmpty(),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.Gray
                                    )
                                }
                                val deletedMsg = stringResource(id = R.string.deleted_participant, participant.description)
                                val errorMsg = stringResource(id = R.string.deleting_error)
                                Button(onClick = {
                                    val result = onDeleteParticipant.invoke(event.id, participant.id)
                                    if (result) {
                                        Toast.makeText(
                                            context,
                                            deletedMsg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        participants.remove(participant)
                                    } else {
                                        Toast.makeText(
                                            context,
                                            errorMsg,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }) {
                                    Text(text = stringResource(id = R.string.label_delete))
                                }
                            }

                            if (index < participants.lastIndex) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 16.dp),
                                    thickness = 1.dp,
                                    color = Color.LightGray
                                )
                            }
                        }
                    }
                }
            } else {
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.label_no_participants),
                            style = TextStyle(
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface,
                            ),
                        )
                    }
                }
            }
        }
    }
}