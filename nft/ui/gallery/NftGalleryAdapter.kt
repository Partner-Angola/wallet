package com.joeware.android.gpulumera.nft.ui.gallery

import android.annotation.SuppressLint
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.databinding.ItemNftGalleryBinding
import com.joeware.android.gpulumera.nft.model.GalleryItem

class NftGalleryAdapter(var activity: AppCompatActivity) : RecyclerView.Adapter<NftGalleryAdapter.NftGalleryViewHolder>() {

    private var items: List<GalleryItem>? = null
    private lateinit var callback: OnGalleryCallback
    fun setCallBack(callback: OnGalleryCallback) {this.callback = callback}

    @SuppressLint("NotifyDataSetChanged")
    fun setItems(items: List<GalleryItem>) {
        this.items = items
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateAllItem() = notifyDataSetChanged()
    fun updateItem(position: Int) = notifyItemChanged(position)
    fun deleteItem(position: Int) = notifyItemRemoved(position)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NftGalleryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return NftGalleryViewHolder(ItemNftGalleryBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: NftGalleryViewHolder, position: Int) {
        items?.get(position)?.let { item -> holder.bind(item) }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class NftGalleryViewHolder(private val binding: ItemNftGalleryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GalleryItem) {
            Glide.with(binding.ivGallery.context)
                .load(Uri.parse("file://${item.path}"))
                .apply(RequestOptions()
                        .skipMemoryCache(true)
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                        .placeholder(R.color.transparent)
                        .centerCrop())
                .transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail))
                .into(binding.ivGallery)
            binding.ivGallery.setOnClickListener {callback.onSelectedItem(item)}
            binding.cbGallery.setOnClickListener { item.isSelect = !item.isSelect!!;callback.onCheckBoxSelectedItem(adapterPosition)}
            if(item.isSelect == true)binding.cbGallery.setBackgroundResource(R.drawable.back_rounded_corners_with_only_stroke_circle_main_color)
            else binding.cbGallery.setBackgroundResource(R.drawable.back_rounded_corners_with_only_stroke_circle)
            binding.cbGallery.visibility = View.GONE
            binding.cbGallery.visibility = View.GONE
        }
    }
}