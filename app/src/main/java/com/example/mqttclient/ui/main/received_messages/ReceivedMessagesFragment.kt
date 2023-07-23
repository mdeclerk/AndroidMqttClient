package com.example.mqttclient.ui.main.received_messages

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import com.example.mqttclient.R
import com.example.mqttclient.databinding.FragmentReceivedMessagesBinding
import com.example.mqttclient.databinding.FragmentRecentBrokersBinding
import org.koin.androidx.navigation.koinNavGraphViewModel

class ReceivedMessagesFragment : Fragment(R.layout.fragment_received_messages), MenuProvider {

    private lateinit var binding: FragmentReceivedMessagesBinding

    private val viewModel: ReceivedMessagesViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentReceivedMessagesBinding.bind(view)
        binding.lifecycleOwner = viewLifecycleOwner
        binding.viewModel = viewModel

        binding.rvReceivedMessagesList.adapter =
            ReceivedMessageListAdapter(binding.rvReceivedMessagesList, binding.btnScrollToTop)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }


    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_received_messages, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem) = when (menuItem.itemId) {
        R.id.delete_all_messages_menu_item -> {
            viewModel.deleteAllMessages()
            true
        }

        else -> false
    }
}