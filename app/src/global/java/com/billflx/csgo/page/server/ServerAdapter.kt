package com.billflx.csgo.page.server

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.billflx.csgo.bean.SampQueryInfoBean
import com.valvesoftware.source.databinding.ItemServerBinding

class ServerAdapter(
    private val onItemClick: (SampQueryInfoBean) -> Unit
) : ListAdapter<SampQueryInfoBean, ServerAdapter.ServerViewHolder>(ServerDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder {
        val binding = ItemServerBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ServerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServerViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ServerViewHolder(
        private val binding: ItemServerBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: SampQueryInfoBean) {
            binding.server = item
            binding.onClick = object : OnServerClickListener {
                override fun onServerClick(server: SampQueryInfoBean) {
                    onItemClick(server)
                }
            }
            binding.executePendingBindings()
        }
    }

    private class ServerDiffCallback : DiffUtil.ItemCallback<SampQueryInfoBean>() {
        override fun areItemsTheSame(oldItem: SampQueryInfoBean, newItem: SampQueryInfoBean): Boolean {
            return oldItem.serverIP == newItem.serverIP
        }

        override fun areContentsTheSame(oldItem: SampQueryInfoBean, newItem: SampQueryInfoBean): Boolean {
            return oldItem == newItem
        }
    }

    interface OnServerClickListener {
        fun onServerClick(server: SampQueryInfoBean)
    }
} 