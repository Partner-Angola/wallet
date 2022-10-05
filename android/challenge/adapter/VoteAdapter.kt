package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.databinding.ItemChallengeParticipateBinding
import com.joeware.android.gpulumera.util.TimeUtil

class VoteAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

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
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ChallengeViewHolder(ItemChallengeParticipateBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        items?.get(position)
            ?.let { item -> (holder as ChallengeViewHolder).bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class ChallengeViewHolder(val binding: ItemChallengeParticipateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) {
            val context = binding.root.context
            binding.apply {
                info = item
                paddingTop = adapterPosition == 0

                // 투표종료일자
                tvDayLeft.text = item.voteLeftTime
                tvDesc.text = String.format(
                    context.resources.getString(R.string.vote_go_on),
                    TimeUtil.formatDate(item.voteEndDate, "yyyy.MM.dd HH:mm")
                )
                btnParticipate.setText(R.string.vote)

                // 투료하기버튼 클릭리스너
                btnParticipate.setOnClickListener {
                    clickListener?.onClickBtn(item)
                }
                ivContent.setOnClickListener {
                    clickListener?.onClickBtn(item)
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickBtn(challenge: Challenge)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}