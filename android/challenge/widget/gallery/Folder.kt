package com.joeware.android.gpulumera.challenge.widget.gallery

class Folder(var folderName: String) {
    private var images: ArrayList<Gallery>

    fun getImages(): ArrayList<Gallery> {
        return images
    }

    fun setImages(images: List<Gallery>) {
        this.images = images as ArrayList<Gallery>
    }

    init {
        images = ArrayList()
    }
}
