package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.databinding.ItemVoteListBinding

class VoteListAdapter : RecyclerView.Adapter<VoteListAdapter.ChallengeViewHolder>() {

    private var items: List<Join>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
        val positionStart = this.items?.size ?: 0
        val itemCount = items.size - positionStart

        this.items = items
        if (items.isNotEmpty()) {
            if (itemCount == 0) {
                notifyItemRangeChanged(0, items.size)
            } else {
                notifyItemRangeChanged(positionStart, itemCount)
            }
        } else {
            notifyItemRangeRemoved(0, positionStart)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChallengeViewHolder(ItemVoteListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ChallengeViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class ChallengeViewHolder(val binding: ItemVoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            binding.imageUrl = item.image
            binding.isVoted = item.voted
            binding.root.setOnClickListener {
                clickListener?.onClickJoin(adapterPosition)
            }

        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickJoin(pos: Int)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}