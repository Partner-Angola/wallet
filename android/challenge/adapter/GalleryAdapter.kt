package com.joeware.android.gpulumera.challenge.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.widget.gallery.Gallery
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ItemProfileBinding
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto

class GalleryAdapter : RecyclerView.Adapter<GalleryAdapter.ViewHolder>() {

    private var items: List<Gallery>? = null

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<Gallery>) {
        val positionStart = this.items?.size ?: 0

        this.items = items

        if (positionStart > 0) {
            notifyItemRangeRemoved(0, positionStart)
        }
        notifyItemRangeChanged(0, items.size)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return ViewHolder(ItemProfileBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class ViewHolder(val binding: ItemProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Gallery) {
            val context = binding.root.context
            Join().apply { status = C.ChallengeStatus.active.toString() }.let {
                binding.ivMedal.visibility = if (it.rank == 1) View.VISIBLE else View.GONE
                binding.tvUnder.visibility = if (!it.isActive) View.VISIBLE else View.GONE
            }

            Glide.with(context)
                .load(item.path)
                .placeholder(R.drawable.transparent)
                .centerCrop()
                .transition(DrawableTransitionOptions().transition(R.anim.fade_in))
                .into(binding.ivContent)
            binding.root.setOnClickListener {
                clickListener?.onClickChallenge(item)
            }
        }
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickChallenge(gallery: Gallery)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}