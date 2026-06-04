package com.example.mqttclient.ui.main.subscribed_topics

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mqttclient.R
import com.example.mqttclient.domain.SubscribedTopic
import com.example.mqttclient.ui.theme.MqttClientTheme

@Composable
fun SubscribedTopicsScreen(
    state: SubscribedTopicsUiState,
    onToggle: (SubscribedTopic) -> Unit,
    onDelete: (SubscribedTopic) -> Unit,
    onNewTopicNameChange: (String) -> Unit,
    onCreateTopic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDialog by rememberSaveable { mutableStateOf(false) }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.topics.isEmpty()) {
            Text(
                text = "No topics",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(state.topics, key = { it.name }) { topic ->
                    TopicRow(
                        topic = topic,
                        onToggle = { onToggle(topic) },
                        onDelete = { onDelete(topic) },
                    )
                    HorizontalDivider()
                }
            }
        }

        FloatingActionButton(
            onClick = { showDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(24.dp),
        ) {
            Icon(painterResource(R.drawable.ic_add), contentDescription = "Subscribe to topic")
        }
    }

    if (showDialog) {
        SubscribeDialog(
            topicName = state.newTopicName,
            onNameChange = onNewTopicNameChange,
            onConfirm = {
                onCreateTopic()
                showDialog = false
            },
            onDismiss = { showDialog = false },
        )
    }
}

@Composable
private fun TopicRow(
    topic: SubscribedTopic,
    onToggle: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(checked = topic.isSubscribed, onCheckedChange = { onToggle() })
        Text(
            text = topic.name,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp),
        )
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = "Delete topic",
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Composable
private fun SubscribeDialog(
    topicName: String,
    onNameChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Subscribe to topic") },
        text = {
            Column {
                Text(
                    text = "e.g. /testtopic/#",
                    style = MaterialTheme.typography.bodyMedium,
                )
                OutlinedTextField(
                    value = topicName,
                    onValueChange = onNameChange,
                    label = { Text("Topic filter") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                )
            }
        },
        confirmButton = { TextButton(onClick = onConfirm) { Text("OK") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } },
    )
}

@Preview(showBackground = true)
@Composable
private fun SubscribedTopicsScreenPreview() {
    MqttClientTheme {
        SubscribedTopicsScreen(
            state = SubscribedTopicsUiState(
                topics = listOf(
                    SubscribedTopic("testtopic/#", isSubscribed = true),
                    SubscribedTopic("sensors/temp", isSubscribed = false),
                ),
            ),
            onToggle = {},
            onDelete = {},
            onNewTopicNameChange = {},
            onCreateTopic = {},
        )
    }
}
