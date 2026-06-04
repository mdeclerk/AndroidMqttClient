package com.example.mqttclient.ui.connect.recents_brokers

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mqttclient.R
import com.example.mqttclient.ui.connect.ConnectViewModel
import com.example.mqttclient.ui.theme.MqttClientTheme
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecentBrokersFragment : Fragment(), MenuProvider {

    private val viewModel: ConnectViewModel by activityViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView(requireContext()).apply {
        setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
        setContent {
            MqttClientTheme {
                val state by viewModel.uiState.collectAsStateWithLifecycle()
                RecentBrokersScreen(
                    brokers = state.recentBrokers,
                    onBrokerClick = { broker ->
                        viewModel.applyBroker(broker)
                        findNavController().navigateUp()
                    },
                    onDeleteClick = { broker ->
                        if (state.recentBrokers.size == 1) {
                            viewModel.clearHostAndPort()
                            findNavController().navigateUp()
                        }
                        viewModel.deleteRecentBroker(broker)
                    },
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_recent_brokers, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.delete_all_menu_item -> {
            viewModel.deleteAllRecentBrokers()
            viewModel.clearHostAndPort()
            findNavController().navigateUp()
            true
        }
        else -> false
    }
}
