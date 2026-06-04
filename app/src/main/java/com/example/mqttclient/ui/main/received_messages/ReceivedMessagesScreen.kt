package com.example.mqttclient.ui.main.received_messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mqttclient.R
import com.example.mqttclient.domain.MqttMessage
import com.example.mqttclient.ui.theme.MqttClientTheme
import kotlinx.coroutines.launch

@Composable
fun ReceivedMessagesScreen(
    state: ReceivedMessagesUiState,
    modifier: Modifier = Modifier,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf { listState.firstVisibleItemIndex > 0 }
    }

    // New messages are prepended; keep the list pinned to the top when already there.
    LaunchedEffect(state.messages) {
        if (listState.firstVisibleItemIndex == 0) {
            listState.scrollToItem(0)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        if (state.messages.isEmpty()) {
            Text(
                text = "No messages",
                fontSize = 24.sp,
                modifier = Modifier.align(Alignment.Center),
            )
        } else {
            LazyColumn(state = listState, modifier = Modifier.fillMaxSize()) {
                items(state.messages.size) { index ->
                    MessageRow(state.messages[index])
                    HorizontalDivider()
                }
            }
        }

        AnimatedVisibility(
            visible = showScrollToTop,
            enter = fadeIn(),
            exit = fadeOut(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 16.dp),
        ) {
            Button(onClick = { scope.launch { listState.animateScrollToItem(0) } }) {
                Icon(
                    painter = painterResource(R.drawable.ic_arrow_upward),
                    contentDescription = null,
                )
                Text(text = "Scroll to top", modifier = Modifier.padding(start = 8.dp))
            }
        }
    }
}

@Composable
private fun MessageRow(message: MqttMessage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = message.topic, fontSize = 14.sp)
            @Suppress("DEPRECATION")
            Text(text = message.timestamp.toLocaleString(), fontSize = 14.sp)
        }
        Text(
            text = message.payload,
            fontSize = 20.sp,
            modifier = Modifier.padding(top = 8.dp),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReceivedMessagesScreenPreview() {
    MqttClientTheme {
        ReceivedMessagesScreen(
            state = ReceivedMessagesUiState(
                messages = listOf(
                    MqttMessage("testtopic/a", "hello"),
                    MqttMessage("testtopic/b", "world"),
                ),
            ),
        )
    }
}
