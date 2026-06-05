package com.example.mqttclient.ui.connect.recents_brokers

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.mqttclient.R
import com.example.mqttclient.domain.RecentBroker
import com.example.mqttclient.ui.theme.MqttClientTheme
import com.example.mqttclient.ui.util.formatTimestamp

@Composable
fun RecentBrokersScreen(
    brokers: List<RecentBroker>,
    onBrokerClick: (RecentBroker) -> Unit,
    onDeleteClick: (RecentBroker) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (brokers.isEmpty()) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(
                text = stringResource(R.string.recents_empty),
                style = MaterialTheme.typography.headlineSmall,
            )
        }
    } else {
        LazyColumn(modifier = modifier.fillMaxSize()) {
            items(brokers, key = { it.toString() }) { broker ->
                RecentBrokerRow(
                    broker = broker,
                    onClick = { onBrokerClick(broker) },
                    onDelete = { onDeleteClick(broker) },
                )
                HorizontalDivider()
            }
        }
    }
}

@Composable
private fun RecentBrokerRow(
    broker: RecentBroker,
    onClick: () -> Unit,
    onDelete: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(start = 24.dp, end = 24.dp, top = 16.dp, bottom = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = broker.toString(),
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = formatTimestamp(broker.timestamp),
                style = MaterialTheme.typography.bodySmall,
            )
        }
        IconButton(onClick = onDelete) {
            Icon(
                painter = painterResource(R.drawable.ic_delete),
                contentDescription = stringResource(R.string.recents_delete_broker_cd),
                tint = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun RecentBrokersScreenPreview() {
    MqttClientTheme {
        RecentBrokersScreen(
            brokers = listOf(
                RecentBroker("broker.hivemq.com", 1883),
                RecentBroker("test.mosquitto.org", 1883),
            ),
            onBrokerClick = {},
            onDeleteClick = {},
        )
    }
}
