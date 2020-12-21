package com.spundev.appplugin.consumer.ui.providerDetails

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.spundev.appplugin.consumer.databinding.RvProviderDetailsItemBinding
import com.spundev.appplugin.consumer.model.PetData


class ProviderDetailsAdapter internal constructor(
    val context: Context,
    val onclick: (item: PetData) -> Unit

) : ListAdapter<PetData, ProviderDetailsAdapter.ContentViewHolder>(object :
    DiffUtil.ItemCallback<PetData>() {
    override fun areItemsTheSame(oldItem: PetData, newItem: PetData) =
        oldItem == newItem

    override fun areContentsTheSame(oldItem: PetData, newItem: PetData) =
        oldItem == newItem
}) {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val binding: RvProviderDetailsItemBinding =
            RvProviderDetailsItemBinding.inflate(inflater, parent, false)
        return ContentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) =
        holder.bindTo(getItem(position))

    inner class ContentViewHolder(
        private val binding: RvProviderDetailsItemBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bindTo(item: PetData?) {
            if (item != null) {
                binding.providerPetNameText.text = item.name
                binding.providerPetDescriptionText.text = item.description
                binding.root.setOnClickListener { onclick(item) }
            } else {
                binding.providerPetNameText.text = ""
                binding.providerPetDescriptionText.text = ""
                binding.root.setOnClickListener(null)

            }
        }
    }
}