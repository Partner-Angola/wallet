package com.joeware.android.gpulumera.challenge.widget.gallery

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import android.text.TextUtils
import android.webkit.MimeTypeMap
import java.io.File
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ImageFileLoader(private val context: Context) {
    private var executorService: ExecutorService? = null
    private val projection = arrayOf(
        MediaStore.Images.Media._ID,
        MediaStore.Images.Media.DISPLAY_NAME,
        MediaStore.Images.Media.DATA,
        MediaStore.Images.Media.BUCKET_DISPLAY_NAME
    )

    fun loadDeviceImages(
        isFolderMode: Boolean,
        includeVideo: Boolean,
        includeAnimation: Boolean,
        excludedImages: ArrayList<File?>?,
        listener: ImageLoaderListener
    ) {
        getExecutorService()?.execute(
            ImageLoadRunnable(
                isFolderMode,
                includeVideo,
                includeAnimation,
                excludedImages,
                listener
            )
        )
    }

    fun abortLoadImages() {
        if (executorService != null) {
            executorService!!.shutdown()
            executorService = null
        }
    }

    private fun getExecutorService(): ExecutorService? {
        if (executorService == null) {
            executorService = Executors.newSingleThreadExecutor()
        }
        return executorService
    }

    private inner class ImageLoadRunnable(
        private val isFolderMode: Boolean,
        private val includeVideo: Boolean,
        private val includeAnimation: Boolean,
        private val excludedImages: ArrayList<File?>?,
        private val listener: ImageLoaderListener
    ) :
        Runnable {

        override fun run() {
            val cursor: Cursor? = if (includeVideo) {
                val selection = (MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE + " OR "
                        + MediaStore.Files.FileColumns.MEDIA_TYPE + "="
                        + MediaStore.Files.FileColumns.MEDIA_TYPE_VIDEO)
                context.contentResolver.query(
                    MediaStore.Files.getContentUri("external"), projection,
                    selection, null, MediaStore.Images.Media.DATE_ADDED
                )
            } else {
                context.contentResolver.query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection,
                    null, null, MediaStore.Images.Media.DATE_ADDED
                )
            }
            if (cursor == null) {
                listener.onFailed(NullPointerException())
                return
            }
            val temp: MutableList<Gallery> = ArrayList()
            var folderMap: MutableMap<String?, Folder>? = null
            if (isFolderMode) {
                folderMap = HashMap()
            }
            if (cursor.moveToLast()) {
                do {
                    val id: Long = cursor.getLong(cursor.getColumnIndex(projection[0]))
                    val name: String = cursor.getString(cursor.getColumnIndex(projection[1]))
                    val path: String = cursor.getString(cursor.getColumnIndex(projection[2]))
                    val bucket: String? = cursor.getString(cursor.getColumnIndex(projection[3]))
                    val file: File? = makeSafeFile(path)
                    if (file != null) {
                        if (excludedImages != null && excludedImages.contains(file)) continue
                        val image = Gallery(id, name, path)
                        if (!includeAnimation) {
                            if (isGifFormat(image)) continue
                        }
                        temp.add(image)
                        if (folderMap != null && bucket != null) {
                            var folder = folderMap[bucket]
                            if (folder == null) {
                                folder = Folder(bucket)
                                folderMap[bucket] = folder
                            }
                            folder.getImages().add(image)
                        }
                    }
                } while (cursor.moveToPrevious())
            }
            cursor.close()

            /* Convert HashMap to ArrayList if not null */
            var folders: List<Folder>? = null
            if (folderMap != null) {
                folders = ArrayList(folderMap.values)
            }
            listener.onImageLoaded(temp, folders)
        }

    }

    companion object {
        private fun makeSafeFile(path: String?): File? {
            return if (path == null || path.isEmpty()) {
                null
            } else try {
                File(path)
            } catch (ignored: Exception) {
                null
            }
        }

        fun isGifFormat(image: Gallery): Boolean {
            val extension = getExtension(image.path ?: "")
            return extension.equals("gif", ignoreCase = true)
        }

        private fun getExtension(path: String): String {
            val extension = MimeTypeMap.getFileExtensionFromUrl(path)
            if (!TextUtils.isEmpty(extension)) {
                return extension
            }
            return if (path.contains(".")) {
                path.substring(path.lastIndexOf(".") + 1, path.length)
            } else {
                ""
            }
        }
    }

}
