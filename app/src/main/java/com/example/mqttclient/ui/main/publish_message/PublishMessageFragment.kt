package com.example.mqttclient.ui.main.publish_message

import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.example.mqttclient.R
import com.example.mqttclient.databinding.FragmentPublishMessageBinding
import org.koin.androidx.navigation.koinNavGraphViewModel

class PublishMessageFragment : Fragment(R.layout.fragment_publish_message) {

    private lateinit var binding: FragmentPublishMessageBinding

    private val viewModel: PublishMessageViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentPublishMessageBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

        val adapter =
            ArrayAdapter(requireContext(), R.layout.drop_down_list_item_qos, listOf("0", "1", "2"))
        binding.actvMessageQos.setAdapter(adapter)

        //unfortunately two data binding in XML (android:text = "@={viewModel.qos}") does not work :(
        setupActvMessageQosDatabinding()
    }

    private fun setupActvMessageQosDatabinding() {
        viewModel.qos.observe(viewLifecycleOwner) {
            binding.actvMessageQos.setText(it, false)
        }
        binding.actvMessageQos.setOnItemClickListener { _, _, _, _ ->
            viewModel.qos.value = binding.actvMessageQos.text.toString()
        }
    }
}