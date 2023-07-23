package com.example.mqttclient.ui.connect.recents_brokers

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.mqttclient.R
import com.example.mqttclient.databinding.ListItemRecentBrokerBinding
import com.example.mqttclient.domain.RecentBroker

class RecentBrokersAdapter(
    private val clickListener: ClickListener,
    private val deleteListener: ClickListener
    ) : ListAdapter<RecentBroker, RecentBrokersAdapter.ViewHolder>(RecentBrokersDiffCallback()) {

    class ViewHolder(val binding: ListItemRecentBrokerBinding)
        : RecyclerView.ViewHolder(binding.root)

    class ClickListener(private val l: (RecentBroker) -> Unit) {
        fun onClick(recentBroker: RecentBroker) = l(recentBroker)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item_recent_broker, parent, false)
        val binding = ListItemRecentBrokerBinding.bind(view)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.recentBroker = getItem(position)
        holder.binding.clickListener = clickListener
        holder.binding.deleteListener = deleteListener
        holder.binding.executePendingBindings()
    }
}

class RecentBrokersDiffCallback : DiffUtil.ItemCallback<RecentBroker>() {
    override fun areItemsTheSame(oldItem: RecentBroker, newItem: RecentBroker): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: RecentBroker, newItem: RecentBroker): Boolean {
        return oldItem == newItem
    }
}

@BindingAdapter("android:brokerList")
fun setRecyclerViewLiveData(recyclerView: RecyclerView, liveData: LiveData<List<RecentBroker>>) {
    (recyclerView.adapter as RecentBrokersAdapter).submitList(liveData.value)
}