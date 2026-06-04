package com.example.mqttclient.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.example.mqttclient.ui.connect.ConnectRoute
import com.example.mqttclient.ui.connect.ConnectViewModel
import com.example.mqttclient.ui.connect.recents_brokers.RecentBrokersRoute
import com.example.mqttclient.ui.main.MainRoute
import org.koin.compose.viewmodel.koinViewModel

object Routes {
    const val CONNECT_GRAPH = "connect_graph"
    const val CONNECT = "connect"
    const val RECENTS = "recents"
    const val MAIN = "main"
}

@Composable
fun AppNavHost(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = Routes.CONNECT_GRAPH) {
        navigation(startDestination = Routes.CONNECT, route = Routes.CONNECT_GRAPH) {
            composable(Routes.CONNECT) { entry ->
                // Share ConnectViewModel across the whole connect graph (connect + recents).
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Routes.CONNECT_GRAPH)
                }
                val viewModel = koinViewModel<ConnectViewModel>(viewModelStoreOwner = parentEntry)
                ConnectRoute(
                    viewModel = viewModel,
                    onNavigateToRecents = { navController.navigate(Routes.RECENTS) },
                    onConnected = {
                        navController.navigate(Routes.MAIN) {
                            popUpTo(Routes.CONNECT_GRAPH) { inclusive = true }
                        }
                    },
                )
            }
            composable(Routes.RECENTS) { entry ->
                val parentEntry = remember(entry) {
                    navController.getBackStackEntry(Routes.CONNECT_GRAPH)
                }
                val viewModel = koinViewModel<ConnectViewModel>(viewModelStoreOwner = parentEntry)
                RecentBrokersRoute(
                    viewModel = viewModel,
                    onNavigateUp = { navController.navigateUp() },
                )
            }
        }
        composable(Routes.MAIN) {
            MainRoute(
                onDisconnected = {
                    navController.navigate(Routes.CONNECT_GRAPH) {
                        popUpTo(Routes.MAIN) { inclusive = true }
                    }
                },
            )
        }
    }
}
