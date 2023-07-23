package com.example.mqttclient.ui.connect

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import com.example.mqttclient.R
import com.example.mqttclient.databinding.FragmentConnectBinding
import com.example.mqttclient.domain.MqttClient
import com.example.mqttclient.ui.main.MainActivity
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ConnectFragment : Fragment(R.layout.fragment_connect), MenuProvider {

    private lateinit var binding: FragmentConnectBinding

    private val viewModel: ConnectViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding = FragmentConnectBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.btnConnect.setOnClickListener {
            viewModel.connect()
        }

        viewModel.clientState.observe(viewLifecycleOwner) {
            when (it) {
                is MqttClient.State.Connected -> {
                    val ctx = requireActivity()
                    val intent = Intent(ctx, MainActivity::class.java)
                    startActivity(intent)
                    ctx.finish()
                }
                is MqttClient.State.Error -> {
                    Snackbar.make(binding.root, it.message, Snackbar.LENGTH_INDEFINITE)
                        .setAction("Dismiss") {}
                        .show()

                    viewModel.disconnect()
                }
                else -> Unit
            }
        }
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_connect, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem)= when (menuItem.itemId) {
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