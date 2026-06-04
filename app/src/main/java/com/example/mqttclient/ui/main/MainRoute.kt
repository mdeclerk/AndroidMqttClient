package com.example.mqttclient.ui.main

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.mqttclient.R
import com.example.mqttclient.ui.components.appTopAppBarColors
import com.example.mqttclient.ui.components.appTopBarTextButtonColors
import com.example.mqttclient.ui.main.publish_message.PublishMessageScreen
import com.example.mqttclient.ui.main.publish_message.PublishMessageViewModel
import com.example.mqttclient.ui.main.received_messages.ReceivedMessagesScreen
import com.example.mqttclient.ui.main.received_messages.ReceivedMessagesViewModel
import com.example.mqttclient.ui.main.subscribed_topics.SubscribedTopicsScreen
import com.example.mqttclient.ui.main.subscribed_topics.SubscribedTopicsViewModel
import org.koin.compose.viewmodel.koinViewModel

private enum class MainTab(
    val route: String,
    val label: String,
    val title: String,
    @param:DrawableRes val iconRes: Int,
) {
    Subscribe("subscribe", "Subscribe", "Subscribe to topic", R.drawable.ic_subscribe),
    Publish("publish", "Publish", "Send message", R.drawable.ic_send),
    Messages("messages", "Messages", "Received messages", R.drawable.ic_messages),
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainRoute(onDisconnected: () -> Unit) {
    val mainViewModel = koinViewModel<MainViewModel>()
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        mainViewModel.effects.collect { effect ->
            when (effect) {
                MainEffect.NavigateToConnect -> onDisconnected()
                is MainEffect.ShowError -> snackbarHostState.showSnackbar(
                    message = effect.message,
                    actionLabel = "Dismiss",
                    duration = SnackbarDuration.Indefinite,
                )
            }
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentTab = MainTab.entries.firstOrNull { it.route == currentRoute } ?: MainTab.Subscribe

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(currentTab.title) },
                colors = appTopAppBarColors(),
                actions = {
                    TextButton(
                        onClick = { mainViewModel.disconnect() },
                        colors = appTopBarTextButtonColors(),
                    ) {
                        Text("Disconnect")
                    }
                    if (currentTab == MainTab.Messages) {
                        val messagesEntry = navController.getBackStackEntry(MainTab.Messages.route)
                        val messagesViewModel =
                            koinViewModel<ReceivedMessagesViewModel>(viewModelStoreOwner = messagesEntry)
                        IconButton(onClick = { menuExpanded = true }) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = menuExpanded,
                            onDismissRequest = { menuExpanded = false },
                        ) {
                            DropdownMenuItem(
                                text = { Text("Delete all") },
                                onClick = {
                                    messagesViewModel.deleteAllMessages()
                                    menuExpanded = false
                                },
                            )
                        }
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                MainTab.entries.forEach { tab ->
                    NavigationBarItem(
                        selected = currentTab == tab,
                        onClick = {
                            navController.navigate(tab.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(painterResource(tab.iconRes), contentDescription = tab.label) },
                        label = { Text(tab.label) },
                    )
                }
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = MainTab.Subscribe.route,
            modifier = Modifier.padding(padding),
        ) {
            composable(MainTab.Subscribe.route) {
                val viewModel = koinViewModel<SubscribedTopicsViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                SubscribedTopicsScreen(
                    state = state,
                    onToggle = viewModel::toggleSubscription,
                    onDelete = viewModel::deleteTopic,
                    onNewTopicNameChange = viewModel::onNewTopicNameChange,
                    onCreateTopic = viewModel::createNewTopic,
                )
            }
            composable(MainTab.Publish.route) {
                val viewModel = koinViewModel<PublishMessageViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                PublishMessageScreen(
                    state = state,
                    onTopicChange = viewModel::onTopicChange,
                    onPayloadChange = viewModel::onPayloadChange,
                    onQosChange = viewModel::onQosChange,
                    onRetainChange = viewModel::onRetainChange,
                    onSend = viewModel::send,
                )
            }
            composable(MainTab.Messages.route) {
                val viewModel = koinViewModel<ReceivedMessagesViewModel>()
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                ReceivedMessagesScreen(state = state)
            }
        }
    }
}
