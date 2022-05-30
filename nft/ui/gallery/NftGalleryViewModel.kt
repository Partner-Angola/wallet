package com.joeware.android.gpulumera.nft.ui.gallery

import android.annotation.SuppressLint
import android.content.Context
import android.media.MediaMetadataRetriever
import android.provider.BaseColumns
import android.provider.MediaStore
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.nft.model.GalleryFolderItem
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.nft.model.GalleryMode
import com.joeware.android.gpulumera.util.SingleLiveEvent
import io.reactivex.Observable
import io.reactivex.subjects.BehaviorSubject
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class NftGalleryViewModel : CandyViewModel() {

    /*****************************************************************************
     * 기본 변수들.
     ****************************************************************************/

    private val _notifiyList = SingleLiveEvent<Void>()
    private val _clickMint = MutableLiveData<ArrayList<String>>()

    val notifyList : LiveData<Void> = _notifiyList
    val clickMint : LiveData<ArrayList<String>> = _clickMint

    fun clickMint(){
        _clickMint.postValue(arrayListOf());
    }

    fun notifyList(){
        _notifiyList.call()
    }

    fun selectPhoto(id: Int, selected: Boolean) {
        _galleryPictureItems.value?.filterIndexed { index, galleryItem ->
            if (galleryItem.id == id) {
                galleryItem.isSelect = selected
                _updateGalleryPictureItem.postValue(index)
            }
            false
        }
    }

    fun getSelectedPhoto(): ArrayList<GalleryItem> {
        var listItem: ArrayList<GalleryItem> = arrayListOf()
        _galleryPictureItems.value?.forEach { item ->
            if (item.isSelect == true) listItem.add(item)
        }
        return listItem
    }


    /*****************************************************************************
     * 사진 리스트
     ****************************************************************************/
    private val _galleryPictureItems = BehaviorSubject.create<List<GalleryItem>>() // 갤러리 현재 폴더 사진 리스트
    private val _updateGalleryPictureItem = MutableLiveData<Int>()

    val galleryPictureItems: Observable<List<GalleryItem>> get() = _galleryPictureItems
    val updateGalleryPictureItem: LiveData<Int> get() = _updateGalleryPictureItem

    fun initLoadDeviceMedia(context: Context) {
        runDisposable(Observable.create<Pair<GalleryMode, GalleryFolderItem>> { emitter ->
            emitter.onNext(Pair(GalleryMode.PICTURE, loadFolderList(context, GalleryMode.PICTURE)))
            emitter.onComplete()
        }, { pairData ->
            if (pairData.first == GalleryMode.PICTURE) {
                _galleryPictureItems.onNext(pairData.second.folderItems)
            }
        }, {
            it.printStackTrace()
        })
    }

    @SuppressLint("Recycle", "SimpleDateFormat")
//    private fun loadFolderList(context: Context, mode: GalleryMode) : List<GalleryFolderItem> {
    private fun loadFolderList(context: Context, mode: GalleryMode) : GalleryFolderItem {
        val format = SimpleDateFormat("yyyy.MM.dd")
        val fileList = mutableListOf<GalleryItem>()
        val folderMap = mutableMapOf<String, GalleryFolderItem>()

        val uri = if (mode == GalleryMode.PICTURE) MediaStore.Images.Media.EXTERNAL_CONTENT_URI else MediaStore.Video.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf(
            BaseColumns._ID,
            MediaStore.Images.ImageColumns.TITLE,
            MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
            MediaStore.MediaColumns.DATA,
            MediaStore.MediaColumns.DISPLAY_NAME,
            MediaStore.MediaColumns.DATE_MODIFIED,
            MediaStore.Images.ImageColumns.SIZE
        )
        context.contentResolver.query(
            uri,
            projection,
            null,
            null,
            MediaStore.MediaColumns.DATE_ADDED + " DESC"
        )?.let { cursor ->
            val columnId: Int = cursor.getColumnIndex(BaseColumns._ID)
            val columnBucket: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME)
            val columnData: Int = cursor.getColumnIndex(MediaStore.MediaColumns.DATA)
            val columnDateModified: Int = cursor.getColumnIndex(MediaStore.MediaColumns.DATE_MODIFIED)
            val columnTitle: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.TITLE)
            val columnMediaType: Int = cursor.getColumnIndex(MediaStore.Files.FileColumns.MEDIA_TYPE)
            val columnSize: Int = cursor.getColumnIndex(MediaStore.Images.ImageColumns.SIZE)

            cursor.use { cursor ->
                while (cursor.moveToNext()) {
                    val id = if (columnId != -1) cursor.getInt(columnId) else -1
                    val path: String? = if (columnData != -1) cursor.getString(columnData) else ""
                    val folderPath = path?.substring(0, path.lastIndexOf("/"))
                    val title = if (columnTitle != -1) cursor.getString(columnTitle) else ""
                    val times = if (columnDateModified != -1) cursor.getLong(columnDateModified) else 0L
                    val parseTime = if (columnDateModified != -1) format.format(Date(times * 1000L)) else ""

                    val bucket: String? = if (columnBucket == -1 && path != null){
                        val splits: Array<String> = path.split("/".toRegex()).toTypedArray()
                        splits[splits.size - 2]
                    } else {
                        cursor.getString(columnBucket)
                    }

                    if (bucket == null ||
                        bucket.contains("%3A") ||
                        bucket.contains("%2F") ||
                        bucket.contains("%3F") ) continue // camera_nft 폴더 외 폴더 제외
                    if (path != null && path.endsWith("gif")) continue  // gif 제외
                    if (columnSize != -1 && cursor.getInt(columnSize) == 0) continue    // size 0 제외

                    var videoDuration: Long = -1
                    var videoWidth = 0
                    var videoHeight = 0
                    var videoRotate = 0
                    if (mode == GalleryMode.VIDEO) {
                        try {
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(path)
                            val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                            val width = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_WIDTH)
                            val height = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_HEIGHT)
                            val rotation = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_VIDEO_ROTATION)
                            val timeInMilliSec = time?.toLong() ?: 0L
                            val duration = (timeInMilliSec / 1000.toFloat()).roundToInt().toLong()
                            if (timeInMilliSec > 0) {
                                videoDuration = duration
                                videoWidth = width?.toInt() ?: 0
                                videoHeight = height?.toInt() ?: 0 //Integer.parseInt(height);
                                videoRotate = rotation?.toInt() ?: 0 //Integer.parseInt(rotation);
                            } else {
                                continue
                            }
                        } catch (e: RuntimeException) {
                            continue
                        }

                    }
                    val item = GalleryItem(id, path, folderPath, title, times, parseTime, mode, videoDuration, videoWidth, videoHeight, videoRotate, false)
                    fileList.add(item)
                    folderMap[folderPath]?.apply {
                        folderItems.add(item)
                    } ?: run {
                        folderPath?.let { folderMap[it] = GalleryFolderItem(bucket, mutableListOf(item)) }
                    }
                }
            }
        }

        val folderList = folderMap.values.sortedBy { it.folderName }.toMutableList()
//        return folderList
        return GalleryFolderItem("all photo", fileList)
    }
}