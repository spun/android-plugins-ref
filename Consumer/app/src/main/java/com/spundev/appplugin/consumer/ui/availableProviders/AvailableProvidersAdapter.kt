package com.spundev.appplugin.consumer.ui.availableProviders

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.appplugin.consumer.databinding.RvAvailableProviderItemBinding
import com.spundev.appplugin.consumer.model.ProviderData

class AvailableProvidersAdapter internal constructor(
    val context: Context,
    val clickListener: (downloadedFile: ProviderData) -> Unit,
) : ListAdapter<ProviderData, AvailableProvidersAdapter.ProviderViewHolder>(object :
    DiffUtil.ItemCallback<ProviderData>() {
    override fun areItemsTheSame(oldItem: ProviderData, newItem: ProviderData) =
        oldItem.authority == newItem.authority

    override fun areContentsTheSame(oldItem: ProviderData, newItem: ProviderData) =
        oldItem == newItem
}) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProviderViewHolder {
        val binding: RvAvailableProviderItemBinding =
            RvAvailableProviderItemBinding.inflate(inflater, parent, false)
        return ProviderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProviderViewHolder, position: Int) =
        holder.bindTo(getItem(position))

    inner class ProviderViewHolder(
        private val binding: RvAvailableProviderItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: ProviderData?) {
            if (item != null) {
                binding.availableProviderTitle.text = item.title
                binding.availableProviderPackageName.text = item.packageName
                binding.root.setOnClickListener { clickListener(item) }
            }
        }
    }
}