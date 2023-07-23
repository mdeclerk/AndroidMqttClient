package com.example.mqttclient.ui.connect.recents_brokers

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
import com.example.mqttclient.databinding.FragmentRecentBrokersBinding
import com.example.mqttclient.ui.connect.ConnectViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class RecentBrokersFragment : Fragment(R.layout.fragment_recent_brokers), MenuProvider {

    private lateinit var binding: FragmentRecentBrokersBinding

    private val viewModel: ConnectViewModel by activityViewModel()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (requireActivity() as MenuHost)
            .addMenuProvider(this, viewLifecycleOwner, Lifecycle.State.RESUMED)

        binding = FragmentRecentBrokersBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        binding.rvRecentBrokers.adapter = RecentBrokersAdapter(
            RecentBrokersAdapter.ClickListener {
                viewModel.host.value = it.host
                viewModel.port.value = it.port.toString()
                findNavController().navigateUp()
            },
            RecentBrokersAdapter.ClickListener {
                if (viewModel.brokerList.value!!.size == 1) {
                    viewModel.host.value = ""
                    viewModel.port.value = null
                    findNavController().navigateUp()
                }
                viewModel.deleteRecentBroker(it)
            }
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
        menuInflater.inflate(R.menu.toolbar_menu_recent_brokers, menu)
    }

    override fun onMenuItemSelected(menuItem: MenuItem)= when (menuItem.itemId) {
        R.id.delete_all_menu_item -> {
            viewModel.deleteAllRecentBrokers()
            viewModel.host.value = ""
            viewModel.port.value = null
            findNavController().navigateUp()
            true
        }
        else -> false
    }
}