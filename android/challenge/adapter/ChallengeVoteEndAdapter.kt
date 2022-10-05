package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.databinding.ItemChallengeVoteEndBinding

class ChallengeVoteEndAdapter :
    RecyclerView.Adapter<ChallengeVoteEndAdapter.ChallengeViewHolder>() {

    private var items: List<Join>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
        this.items = items
        notifyItemRangeInserted(0, items.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChallengeViewHolder(ItemChallengeVoteEndBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: ChallengeViewHolder,
        position: Int
    ) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class ChallengeViewHolder(val binding: ItemChallengeVoteEndBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            val context = binding.root.context
            binding.apply {
                isFirst = adapterPosition == 0
                binding.item = item

                // 순위

                tvRanking.text = "${adapterPosition + 1}${when(adapterPosition + 1) {
                    1 -> context.getString(R.string.ranking_st)
                    2 -> context.getString(R.string.ranking_nd)
                    3 -> context.getString(R.string.ranking_rd)
                    4 -> context.getString(R.string.ranking_th)
                    5 -> context.getString(R.string.ranking_th)
                    else -> ""
                }}"

                root.setOnClickListener {
                    clickListener?.onClickCell(adapterPosition)
                }
                ivProfile.setOnClickListener {
                    clickListener?.onClickUser(item.user)
                }
                tvNickname.setOnClickListener {
                    clickListener?.onClickUser(item.user)
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickCell(pos: Int)
        fun onClickUser(user: User)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}