package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.graphics.Color
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeViewModel
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ItemChallengeHeaderBinding
import com.joeware.android.gpulumera.databinding.ItemChallengeManualBinding
import com.joeware.android.gpulumera.databinding.ItemChallengeNoticeBinding
import com.joeware.android.gpulumera.databinding.ItemChallengeParticipateBinding
import java.text.NumberFormat

class ProgressChallengeAdapter(val viewModel: ChallengeViewModel) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var CELL_NOTICE = 0
        private var CELL_MANUAL = 1
        private var CELL_HEADER = 2
        private var CELL_ITEM = 3
    }

    private var voteItems: List<Challenge>? = null
    private var participateItems: List<Challenge>? = null

    private val hDecoration = HorizontalSpaceItemDecoration(12, includeLast = true)
    private var voteAdapter: ChallengeVoteAdapter? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Challenge>) {
        val positionStart = this.participateItems?.size ?: 0
        val itemCount = items.size - positionStart
        this.participateItems = items
        if (items.isNotEmpty()) {
            notifyItemRangeChanged(positionStart + 2, itemCount)
        } else {
            notifyItemRangeRemoved(2, positionStart)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setHeaderItems(items: List<Challenge>) {
        this.voteItems = items
        voteAdapter?.setItems(items)
        notifyItemChanged(1)
    }

    override fun getItemViewType(position: Int): Int {
        return when(position) {
//            0 -> CELL_NOTICE
            0 -> CELL_MANUAL
            1 -> CELL_HEADER
            else -> CELL_ITEM
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            CELL_NOTICE -> ChallengeNoticeViewHolder(ItemChallengeNoticeBinding.inflate(inflater, parent, false))
            CELL_MANUAL -> ChallengeManualViewHolder(ItemChallengeManualBinding.inflate(inflater, parent, false))
            CELL_HEADER -> ChallengeHeaderViewHolder(ItemChallengeHeaderBinding.inflate(inflater, parent, false))
            else -> ChallengeViewHolder(ItemChallengeParticipateBinding.inflate(inflater, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is ChallengeNoticeViewHolder -> holder.bind()
            is ChallengeManualViewHolder -> holder.bind()
            is ChallengeHeaderViewHolder -> voteItems?.let { holder.bind(it) }
            is ChallengeViewHolder -> participateItems?.get(position - 2)?.let { item -> holder.bind(item) }
        }
    }

    override fun getItemCount(): Int = (participateItems?.size ?: 2) + 2

    inner class ChallengeNoticeViewHolder(val binding: ItemChallengeNoticeBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener { clickListener?.onClickNotice() }
        }
    }

    inner class ChallengeManualViewHolder(val binding: ItemChallengeManualBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.root.setOnClickListener { clickListener?.onClickManual() }
        }
    }

    inner class ChallengeHeaderViewHolder(val binding: ItemChallengeHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<Challenge>) {
            binding.apply {
                /*tvPleaseVoteDesc.text = String.format(
                    context.resources.getString(R.string.prize_entry_point),
                    "100,000",
                    "1,200"
                )*/

                lyHeader.visibility = boolean2Visibility(items.isNotEmpty())

                // 모두 보기버튼 클릭리스너
                btnViewVote.setOnClickListener {
                    clickListener?.onClickViewAll()
                }

                adapter = ChallengeVoteAdapter().apply {
//                    setHasStableIds(true)
                    setOnClickListener(object : ChallengeVoteAdapter.OnClickListener {
                        override fun onClickCell(challenge: Challenge) {
                            clickListener?.onClickCell(challenge)
                        }
                    })
                    setItems(items)
                }
                voteAdapter = adapter

                rvVote.apply {
                    removeItemDecoration(hDecoration)
                    addItemDecoration(hDecoration)

                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                            super.onScrolled(recyclerView, dx, dy)

                            val layoutManager = recyclerView.layoutManager
                            val totalItemCount = layoutManager!!.itemCount
                            val lastVisibleItemPos = when (layoutManager) {
                                is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                                is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                                is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(
                                    null
                                )[0]
                                else -> 0
                            }

                            if (
                                totalItemCount > 0 &&
                                (lastVisibleItemPos + 2) >= totalItemCount
                            ) {
                                viewModel.getVoteChallengeList()
                            }
                        }
                    })
                }
            }
        }
    }

    inner class ChallengeViewHolder(val binding: ItemChallengeParticipateBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Challenge) {
            val context = binding.root.context
            binding.apply {
                info = item

                // 참가종료일자
                tvDayLeft.text = item.participateLeftTime
                // 포인트

                val messageStr = context.getString(R.string.name_in_winner)
                val pointStr = String.format(
                    context.resources.getString(R.string.point_p),
                    NumberFormat.getInstance()
                        .format(if (item.prize.isNotEmpty()) item.prize.reduce { a, b -> a + b } else 0)
                )

                val message = "$messageStr $pointStr"
                val ssb = SpannableStringBuilder(message)
                ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color)), messageStr.length, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvDesc.text = ssb

                // 이미지 클릭리스너
                ivContent.setOnClickListener {
                    clickListener?.onClickCellBtn(item)
                }
                // 참가하기버튼 클릭리스너
                btnParticipate.setOnClickListener {
                    clickListener?.onClickCellBtn(item)
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickCell(challenge: Challenge)
        fun onClickCellBtn(challenge: Challenge)
        fun onClickViewAll()
        fun onClickNotice()
        fun onClickManual()
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}