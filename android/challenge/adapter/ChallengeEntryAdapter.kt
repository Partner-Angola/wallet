package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ItemChallengeDetailHeaderBinding
import com.joeware.android.gpulumera.databinding.ItemVoteListBinding
import java.text.NumberFormat

class ChallengeEntryAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var CELL_HEADER = 0
        private var CELL_ITEM = 1
    }

    lateinit var challenge: Challenge
    private var allCount: Int = 0
    private var items: List<Join>? = null
    private var myItems: List<Join>? = null

    private val itemDecoration = HorizontalSpaceItemDecoration(
        12,
        includeLast = true
    )

    @SuppressLint("NotifyDataSetChanged")
    fun setAllCount(count: Int) {
        allCount = count
        notifyItemChanged(0)
    }

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

    @SuppressLint("NotifyDataSetChanged")
    fun setChallengeInfo(c: Challenge) {
        challenge = c
        notifyItemChanged(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMyItems(items: List<Join>) {
        myItems = items.filter { it.status == C.ChallengeStatus.active.toString() }
        notifyItemChanged(0)
    }

    override fun getItemViewType(position: Int): Int {
        return if (position == 0) {
            CELL_HEADER
        } else {
            CELL_ITEM
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == CELL_HEADER)
            HeaderViewHolder(ItemChallengeDetailHeaderBinding.inflate(inflater, parent, false))
        else CellViewHolder(ItemVoteListBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == CELL_HEADER) {
            (holder as HeaderViewHolder).bind()
        } else {
            items?.get(position - 1)
                ?.let { item -> (holder as CellViewHolder).bind(item) }
        }
    }

    override fun getItemCount(): Int = (items?.size ?: 0) + 1

    inner class HeaderViewHolder(val binding: ItemChallengeDetailHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.apply {
                val context = binding.root.context
                descriptionOpen = true
                ruleOpen = true
                info = challenge


                val messageStr = context.getString(R.string.name_in_winner)
                val pointStr = String.format(
                    StringUtils.getString(R.string.prize_point),
                    NumberFormat.getInstance()
                        .format(if (challenge.prize.isNotEmpty()) challenge.prize.reduce { a, b -> a + b } else 0)
                )
                val message = "$messageStr $pointStr"
                val ssb = SpannableStringBuilder(message)
                ssb.setSpan(ForegroundColorSpan(ContextCompat.getColor(context, R.color.main_color)), messageStr.length, message.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                tvInfo.text = ssb

                // 설명 접기/펼치기
                rlyDescription.setOnClickListener {
                    descriptionOpen = !descriptionOpen
                }

                // 룰 접기/펼치기
                rlyRule.setOnClickListener {
                    ruleOpen = !ruleOpen
                }

                // 내 출품작현시여부
                rlyMyList.visibility = boolean2Visibility(myItems?.isNotEmpty() == true)

                // 내 출품작 어뎁터
                myAdapter = MyEntryAdapter().apply {
                    setOnClickListener(object : MyEntryAdapter.OnClickListener {
                        override fun onClickJoin(join: Join) {
                            clickListener?.onClickMyEntry(join)
                        }
                    })
                    myItems?.let { setItems(it) }
                }

                // 내 출품작리스트
                rcvMyEntry.apply {
                    removeItemDecoration(itemDecoration)
                    addItemDecoration(itemDecoration)
                }

                // 모든 출품작갯수
                tvAllEntryCount.text =
                    String.format("(%s)", NumberFormat.getInstance().format(allCount))
            }
        }
    }

    inner class CellViewHolder(val binding: ItemVoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            binding.imageUrl = item.image
            binding.isVoted = item.voted
            binding.root.setOnClickListener {
                clickListener?.onClickItem(adapterPosition)
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickItem(pos: Int)
        fun onClickMyEntry(join: Join)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}