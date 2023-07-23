package com.example.mqttclient.ui.main.subscribed_topics

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttclient.R
import com.example.mqttclient.databinding.ListItemSubscribedTopicBinding
import com.example.mqttclient.domain.SubscribedTopic

class SubscribedTopicsListAdapter(
    private val subscribeListener: ClickListener,
    private val deleteListener: ClickListener,
    ) : ListAdapter<SubscribedTopic, SubscribedTopicsListAdapter.ViewHolder>(SubscribedTopicsDiffCallback()) {

    class ViewHolder(val binding: ListItemSubscribedTopicBinding)
        : RecyclerView.ViewHolder(binding.root)

    class ClickListener(private val l: (SubscribedTopic) -> Unit) {
        fun onClick(subscribedTopic: SubscribedTopic) = l(subscribedTopic)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_subscribed_topic, parent, false)
        val binding = ListItemSubscribedTopicBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.topic = getItem(position)
        holder.binding.subscribeListener = subscribeListener
        holder.binding.deleteListener = deleteListener
        holder.binding.executePendingBindings()
    }
}

class SubscribedTopicsDiffCallback : DiffUtil.ItemCallback<SubscribedTopic>() {
    override fun areItemsTheSame(oldItem: SubscribedTopic, newItem: SubscribedTopic): Boolean {
        return oldItem.name == newItem.name
    }

    override fun areContentsTheSame(oldItem: SubscribedTopic, newItem: SubscribedTopic): Boolean {
        return oldItem == newItem
    }
}

@BindingAdapter("android:topicList")
fun setRecyclerViewLiveData(recyclerView: RecyclerView, liveData: LiveData<List<SubscribedTopic>>) {
    (recyclerView.adapter as SubscribedTopicsListAdapter).submitList(liveData.value)
}