package com.joeware.android.gpulumera.challenge.ui.challenge

import android.Manifest
import android.content.Intent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import com.blankj.utilcode.util.PermissionUtils
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.GalleryAdapter
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.gallery.Folder
import com.joeware.android.gpulumera.challenge.widget.gallery.Gallery
import com.joeware.android.gpulumera.challenge.widget.gallery.ImageFileLoader
import com.joeware.android.gpulumera.challenge.widget.gallery.ImageLoaderListener
import com.joeware.android.gpulumera.databinding.ActivityPhotoSelectBinding
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.joeware.android.gpulumera.nft.ui.gallery.NftGalleryActivity
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.util.*

class PhotoSelectActivity : BaseActivity() {

    private lateinit var binding: ActivityPhotoSelectBinding
    private lateinit var imageLoader: ImageFileLoader
    private val viewModel: PhotoSelectViewModel by viewModel()
    private var mode = "gallery"

    private var folderList = arrayListOf<Folder>()

    private val vDecoration = VerticalSpaceItemDecoration(1)
    private val hDecoration = HorizontalSpaceItemDecoration(1)

    override fun setBinding() {
        mode = intent.getStringExtra("mode") ?: "gallery"
        binding = DataBindingUtil.setContentView(this, R.layout.activity_photo_select)
        binding.apply {
            activity = this@PhotoSelectActivity
            lifecycleOwner = this@PhotoSelectActivity

            if (mode == "nft") {
                tvTitleNft.visibility = View.VISIBLE
                spFolder.visibility = View.GONE
                binding.btnNftCreate.visibility = View.VISIBLE
            } else {
                binding.btnNftCreate.visibility = View.GONE
                lySwipeRefresh.isRefreshing = false
                tvTitleNft.visibility = View.GONE
                spFolder.visibility = View.VISIBLE
                spFolder.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(
                        parent: AdapterView<*>?,
                        view: View?,
                        position: Int,
                        id: Long
                    ) {
                        val folder = folderList[position]
                        binding.lySwipeRefresh.isRefreshing = false
                        adapter?.setItems(folder.getImages())
                    }

                    override fun onNothingSelected(parent: AdapterView<*>?) {

                    }
                }
            }



            adapter = GalleryAdapter().apply {
                setOnClickListener(object : GalleryAdapter.OnClickListener {
                    override fun onClickChallenge(gallery: Gallery) {
                        val intent = Intent()
                        intent.putExtra("filePath", gallery.path)
                        setResult(RESULT_OK, intent)
                        finish()
                    }
                })
            }

            rcvList.apply {
                removeItemDecoration(vDecoration)
                addItemDecoration(vDecoration)
                removeItemDecoration(hDecoration)
                addItemDecoration(hDecoration)
            }
        }
    }

    override fun setObserveData() {
        viewModel.items.observe(this, androidx.lifecycle.Observer {
            binding.lySwipeRefresh.isRefreshing = false
            binding.adapter?.setItems(it)
        })
    }

    override fun init() {
        if (mode == "nft") {
            viewModel.getMyNft()
            binding.lySwipeRefresh.isRefreshing = true
            binding.lySwipeRefresh.setOnRefreshListener { viewModel.getMyNft()}
            binding.btnNftCreate.setOnClickListener { startActivity(Intent(this, NftGalleryActivity::class.java)) }
        } else {
            initGallery()
            checkPermissions()
            binding.lySwipeRefresh.setOnRefreshListener {
                binding.lySwipeRefresh.isRefreshing = true
            }
        }
    }

    private fun initGallery() {
        imageLoader = ImageFileLoader(this)
        imageLoader.abortLoadImages()
    }

    private fun checkPermissions() {
        if (PermissionUtils.isGranted(Manifest.permission.READ_EXTERNAL_STORAGE)) {
            loadGallery()
        } else {
            PermissionUtils.permission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .callback(object : PermissionUtils.FullCallback {
                    override fun onGranted(granted: MutableList<String>) {
                        loadGallery()
                    }

                    override fun onDenied(
                        deniedForever: MutableList<String>,
                        denied: MutableList<String>
                    ) {
                        ToastUtils.showShort(R.string.permission_gallery_allow)
                    }
                }).request()
        }
    }

    private fun loadGallery() {
        imageLoader.loadDeviceImages(
            isFolderMode = true,
            includeVideo = false,
            includeAnimation = false,
            excludedImages = arrayListOf(),
            listener = object : ImageLoaderListener {
                override fun onFailed(throwable: Throwable?) {
                }

                override fun onImageLoaded(
                    images: List<Gallery>?, folders: List<Folder>?
                ) {
                    folderList.clear()

                    // 전체 폴더 추가
                    val allFolder = Folder(getString(R.string.all_photo))
                    if (images != null) {
                        allFolder.setImages(images)
                    }
                    folderList.add(allFolder)

                    // 매 폴더별 이미지 추가
                    if (folders != null) {
                        folderList.addAll(folders)
                    }

                    this@PhotoSelectActivity.runOnUiThread {
//                        binding.adapter?.setItems(folderList[0].getImages())

                        val folderAdapter = ArrayAdapter(
                            this@PhotoSelectActivity,
                            R.layout.item_spinner_text,
                            folderList.map { it.folderName })
                        folderAdapter.setDropDownViewResource(R.layout.item_spinner_dropdown_text)
                        binding.spFolder.adapter = folderAdapter

                        binding.spFolder.setSelection(0)
                    }
                }
            })
    }
}