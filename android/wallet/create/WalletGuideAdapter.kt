package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joeware.android.gpulumera.account.wallet.model.WalletGuide
import com.joeware.android.gpulumera.databinding.ItemWalletGuideBinding

class WalletGuideAdapter : RecyclerView.Adapter<WalletGuideAdapter.WalletGuideViewHolder>() {

    private var items: List<WalletGuide>? = null

    fun setItems(items: List<WalletGuide>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletGuideViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return WalletGuideViewHolder(ItemWalletGuideBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: WalletGuideViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class WalletGuideViewHolder(private val binding: ItemWalletGuideBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletGuide) {
            val context = binding.root.context
            binding.tvTitle.text = item.title
            item.contents?.let {
                binding.tvSubTitle.visibility = View.VISIBLE
                binding.tvSubTitle.text = it
                binding.tvSubTitle.setTextColor(item.contentsColor)
            } ?: run { binding.tvSubTitle.visibility = View.GONE }
            item.bottomImagesRes?.let { res ->
                binding.ivBottomIcon.visibility = View.VISIBLE
                Glide.with(context).load(res).into(binding.ivBottomIcon)
            } ?: run { binding.ivBottomIcon.visibility = View.GONE }
        }
    }
}