package com.joeware.android.gpulumera.challenge.widget.gallery

interface ImageLoaderListener {
    fun onImageLoaded(images: List<Gallery>?, folders: List<Folder>?)
    fun onFailed(throwable: Throwable?)
}
