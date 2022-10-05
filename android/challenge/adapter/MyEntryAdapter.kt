package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.databinding.ItemMyEntryBinding

class MyEntryAdapter : RecyclerView.Adapter<MyEntryAdapter.JoinViewHolder>() {

    private var items: List<Join>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
        this.items = items
        notifyItemRangeInserted(0, items.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): JoinViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return JoinViewHolder(ItemMyEntryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: JoinViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class JoinViewHolder(val binding: ItemMyEntryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            binding.apply {
                isFirst = adapterPosition == 0
                binding.item = item
                root.setOnClickListener {
                    clickListener?.onClickJoin(item)
                }
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickJoin(join: Join)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}