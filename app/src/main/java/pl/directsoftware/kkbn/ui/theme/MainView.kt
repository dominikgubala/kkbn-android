package pl.directsoftware.kkbn.ui.theme

import android.widget.Toast
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Sync
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
import androidx.compose.runtime.NonRestartableComposable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import pl.directsoftware.kkbn.AppViewModel
import pl.directsoftware.kkbn.R
import pl.directsoftware.kkbn.api.data.Event
import pl.directsoftware.kkbn.api.data.Exhibitor
import pl.directsoftware.kkbn.api.data.Partner
import pl.directsoftware.kkbn.common.HtmlData
import pl.directsoftware.kkbn.common.Route
import pl.directsoftware.kkbn.utils.Consts.TRANSITION_TIME
import pl.directsoftware.kkbn.utils.Logger

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
        logger.debug("kliknąłem sobie żeby odświeżyło")
        runBlocking { delay(2000) }
        logger.debug("po delay-u")
    }

    Box(
        Modifier
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
fun HtmlContent(
    htmlData: HtmlData,
) {
    if (!htmlData.isEmpty()) {
        for ((index, part) in htmlData.parts.withIndex()) {
            if (part.isNotEmpty()) {
                Text(
                    text = part,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Start
                )
            }
            if (index < htmlData.matches.size) {
                val bitmap = htmlData.bitmapList[index]
                NetworkImage(
                    image = bitmap,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
@NonRestartableComposable
fun NetworkImage(image: ImageBitmap?, modifier: Modifier = Modifier) {
    image?.let {
        Image(bitmap = it, contentDescription = null, modifier = modifier)
    }
}

@Composable
fun ImageWithText(painter: Painter, text: String, contentDescription: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Image(
            painter = painter,
            contentDescription = contentDescription,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
        TextWithShadow(
            text = text,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(4.dp)
        )
    }
}

@Composable
fun TextWithShadow(
    text: String,
    modifier: Modifier
) {
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.DarkGray,
        modifier = modifier
            .fillMaxWidth()
            .offset(
                x = 2.dp,
                y = 2.dp
            )
            .alpha(0.75f),
        fontSize = 24.sp
    )
    Text(
        text = text,
        textAlign = TextAlign.Center,
        color = Color.White,
        modifier = modifier,
        fontSize = 24.sp
    )
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
            navController
        )
        Route.NEWS -> NewsScreen(viewModel, navController)
        Route.NEWS_BY_ID -> {
            NewsByIdScreen(
                navController = navController,
                id = backStackEntry.arguments?.getString("id")?.toIntOrNull()!!
            )
        }
        Route.COMMITTEE -> CommitteeScreen(
            navController,
        )
        Route.PARTNERS -> PartnersScreen(navController)
        Route.PARTNERS_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val partner = Partner.restore(serialized) ?: return
            PartnerScreen(
                navController = navController,
                partner = partner
            )
        }
        Route.EXHIBITORS -> ExhibitorsScreen(navController)
        Route.EXHIBITORS_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val exhibitor = Exhibitor.restore(serialized) ?: return
            ExhibitorScreen(
                navController,
                exhibitor
            )
        }
        Route.PLACE -> PlaceScreen()
        Route.CONTACT -> ContactScreen()
        Route.DOWNLOADS -> DownloadsScreen()
        Route.EVENT_BY_OBJECT -> {
            val serialized = backStackEntry.arguments?.getString("object")
            val event = Event.restore(serialized) ?: return
            EventScreen(event, viewModel)
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

        Route.MY_ACCOUNT -> MyAccountScreen(viewModel, navController)

        Route.EVENT_MANAGEMENT -> EventManagementScreen(viewModel)
        Route.PARTICIPANTS_MANAGEMENT -> ParticipantManagementScreen(viewModel)
        Route.HOST_MANAGEMENT -> HostManagementScreen(viewModel)
    }
}

@Composable
fun SyncButton(viewModel: AppViewModel) {
    var rotation by remember { mutableFloatStateOf(0f) }

    val animatedRotation by animateFloatAsState(
        targetValue = rotation,
        animationSpec = tween(durationMillis = 1000, easing = LinearEasing),
        label = ""
    )

    Icon(
        imageVector = Icons.Default.Sync,
        contentDescription = null,
        modifier = Modifier
            .rotate(animatedRotation)
            .clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                rotation += 360f
                viewModel.refresh()
            }
    )
}