package com.example.mqttclient.ui.main.subscribed_topics

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.mqttclient.R
import com.example.mqttclient.databinding.AlertDialogSubscribeTopicBinding
import com.example.mqttclient.databinding.FragmentSubscribedTopicsBinding
import org.koin.androidx.navigation.koinNavGraphViewModel

class SubscribedTopicsFragment : Fragment(R.layout.fragment_subscribed_topics) {

    private lateinit var binding: FragmentSubscribedTopicsBinding

    private val viewModel: SubscribedTopicsViewModel by koinNavGraphViewModel(R.id.main_nav_graph)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentSubscribedTopicsBinding.bind(view)
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        binding.rvSubscribedTopics.adapter = SubscribedTopicsListAdapter(
            SubscribedTopicsListAdapter.ClickListener {
                if (it.isSubscribed) {
                    viewModel.unsubscribeTopic(it)
                } else {
                    viewModel.subscribeTopic(it)
                }
            },
            SubscribedTopicsListAdapter.ClickListener {
                viewModel.unsubscribeTopic(it)
                viewModel.deleteTopicFromDb(it)
            }
        )
        binding.fabSubscribeTopic.setOnClickListener {
            val binding = AlertDialogSubscribeTopicBinding.inflate(layoutInflater)
            binding.viewModel = viewModel

            val builder = AlertDialog.Builder(requireContext())
            with(builder) {
                setTitle("Subscribe to topic")
                setMessage("e.g. /testtopic/#")
                setView(binding.root)
                setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                    viewModel.createNewTopicWithTopicName()
                })
                setNegativeButton("Cancel", DialogInterface.OnClickListener { dialog, which ->
                })
                show()
            }
        }
    }
}