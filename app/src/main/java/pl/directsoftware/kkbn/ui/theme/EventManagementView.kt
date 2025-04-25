package pl.directsoftware.kkbn.ui.theme

import android.app.TimePickerDialog
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.utils.StringHelper
import java.util.Calendar

@Composable
fun EventManagementScreen(
    loadData: () -> List<Event>?,
    goTo: (Event) -> Unit = {},
) {
    val isLoading = remember { mutableStateOf(true) }
    val events = remember { mutableStateOf<List<Event>?>(null) }

    LaunchedEffect(Event::class) {
        events.value = loadData()
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        if (events.value != null) {
            val rowHeight = 48.dp
            LazyColumn {
                items(events.value!!) { event ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                goTo(event)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(rowHeight)
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = event.getTime(),
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(end = 8.dp)
                            )

                            Column(
                                modifier = Modifier
                                    .weight(3f),
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = event.title,
                                    style = MaterialTheme.typography.bodyLarge,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Text(
                                    text = stringResource(R.string.available_places, event.maxParticipants - event.currentParticipants, event.maxParticipants),
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventManagementScreen(
    event: Event,
    loadHostList: () -> MutableMap<Int,String>,
    onBack: () -> Unit,
    onSave: (Event) -> Unit = {}
) {
    val nestedScroll = rememberScrollState()
    val isLoading = remember { mutableStateOf(true) }

    var title by remember { mutableStateOf(event.title) }
    var dateTimeBegin by remember { mutableStateOf(event.dateTimeBegin ?: "") }
    var dateTimeEnd by remember { mutableStateOf(event.dateTimeEnd ?: "") }
    var speaker by remember { mutableStateOf(event.host?.id ?: "") }
    var maxParticipants by remember { mutableStateOf(event.maxParticipants.toString()) }

    var expanded by remember { mutableStateOf(false) }
    val hostList = remember { mutableStateMapOf(-1 to "Wybierz prowadzącego") }
    var hostId by remember { mutableIntStateOf(event.host?.id ?: -1) }

    LaunchedEffect(Event::class) {
        hostList.putAll(loadHostList())
        isLoading.value = false
    }

    if (isLoading.value) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 4.dp)
                .fillMaxSize()
                .verticalScroll(nestedScroll)
            ,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(stringResource(R.string.edit_event), style = MaterialTheme.typography.headlineSmall)

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.title)) },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTimePickerField(
                label = stringResource(R.string.start_time),
                time = dateTimeBegin,
                onChanged = { dateTimeBegin = it }
            )

            OutlinedTimePickerField(
                label = stringResource(R.string.end_time),
                time = dateTimeEnd,
                onChanged = { dateTimeEnd = it }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                TextField(
                    readOnly = true,
                    value = hostList.getValue(hostId),
                    onValueChange = {},
                    label = { Text(stringResource(R.string.speaker)) },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                        .fillMaxWidth(),
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    hostList
                        .toSortedMap()
                        .forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.value) },
                            onClick = {
                                hostId = selectionOption.key
                                expanded = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = maxParticipants,
                onValueChange = { maxParticipants = it.filter { c -> c.isDigit() } },
                label = { Text(stringResource(R.string.max_participants)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )

            Text(
                text = stringResource(R.string.current_participants, event.currentParticipants),
                style = MaterialTheme.typography.bodyMedium
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onBack() },
                    modifier = Modifier
                        .weight(1f)
                ) {
                    Text(stringResource(R.string.cancel))
                }

                Button(
                    onClick = {
                        onSave(
                            event.copy(
                                title = title,
                                dateTimeBegin = dateTimeBegin.ifBlank { null },
                                dateTimeEnd = dateTimeEnd.ifBlank { null },
                                host = EventHost(hostId),
                                maxParticipants = maxParticipants.toIntOrNull() ?: 0
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
}

@Composable
fun OutlinedTimePickerField(
    label: String,
    time: String?,
    onChanged: (String) -> Unit) {

    var text by remember { mutableStateOf(time ?: "") }

    val context = LocalContext.current
    val calendar = StringHelper.parseTimeToCalendar(time) ?: Calendar.getInstance()
    val hour = calendar.get(Calendar.HOUR_OF_DAY)
    val minute = calendar.get(Calendar.MINUTE)

    val timePickerDialog = TimePickerDialog(
        context,
        { _, selectedHour: Int, selectedMinute: Int ->
            text = "%02d:%02d".format(selectedHour, selectedMinute)
            onChanged.invoke(text)
        },
        hour,
        minute,
        true // true oznacza format 24h
    )

    OutlinedTextField(
        value = text,
        onValueChange = {},
        label = { Text(label) },
        modifier = Modifier
            .fillMaxWidth()
        ,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.CalendarToday,
                contentDescription = null,
                modifier = Modifier
                    .clickable {
                        timePickerDialog.show()
                    }
            )
        },
        readOnly = true
    )
}