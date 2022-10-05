package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.databinding.ItemChallengeEndBinding
import java.text.NumberFormat

class EndChallengeAdapter :
    RecyclerView.Adapter<EndChallengeAdapter.EndChallengeViewHolder>() {

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
    ): EndChallengeViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return EndChallengeViewHolder(
            ItemChallengeEndBinding.inflate(
                inflater,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(
        holder: EndChallengeViewHolder,
        position: Int
    ) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class EndChallengeViewHolder(val binding: ItemChallengeEndBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) {
            binding.apply {
                info = item
                isLast = (adapterPosition + 1) == items?.size

                tvPleaseVoteDesc.text =
                    String.format(
                        StringUtils.getString(R.string.prize_entry_point),
                        NumberFormat.getInstance()
                            .format(if (item.prize.isNotEmpty()) item.prize.reduce { a, b -> a + b } else 0),
                        NumberFormat.getInstance().format(item.joinTotalCount)
                    )

                // 모두 보기 버튼 클릭리스너
                btnViewVote.setOnClickListener {
                    clickListener?.onClickViewAll(item)
                }

                // 참가자리스트 어뎁터
                adapter = ChallengeVoteEndAdapter().apply {
//                    setHasStableIds(true)
                    setOnClickListener(object : ChallengeVoteEndAdapter.OnClickListener {
                        override fun onClickCell(pos: Int) {
                            clickListener?.onClickCell(item, pos)
                        }

                        override fun onClickUser(user: User) {
                            clickListener?.onClickUser(user)
                        }
                    })
                    setItems(item.join.filter { it.id.isNotEmpty() })
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickCell(challenge: Challenge, pos: Int)
        fun onClickUser(user: User)
        fun onClickViewAll(challenge: Challenge)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}