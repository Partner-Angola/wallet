package com.joeware.android.gpulumera.nft.model


enum class GalleryMode {
    PICTURE, VIDEO
}

data class GalleryItem(
    val id: Int,
    val path: String?,
    val folderPath: String?,
    val title: String,
    val times: Long,
    val parseTime: String?,
    val mode: GalleryMode,
    val videoDuration: Long,
    val videoWidth: Int,
    val videoHeight: Int,
    val videoRotate: Int,
    var isSelect: Boolean?
)

data class GalleryFolderItem(
    val folderName: String,
    val folderItems: MutableList<GalleryItem>
)