package com.example.mqttclient.ui.connect

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mqttclient.R
import com.example.mqttclient.ui.components.appTopAppBarColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectRoute(
    viewModel: ConnectViewModel,
    onNavigateToRecents: () -> Unit,
    onConnected: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var menuExpanded by remember { mutableStateOf(false) }
    val dismissLabel = stringResource(R.string.all_dismiss)

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                ConnectEffect.NavigateToMain -> onConnected()
                is ConnectEffect.ShowError -> snackbarHostState.showSnackbar(
                    message = effect.message,
                    actionLabel = dismissLabel,
                    duration = SnackbarDuration.Indefinite,
                )
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.connect_title)) },
                colors = appTopAppBarColors(),
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
                            text = { Text(stringResource(R.string.connect_default_broker)) },
                            onClick = {
                                viewModel.setDefaultHostAndPort()
                                menuExpanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(stringResource(R.string.all_recent_brokers)) },
                            onClick = {
                                menuExpanded = false
                                onNavigateToRecents()
                            },
                        )
                    }
                },
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { padding ->
        ConnectScreen(
            state = state,
            onHostChange = viewModel::onHostChange,
            onPortChange = viewModel::onPortChange,
            onConnectClick = viewModel::connect,
            modifier = Modifier.padding(padding),
        )
    }
}
