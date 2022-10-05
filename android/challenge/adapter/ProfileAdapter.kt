package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.databinding.ItemProfileBinding
import jp.wasabeef.glide.transformations.BlurTransformation

class ProfileAdapter : RecyclerView.Adapter<ProfileAdapter.CellViewHolder>() {

    private var items: List<Join>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Join>) {
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
    ): CellViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return CellViewHolder(ItemProfileBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class CellViewHolder(val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Join) {
            val context = binding.root.context
            binding.apply {
                ivMedal.visibility = if (item.rank == 1) View.VISIBLE else View.GONE
                tvUnder.visibility = if (!item.isActive) View.VISIBLE else View.GONE
                if (!item.isActive) {
                    Glide.with(context)
                        .load(item.image)
                        .placeholder(R.drawable.transparent)
                        .apply(
                            RequestOptions.bitmapTransform(
                                MultiTransformation(
                                    CenterCrop(),
                                    BlurTransformation()
                                )
                            )
                        )
                        .transition(DrawableTransitionOptions().transition(R.anim.fade_in))
                        .into(ivContent)
                } else {
                    bindImage(ivContent, item.image, 0f)
                }
                tvUnder.text = if (item.isBlocked) context.resources.getString(R.string.blocked)
                else if (!item.isActive) context.resources.getString(R.string.under_inspection)
                else ""

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
        fun onClickCell(join: Join)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}