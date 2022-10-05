package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.databinding.ItemPrizeGoldBinding
import com.joeware.android.gpulumera.databinding.ItemPrizeVoteBinding

class PrizeTopListAdapter :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private var CELL_HEADER = 0
        private var CELL_ITEM = 1
    }

    private var goldItem: Join? = null
    private var items: List<Join>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
        if (items.isNotEmpty()) {
            this.goldItem = items[0]
        }
        this.items = items
        notifyDataSetChanged()
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
            HeaderViewHolder(ItemPrizeGoldBinding.inflate(inflater, parent, false))
        else
            CellViewHolder(ItemPrizeVoteBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        if (getItemViewType(position) == CELL_HEADER) {
            goldItem?.let { (holder as HeaderViewHolder).bind(it) }
        } else {
            items?.get(position)
                ?.let { item -> (holder as CellViewHolder).bind(item) }
        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class HeaderViewHolder(val binding: ItemPrizeGoldBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            val context = binding.root.context
            binding.apply {
                join = item

                tvRanking.text = "1${context.resources.getString(R.string.ranking_st)}"
                root.setOnClickListener {
                    clickListener?.onClickItem(adapterPosition)
                }
            }
        }
    }

    inner class CellViewHolder(val binding: ItemPrizeVoteBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            val context = binding.root.context
            binding.apply {
                odd = adapterPosition % 2 == 1
                binding.item = item

                tvRanking.text = "${adapterPosition + 1}${when(adapterPosition + 1) {
                    1 -> context.getString(R.string.ranking_st)
                    2 -> context.getString(R.string.ranking_nd)
                    3 -> context.getString(R.string.ranking_rd)
                    4 -> context.getString(R.string.ranking_th)
                    5 -> context.getString(R.string.ranking_th)
                    else -> ""
                }}"
                root.setOnClickListener {
                    clickListener?.onClickItem(adapterPosition)
                }
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