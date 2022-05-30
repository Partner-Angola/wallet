package com.joeware.android.gpulumera.nft.ui.mint

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.databinding.ItemNftMintingChallBinding

class NftMintingCompleteAdapter: RecyclerView.Adapter<NftMintingCompleteAdapter.NftMintingCompleteViewHolder>() {

    private var items: List<Challenge>? = null

    fun setItems(items: List<Challenge>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NftMintingCompleteViewHolder {
        return NftMintingCompleteViewHolder(ItemNftMintingChallBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: NftMintingCompleteViewHolder, position: Int) {
        items?.get(position)?.let { holder.bind(it) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class NftMintingCompleteViewHolder(val binding: ItemNftMintingChallBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) {
            val context = binding.root.context
            binding.tvTitle.text = item.title
            Glide.with(context).load(item.image).into(binding.ivMain)
            binding.root.setOnClickListener {moveChallenge?.let { it() }}
            binding.btnShowChallenge.setOnClickListener { moveChallenge?.let { it() } }
        }
    }

    private var moveChallenge: (() -> Unit)? = null

    fun setMoveChallenge(func: () -> Unit) {
        this.moveChallenge = func
    }
}