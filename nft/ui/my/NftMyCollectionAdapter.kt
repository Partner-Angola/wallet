package com.joeware.android.gpulumera.nft.ui.my

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.databinding.*
import com.joeware.android.gpulumera.nft.model.NftMyCollection
import com.joeware.android.gpulumera.nft.model.NftMyCollectionCamera
import com.joeware.android.gpulumera.nft.model.NftMyCollectionCameraBox
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.jpbrothers.base.util.log.JPLog

class NftMyCollectionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class NftMyCollectionType { CAMERA, CAMERA_BOX, PHOTO }

    private var items: List<NftMyCollection>? = null
    private var type = NftMyCollectionType.CAMERA
    private lateinit var callback: OnCollectionCallback
    fun setCallBack(callback: OnCollectionCallback) {this.callback = callback}

    fun setItems(items: List<NftMyCollection>) {
        if (items.isNotEmpty()) {
            this.items = items
            type = when (items[0]) {
                is NftMyCollectionCamera -> NftMyCollectionType.CAMERA
                is NftMyCollectionCameraBox -> NftMyCollectionType.CAMERA_BOX
                is NftMyCollectionPhoto -> NftMyCollectionType.PHOTO
                else -> NftMyCollectionType.CAMERA
            }
        } else {
            this.items = null
        }
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return when (type) {
            NftMyCollectionType.CAMERA -> {
                if (position == 0) R.layout.item_nft_my_collection_camera_header
                else R.layout.item_nft_my_collection_camera
            }
            NftMyCollectionType.CAMERA_BOX -> {
                R.layout.item_nft_my_collection_camera_box
            }
            NftMyCollectionType.PHOTO -> {
//                if (position == 0) R.layout.item_nft_my_collection_photo_header
//                else R.layout.item_nft_my_collection_photo
                R.layout.item_nft_my_collection_photo
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when(viewType) {
            R.layout.item_nft_my_collection_camera_header -> CameraHeaderViewHolder(ItemNftMyCollectionCameraHeaderBinding.inflate(inflater, parent, false))
            R.layout.item_nft_my_collection_camera -> CameraViewHolder(ItemNftMyCollectionCameraBinding.inflate(inflater, parent, false))
            R.layout.item_nft_my_collection_camera_box -> CameraBoxViewHolder(ItemNftMyCollectionCameraBoxBinding.inflate(inflater, parent, false))
//            R.layout.item_nft_my_collection_photo_header -> PhotoHeaderViewHolder(ItemNftMyCollectionPhotoHeaderBinding.inflate(inflater, parent, false))
            R.layout.item_nft_my_collection_photo -> PhotoViewHolder(ItemNftMyCollectionPhotoBinding.inflate(inflater, parent, false))
            else -> EmptyViewHolder(View(parent.context))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is CameraHeaderViewHolder -> items?.let { holder.bind(it) }
            is CameraViewHolder -> (items?.get(position - 1) as? NftMyCollectionCamera)?.let { holder.bind(it) }
            is CameraBoxViewHolder -> (items?.get(position) as? NftMyCollectionCameraBox)?.let { holder.bind(it) }
//            is PhotoHeaderViewHolder -> {
//
//            }
//            is PhotoViewHolder -> (items?.get(position - 1) as? NftMyCollectionPhoto)?.let { holder.bind(it) }
            is PhotoViewHolder -> (items?.get(position) as? NftMyCollectionPhoto)?.let { holder.bind(it) }
        }
    }

    override fun getItemCount(): Int = when (type) {
//        NftMyCollectionType.PHOTO
        NftMyCollectionType.PHOTO -> items?.size ?: 0
        NftMyCollectionType.CAMERA -> items?.size?.plus(1) ?: 0
        NftMyCollectionType.CAMERA_BOX -> items?.size ?: 0
    }

    private inner class CameraHeaderViewHolder(private val binding: ItemNftMyCollectionCameraHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<NftMyCollection>) {
            binding.rvCamera.adapter = CameraHeaderAdapter().apply { setItems(items) }
        }

        inner class CameraHeaderAdapter : RecyclerView.Adapter<CameraHeaderAdapter.CameraHeaderItemViewHolder>() {

            private var items: List<NftMyCollection>? = null

            fun setItems(items: List<NftMyCollection>) {
                this.items = items
                notifyDataSetChanged()
            }

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CameraHeaderItemViewHolder {
                val inflater = LayoutInflater.from(parent.context)
                return CameraHeaderItemViewHolder(ItemNftMyCollectionCameraHeaderItemBinding.inflate(inflater, parent, false))
            }

            override fun onBindViewHolder(holder: CameraHeaderItemViewHolder, position: Int) {
                (items?.get(position) as? NftMyCollectionCamera)?.let { holder.bind(it) }
            }

            override fun getItemCount(): Int = items?.size ?: 0

            inner class CameraHeaderItemViewHolder(private val binding: ItemNftMyCollectionCameraHeaderItemBinding) : RecyclerView.ViewHolder(binding.root) {
                fun bind(item: NftMyCollectionCamera) {
                    Glide.with(binding.ivCamera.context)
                        .load(item.image)
                        .apply(
                            RequestOptions()
                                .skipMemoryCache(true)
                                .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                .placeholder(R.color.transparent)
                                .centerCrop())
                        .transform(RoundedCorners(30))
                        .transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail))
                        .into(binding.ivCamera)
                    binding.tvMainBadge.visibility = if (item.isMain) View.VISIBLE else View.INVISIBLE
                }
            }
        }
    }

    private inner class CameraViewHolder(private val binding: ItemNftMyCollectionCameraBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NftMyCollectionCamera) {

            val color = Color.parseColor(item.mainColor)
            binding.tvRank.setTextColor(color)
            binding.tvRank.text = item.rating
            binding.tvId.setTextColor(color)
            binding.tvId.text = item.id.replace("#", "")
            binding.tvSharp.backgroundTintList = ColorStateList.valueOf(color)

            Glide.with(binding.ivCamera.context)
                .load(item.image)
                .apply(
                    RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.color.transparent)
                    .centerCrop())
                .transform(RoundedCorners(30))
                .transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail))
                .into(binding.ivCamera)
        }
    }

    private inner class CameraBoxViewHolder(private val binding: ItemNftMyCollectionCameraBoxBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NftMyCollectionCameraBox) {

        }
    }

    private inner class PhotoHeaderViewHolder(private val binding: ItemNftMyCollectionPhotoHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(items: List<NftMyCollection>) {

        }
    }

    private inner class PhotoViewHolder(private val binding: ItemNftMyCollectionPhotoBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NftMyCollectionPhoto) {
            Glide.with(binding.ivImage.context)
                .load(item.image)
                .apply(RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .placeholder(R.color.transparent)
                    .centerCrop())
                .transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail))
                .into(binding.ivImage)
            binding.tvTitleId.text = item.name.replace("#","")
            binding.root.setOnClickListener { callback.onSelectedItem(item) }
        }
    }

    private inner class EmptyViewHolder(view: View): RecyclerView.ViewHolder(view)



}