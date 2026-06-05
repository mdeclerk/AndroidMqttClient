package com.example.mqttclient.ui.connect.recents_brokers

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mqttclient.R
import com.example.mqttclient.ui.components.appTopAppBarColors
import com.example.mqttclient.ui.connect.ConnectViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecentBrokersRoute(
    viewModel: ConnectViewModel,
    onNavigateUp: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    var menuExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.all_recent_brokers)) },
                colors = appTopAppBarColors(),
                navigationIcon = {
                    IconButton(onClick = onNavigateUp) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.recents_back_cd),
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = stringResource(R.string.all_more_options_cd),
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false },
                    ) {
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.all_delete_all)) },
                            onClick = {
                                menuExpanded = false
                                viewModel.deleteAllRecentBrokers()
                                viewModel.clearHostAndPort()
                                onNavigateUp()
                            },
                        )
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
