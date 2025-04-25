package pl.directsoftware.kkbn.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Group
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.auth.MemberRole
import pl.directsoftware.kkbn.common.Route
import pl.directsoftware.kkbn.utils.SessionManager

@Composable
fun MyAccountScreen(
    goTo: (Route) -> Unit = {},
    onLogout: () -> Unit = {},
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp)
    ) {
        if (SessionManager.isGranted(MemberRole.EDIT_PARTICIPANT)) {
            AccountOptionItem(R.string.title_event_management, Icons.Default.Event) {
                goTo(Route.EVENT_MANAGEMENT)
            }
            HorizontalDivider(thickness = 1.dp)
        }

        if (SessionManager.isGranted(MemberRole.EDIT_HOST)) {
            AccountOptionItem(R.string.title_participant_management, Icons.Default.Group) {
                goTo(Route.PARTICIPANTS_MANAGEMENT)
            }
            HorizontalDivider(thickness = 1.dp)
        }

        if (SessionManager.isGranted(MemberRole.EDIT_ALL)) {
            AccountOptionItem(R.string.title_host_management, Icons.Default.Group) {
                goTo(Route.HOST_MANAGEMENT)
            }
            HorizontalDivider(thickness = 1.dp)
        }

        AccountOptionItem(R.string.title_logout, Icons.AutoMirrored.Filled.ExitToApp) {
            onLogout()
        }
    }
}

@Composable
fun AccountOptionItem(titleId: Int, icon: ImageVector, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(text = stringResource(id = titleId))
        },
        leadingContent = {
            Icon(imageVector = icon, contentDescription = null)
        },
        modifier = Modifier
            .padding(vertical = 4.dp)
            .clickable {
                onClick.invoke()
            }
    )
}