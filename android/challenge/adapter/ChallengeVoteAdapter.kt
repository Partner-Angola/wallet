package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.databinding.ItemChallengeVoteBinding

class ChallengeVoteAdapter : RecyclerView.Adapter<ChallengeVoteAdapter.ChallengeViewHolder>() {

    private var items: List<Challenge>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Challenge>) {
        val positionStart = this.items?.size ?: 0
        val itemCount = items.size - positionStart

        this.items = items
        if (items.isNotEmpty()) {
            notifyItemRangeChanged(positionStart, itemCount)
        } else {
            notifyItemRangeRemoved(0, positionStart)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChallengeViewHolder(ItemChallengeVoteBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class ChallengeViewHolder(val binding: ItemChallengeVoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) {
            binding.apply {
                isFirst = adapterPosition == 0
                info = item

                root.setOnClickListener {
                    clickListener?.onClickCell(item)
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickCell(challenge: Challenge)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}