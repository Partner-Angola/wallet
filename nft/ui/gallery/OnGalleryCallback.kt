package com.joeware.android.gpulumera.nft.ui.gallery

import com.joeware.android.gpulumera.nft.model.GalleryItem

interface OnGalleryCallback {
    fun onSelectedItem(item: GalleryItem)
    fun onCheckBoxSelectedItem(idx: Int)
}