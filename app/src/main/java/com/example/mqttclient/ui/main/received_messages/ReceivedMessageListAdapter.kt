package com.example.mqttclient.ui.main.received_messages

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttclient.R
import com.example.mqttclient.databinding.ListItemReceivedMessageBinding
import com.example.mqttclient.domain.MqttMessage
import com.example.mqttclient.utils.fadeIn
import com.example.mqttclient.utils.fadeOut

class ReceivedMessageListAdapter(
    private val recyclerView: RecyclerView,
    private val scrollToTopButton: Button
) : ListAdapter<MqttMessage, ReceivedMessageListAdapter.ViewHolder>(MessageDiffCallback()) {

    private var isScrolling = false

    init {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                isScrolling = newState == RecyclerView.SCROLL_STATE_DRAGGING
                updateScrollToTopButtonVisibility()
            }
        })

        scrollToTopButton.visibility = View.GONE
        scrollToTopButton.setOnClickListener {
            recyclerView.scrollToPosition(0)
            scrollToTopButton.fadeOut()
        }
    }

    class ViewHolder(val binding: ListItemReceivedMessageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_received_message, parent, false)
        val binding = ListItemReceivedMessageBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.message = getItem(position)
        holder.binding.executePendingBindings()
    }

    fun scrollToTop() {
        val firstVisibleItemPosition =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (firstVisibleItemPosition == 0 && !isScrolling) {
            recyclerView.scrollToPosition(0)
        }
    }

    fun updateScrollToTopButtonVisibility() {
        val firstVisibleItemPosition =
            (recyclerView.layoutManager as LinearLayoutManager).findFirstVisibleItemPosition()
        if (firstVisibleItemPosition > 0) {
            scrollToTopButton.fadeIn()
        } else {
            scrollToTopButton.fadeOut()
        }
    }
}

class MessageDiffCallback : DiffUtil.ItemCallback<MqttMessage>() {
    override fun areItemsTheSame(oldItem: MqttMessage, newItem: MqttMessage): Boolean {
        return oldItem.topic == newItem.topic
    }

    override fun areContentsTheSame(oldItem: MqttMessage, newItem: MqttMessage): Boolean {
        return oldItem == newItem
    }
}

@BindingAdapter("android:messageList")
fun setRecyclerViewLiveData(recyclerView: RecyclerView, liveData: LiveData<List<MqttMessage>>) {
    val adapter = recyclerView.adapter as ReceivedMessageListAdapter
    adapter.submitList(liveData.value) {
        adapter.scrollToTop()
        adapter.updateScrollToTopButtonVisibility()
    }
}