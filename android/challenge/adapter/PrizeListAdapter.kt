package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ItemPrizeListHeaderBinding
import com.joeware.android.gpulumera.databinding.ItemVoteListBinding
import java.text.NumberFormat
import kotlin.math.min

class PrizeListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var CELL_HEADER = 0
        private var CELL_ITEM = 1
    }

    lateinit var challenge: Challenge
    private var totalItemCount = 0
    private var topItems: List<Join>? = null
    private var items: List<Join>? = null

    private val itemDecoration = VerticalSpaceItemDecoration(25, includeHeader = true)

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
        if (items.isNotEmpty()) {
            this.topItems = items.subList(0, min(items.size, 5))
            this.topItems = this.topItems?.filter { it.prize > 0 }
            this.items = items.filter { it.prize <= 0 }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setItemCount(count: Int) {
        totalItemCount = count
        notifyItemChanged(0)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setChallengeInfo(c: Challenge) {
        challenge = c
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
            HeaderViewHolder(ItemPrizeListHeaderBinding.inflate(inflater, parent, false))
        else
            CellViewHolder(ItemVoteListBinding.inflate(inflater, parent, false))
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

    inner class HeaderViewHolder(val binding: ItemPrizeListHeaderBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            val context = binding.root.context
            binding.apply {
                info = challenge

                tvInfo.text = String.format(
                    context.resources.getString(
                        R.string.prize_entry_point,
                        NumberFormat.getInstance()
                            .format(if (challenge.prize.isNotEmpty()) challenge.prize.reduce { a, b -> a + b } else 0),
                        NumberFormat.getInstance().format(challenge.joinTotalCount)
                    )
                )
                tvEntryCount.text = String.format(
                    context.resources.getString(
                        R.string.all_count,
                        NumberFormat.getInstance().format(totalItemCount)
                    )
                )
                topAdapter = PrizeTopListAdapter().apply {
//                    setHasStableIds(true)
                    setOnClickListener(object : PrizeTopListAdapter.OnClickListener {
                        override fun onClickItem(pos: Int) {
                            clickListener?.onClickItem(pos)
                        }
                    })
                }
                topItems?.let { topAdapter?.setItems(it) }

                rcvTop.apply {
                    removeItemDecoration(itemDecoration)
                    addItemDecoration(itemDecoration)

                    (layoutManager as GridLayoutManager).spanSizeLookup =
                        object : GridLayoutManager.SpanSizeLookup() {
                            override fun getSpanSize(position: Int): Int {
                                return when (position) {
                                    0 -> 2
                                    else -> 1
                                }
                            }
                        }
                }
            }
        }
    }

    inner class CellViewHolder(val binding: ItemVoteListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            binding.imageUrl = item.image
            binding.isVoted = item.voted
            binding.root.setOnClickListener {
                clickListener?.onClickItem(adapterPosition + (topItems?.lastIndex ?: 0))
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickItem(pos: Int)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}