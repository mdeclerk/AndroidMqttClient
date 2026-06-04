package com.example.mqttclient.ui.connect

import android.content.Intent
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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.mqttclient.R
import com.example.mqttclient.ui.main.MainActivity
import com.example.mqttclient.ui.theme.MqttClientTheme
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ConnectFragment : Fragment(), MenuProvider {

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
                ConnectScreen(
                    state = state,
                    onHostChange = viewModel::onHostChange,
                    onPortChange = viewModel::onPortChange,
                    onConnectClick = viewModel::connect,
                )
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.effects.collect { effect ->
                    when (effect) {
                        is ConnectEffect.NavigateToMain -> {
                            val ctx = requireActivity()
                            startActivity(Intent(ctx, MainActivity::class.java))
                            ctx.finish()
                        }
                        is ConnectEffect.ShowError -> {
                            Snackbar.make(requireView(), effect.message, Snackbar.LENGTH_INDEFINITE)
                                .setAction("Dismiss") {}
                                .show()
                        }
                    }
                }
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_connect, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.default_broker_menu_item -> {
            viewModel.setDefaultHostAndPort()
            true
        }
        R.id.recent_brokers_menu_item -> {
            findNavController()
                .navigate(ConnectFragmentDirections.actionConnectFragmentToRecentsFragment())
            true
        }
        else -> false
    }
}
