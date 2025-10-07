package pl.directsoftware.kkbn.ui.theme

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch
import pl.directsoftware.kkbn.AppViewModel
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.EventHost
import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.common.Route
import pl.directsoftware.kkbn.repository.DownloadRepository
import pl.directsoftware.kkbn.repository.PlaceRepository
import pl.directsoftware.kkbn.utils.Consts.TRANSITION_TIME
import pl.directsoftware.kkbn.utils.Logger
import pl.directsoftware.kkbn.utils.Notification
import pl.directsoftware.kkbn.utils.SessionManager

private val logger = Logger("MainView")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: AppViewModel) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val defaultRoute = Route.NEWS
    val defaultTitle = stringResource(defaultRoute.textId)
    val title = remember { mutableStateOf(defaultTitle) }

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val isChildrenRoute = Route.isNested(navBackStackEntry?.destination?.route)

    viewModel.onRefresh = {
        // todo
    }

    Box(
        Modifier
            .systemBarsPadding()
            .fillMaxSize()
            .pointerInput(Unit) {
                detectHorizontalDragGestures { change, dragAmount ->
                    val startX = change.position.x
                    val width = size.width.toFloat()

                    if (!drawerState.isOpen) {
                        if (startX < width * 0.2f && dragAmount > 20) {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    } else {
                        if (startX > width * 0.7f && dragAmount < -20) {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    }
                }
            }
    ) {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                DrawerContent(
                    title = title,
                    navController = navController,
                    viewModel = viewModel
                ) {
                    scope.launch {
                        drawerState.close()
                    }
                }
            },
            gesturesEnabled = false,
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(text = title.value) },
                        navigationIcon = {
                            IconButton(
                                onClick = {
                                    if (isChildrenRoute) {
                                        navController.popBackStack()
                                    } else {
                                        scope.launch { drawerState.open() }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = if (isChildrenRoute) Icons.AutoMirrored.Filled.ArrowBack else Icons.Filled.Menu,
                                    contentDescription = "Menu")
                            }
                        },
                        actions = {
                            SyncButton(viewModel)
                        }
                    )
                },
            ) { paddingValues ->
                NavHost(
                    navController = navController,
                    startDestination = defaultRoute.type,
                    modifier = Modifier.padding(paddingValues)
                ) {
                    Route.entries.forEach { screen ->
                        composable(
                            route = screen.type,
                            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(TRANSITION_TIME)) },
                            exitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(TRANSITION_TIME)) },
                            popEnterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(TRANSITION_TIME)) },
                            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(TRANSITION_TIME)) }
                        ) { backStackEntry ->
                            GoToRoute(
                                route = screen,
                                backStackEntry = backStackEntry,
                                navController = navController,
                                viewModel = viewModel
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DrawerContent(
    title: MutableState<String>,
    navController: NavController,
    viewModel: AppViewModel,
    onClose: () -> Unit
) {
    val isLoggedIn = viewModel.isLoggedIn.collectAsState()
    ModalDrawerSheet(
        modifier = Modifier
            .fillMaxSize()
        ,
    ) {
        Column() {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ImageWithText(

                    painter = painterResource(id = R.drawable.main_header),

                    text = stringResource(id = R.string.app_description),

                    contentDescription = stringResource(id = R.string.app_subtitle)

                )
            }

            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
            )

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 4.dp)
            ) {
                items(Route.entries) { route ->
                    if (route.main && route != Route.MY_ACCOUNT && (route != Route.LOGIN || !isLoggedIn.value)) {
                        val viewTitle = stringResource(id = route.textId)
                        NavigationDrawerItem(
                            label = { Text(viewTitle) },
                            selected = title.value == viewTitle,
                            onClick = {
                                title.value = viewTitle
                                navController.navigate(route.type)
                                onClose()
                            },
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }

                    if (route == Route.MY_ACCOUNT && isLoggedIn.value) {
                        val viewTitle = stringResource(id = route.textId)
                        NavigationDrawerItem(
                            label = { Text(viewTitle) },
                            selected = title.value == viewTitle,
                            onClick = {
                                title.value = viewTitle
                                navController.navigate(route.type)
                                onClose()
                            },
                            icon = {
                                Icon(
                                    imageVector = route.icon,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GoToRoute(
    route: Route,
    backStackEntry: NavBackStackEntry,
    navController: NavController,
    viewModel: AppViewModel,
) {
    val context = LocalContext.current

    when (route) {
        Route.AGENDA -> AgendaScreen(
            loadData = {
                viewModel.getAgendaEvents()
            },
            goToEvent = {
                navController.navigate(Route.EVENT_BY_OBJECT.setObject(it))
            }
        )

        Route.NEWS -> NewsScreen(
            loadData = {
                viewModel.getNews()
            },
            onClick = {
                navController.navigate(Route.NEWS_BY_ID.setId(it.id))
            }
        )

        Route.NEWS_BY_ID -> NewsByIdScreen(
            onLoad = {
                val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()!!
                viewModel.getNews(id)
            },
        )

        Route.COMMITTEE -> CommitteeScreen(
            loadData = {
                viewModel.getCommittee()
            }
        )

        Route.PARTNERS -> PartnersScreen(
            loadData = {
                viewModel.getPartners()
            },
            onClick = {
                navController.navigate(Route.PARTNERS_BY_OBJECT.setObject(it))
            }
        )

        Route.PARTNERS_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val partner = Partner.restore(serialized) ?: return
            PartnerScreen(
                partner = partner
            )
        }

        Route.EXHIBITORS -> ExhibitorsScreen(
            loadData = {
                viewModel.getExhibitors()
            },
            onClick = {
                navController.navigate(Route.PARTNERS_BY_OBJECT.setObject(it))
            }
        )

        Route.EXHIBITORS_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val exhibitor = Exhibitor.restore(serialized) ?: return
            ExhibitorScreen(
                exhibitor
            )
        }

        Route.PLACE -> PlaceScreen(
            loadData = {
                viewModel.getPlace()
            },
            onOpen = { url ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse(url)
                )
                context.startActivity(intent)
            }
        )

        Route.CONTACT -> ContactScreen(
            loadData = {
                viewModel.getContacts()
            }
        )

        Route.DOWNLOADS -> DownloadsScreen(
            loadData = {
                viewModel.getDownloads()
            },
            onOpen = { url ->
                val intent = Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://kkbn.pl/${url}")
                )
                context.startActivity(intent)
            }
        )

        Route.EVENT_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val event = Event.restore(serialized) ?: return
            EventScreen(
                event,
                viewModel.isLoggedIn,
                isRegistered = {
                    viewModel.isParticipantRegistered(event.id)
                },
                onRegister = { eventId ->
                    val memberId = SessionManager.memberId ?: -1
                    val memberType = SessionManager.memberType
                    val response = viewModel.registerParticipant(eventId, memberId, memberType)
                    Notification.toast(response != null)
                    navController.popBackStack()
                },
                onUnregister = { eventId ->
                    val memberId = SessionManager.memberId ?: -1
                    val memberType = SessionManager.memberType
                    val response = viewModel.unregisterParticipant(eventId, memberId, memberType)
                    Notification.toast(response != null)
                    navController.popBackStack()
                },
                loadParticipants = {
                    viewModel.getEventParticipants(it)
                },
                onDeleteParticipant = { eventId, participantId ->
                    logger.debug("delete: eventId($eventId), participantId($participantId)")
                    viewModel.unregisterParticipant(eventId, participantId)
                }
            )
        }

        Route.LOGIN -> {
            LoginScreen(
                onLoginClick = { email, token ->
                    val result = viewModel.login(email, token)
                    if (result) {
                        Toast.makeText(
                            context,
                            R.string.logged_in,
                            Toast.LENGTH_SHORT
                        ).show()
                        navController.navigate(Route.NEWS.type)
                    } else {
                        Toast.makeText(
                            context,
                            R.string.auth_error,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
        }

        Route.MY_ACCOUNT -> MyAccountScreen(
            goTo = {
                navController.navigate(it.type)
            },
            onLogout = {
                viewModel.logout()
                navController.navigate(Route.NEWS.type)
            }
        )

        Route.YOUR_DATA -> YourDataScreen(
            loadData = {
                viewModel.getProfile()
            },
            goToEvent = {
                navController.navigate(
                    Route.EVENT_BY_OBJECT.setObject(it)
                )
            }
        )

        Route.EVENT_MANAGEMENT -> EventManagementScreen(
            loadData = {
                viewModel.getEvents()
            },
            goTo = {
                navController.navigate(
                    Route.EVENT_MANAGEMENT_BY_OBJECT.setObject(it)
                )
            }
        )

        Route.EVENT_MANAGEMENT_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val event = Event.restore(serialized) ?: return
            EventManagementScreen(
                event,
                loadHostList = {
                    viewModel.getEventHostMap()
                },
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                    val response = viewModel.updateEvent(it)
                    Notification.toast(response != null)
                    navController.popBackStack()
                }
            )
        }

        Route.PARTICIPANTS_MANAGEMENT -> ParticipantManagementScreen(
            loadData = {
                viewModel.getEventParticipants()
            },
            onClick = {
                navController.navigate(
                    Route.PARTICIPANTS_MANAGEMENT_BY_OBJECT.setObject(it)
                )
            }
        )

        Route.PARTICIPANTS_MANAGEMENT_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val participant = EventHost.restore(serialized) ?: return
            ParticipantManagementScreen(
                participant,
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                    val response = viewModel.updateEventParticipant(it)
                    Notification.toast(response != null)
                    navController.popBackStack()
                }
            )
        }

        Route.HOST_MANAGEMENT -> HostManagementScreen(
            loadData = {
                viewModel.getEventHosts()
            },
            onClick = {
                navController.navigate(
                    Route.HOST_MANAGEMENT_BY_OBJECT.setObject(it)
                )
            }
        )

        Route.HOST_MANAGEMENT_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val host = EventHost.restore(serialized) ?: return
            HostManagementScreen(
                host,
                onBack = {
                    navController.popBackStack()
                },
                onSave = {
                    val response = viewModel.updateEventHost(it)
                    Notification.toast(response != null)
                    navController.popBackStack()
                }
            )
        }
    }
}

@Composable
fun SetSystemBars() {
    val systemUiController = rememberSystemUiController()
    val statusBarColor = Color.White
    val navigationBarColor = Color.Black

    SideEffect {
        systemUiController.setStatusBarColor(
            color = statusBarColor,
            darkIcons = false
        )
        systemUiController.setNavigationBarColor(
            color = navigationBarColor,
            darkIcons = false
        )
    }
}