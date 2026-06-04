package com.example.mqttclient.ui.connect.recents_brokers

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mqttclient.ui.connect.ConnectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentBrokersRoute(
    viewModel: ConnectViewModel,
    onNavigateUp: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Recent brokers") },
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.deleteAllRecentBrokers()
                            viewModel.clearHostAndPort()
                            onNavigateUp()
                        },
                    ) {
                        Text("Delete all")
                    }
                },
            )
        },
    ) { padding ->
        RecentBrokersScreen(
            brokers = state.recentBrokers,
            onBrokerClick = { broker ->
                viewModel.applyBroker(broker)
                onNavigateUp()
            },
            onDeleteClick = { broker ->
                if (state.recentBrokers.size == 1) {
                    viewModel.clearHostAndPort()
                    onNavigateUp()
                }
                viewModel.deleteRecentBroker(broker)
            },
            modifier = Modifier.padding(padding),
        )
    }
}
