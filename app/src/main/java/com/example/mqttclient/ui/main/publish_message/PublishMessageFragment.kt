package com.example.mqttclient.ui.main.publish_message

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.mqttclient.R
import com.example.mqttclient.ui.theme.MqttClientTheme
import org.koin.androidx.navigation.koinNavGraphViewModel

class PublishMessageFragment : Fragment() {

    private val viewModel: PublishMessageViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MqttClientTheme {
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
        }
    }
}
