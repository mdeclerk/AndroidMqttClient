package com.example.mqttclient.ui.main.publish_message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mqttclient.ui.theme.MqttClientTheme

@Composable
fun PublishMessageScreen(
    state: PublishMessageUiState,
    onTopicChange: (String) -> Unit,
    onPayloadChange: (String) -> Unit,
    onQosChange: (String) -> Unit,
    onRetainChange: (Boolean) -> Unit,
    onSend: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 24.dp, vertical = 16.dp),
    ) {
        SectionLabel("Topic:")
        OutlinedTextField(
            value = state.topic,
            onValueChange = onTopicChange,
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
        )

        SectionLabel("Quality of service (QoS):", Modifier.padding(top = 16.dp))
        QosDropdown(qos = state.qos, onQosChange = onQosChange)

        SectionLabel("Retain message on broker:", Modifier.padding(top = 16.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = state.retain, onCheckedChange = onRetainChange)
            Text("Retain")
        }

        SectionLabel("Message:", Modifier.padding(top = 8.dp))
        OutlinedTextField(
            value = state.payload,
            onValueChange = onPayloadChange,
            modifier = Modifier.fillMaxWidth(),
        )

        Button(
            onClick = onSend,
            enabled = state.isInputValid,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 24.dp),
        ) {
            Text("Send")
        }
    }
}

@Composable
private fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = modifier)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun QosDropdown(qos: String, onQosChange: (String) -> Unit) {
    val options = listOf("0", "1", "2")
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
    ) {
        OutlinedTextField(
            value = qos,
            onValueChange = {},
            readOnly = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier
                .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable)
                .fillMaxWidth(),
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onQosChange(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PublishMessageScreenPreview() {
    MqttClientTheme {
        PublishMessageScreen(
            state = PublishMessageUiState(isInputValid = true),
            onTopicChange = {},
            onPayloadChange = {},
            onQosChange = {},
            onRetainChange = {},
            onSend = {},
        )
    }
}
