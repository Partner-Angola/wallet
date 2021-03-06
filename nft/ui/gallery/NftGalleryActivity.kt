package com.joeware.android.gpulumera.nft.ui.gallery

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Rect
import android.view.View
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.base.BaseAlterDialog
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityNftGalleryBinding
import com.joeware.android.gpulumera.nft.ui.gallery.detail.NftGalleryDetailDialog
import com.joeware.android.gpulumera.nft.ui.mint.NftMintingDialog
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.util.isDenyPermission
import com.joeware.android.gpulumera.util.isPermissionGrantedAndRequestPermission
import com.joeware.android.gpulumera.util.movePermissionSetting
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftGalleryActivity : BaseActivity(), OnGalleryCallback {

    private lateinit var binding: ActivityNftGalleryBinding
    private val viewModel: NftGalleryViewModel by viewModel()

    private val requestCodePermission = 100

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nft_gallery)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.adapter = NftGalleryAdapter(this)
    }

    override fun setObserveData() {
        runDisposable(viewModel.galleryPictureItems) {
            binding.lySwipeRefresh.isRefreshing = false
            if (it.isNotEmpty()) {
                binding.adapter?.setCallBack(this)
                binding.adapter?.setItems(it)
                binding.tvEmptyText.visibility = View.GONE
            }  else {
                binding.tvEmptyText.visibility = View.VISIBLE
            }
        }

        viewModel.notifyList.observe(this, Observer {
            binding.adapter?.notifyDataSetChanged()
        })
        viewModel.updateGalleryPictureItem.observe(this, Observer {
            binding.adapter?.updateItem(it)
        })
        viewModel.clickMint.observe(this, Observer {
            NftMintingDialog.showDialog(supportFragmentManager, viewModel.getSelectedPhoto())
        })
    }

    override fun init() {
        checkPermissions()
        binding.btnClose.setOnClickListener { finish() }
        binding.lySwipeRefresh.setOnRefreshListener { initLoadDeviceMedia() }
    }

    private fun initLoadDeviceMedia() {
        setSpanSizeGalleryRecyclerview()
        viewModel.initLoadDeviceMedia(this)
    }

    /***********************************************************************************************
     * ?????? ?????? ?????? ?????? ??? ??????
     **********************************************************************************************/
    private fun checkPermissions() {
        if (isPermissionGrantedAndRequestPermission(
                this@NftGalleryActivity,
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                requestCodePermission
            )
        ) {
            initLoadDeviceMedia()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodePermission) {
            if (grantResults.isNotEmpty()) {
                var isAllGranted = true
                for (grant in grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false; break
                    }
                }

                if (isAllGranted) { // ????????? ????????? ?????? ????????????.
                    initLoadDeviceMedia()
                } else {  // ???????????? ?????? ????????? ??????. ????????????/???????????? ????????? ????????? ?????? ????????? ???????????? ???.
                    if (isDenyPermission(
                            this,
                            arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
                        )
                    ) {    // ?????? ?????? ?????? ??????????????? ?????? ?????? ?????????.
                        BaseAlterDialog.Builder()
                            .title(getString(R.string.permission_title))
                            .message(getString(R.string.permission_album_content))
                            .okButton { movePermissionSetting(this, requestCodePermission) }
                            .cancelButton { finish() }
                            .build()
                            .show(supportFragmentManager, BaseAlterDialog.TAG)
                    } else {    // ?????? ?????? ???????????????.
                        BaseAlterDialog.Builder()
                            .title(getString(R.string.permission_title))
                            .message(getString(R.string.permission_album_content))
                            .okButton { movePermissionSetting(this, requestCodePermission) }
                            .cancelButton { finish() }
                            .build()
                            .show(supportFragmentManager, BaseAlterDialog.TAG)
                    }
                }
            }
        }
    }

    /***********************************************************************************************
     * ????????????????????? Spacing
     **********************************************************************************************/
    private fun setSpanSizeGalleryRecyclerview() {
        val displayWidth = C.DIS_SIZE.x
        val spanSize = if (displayWidth / 360 > 3) displayWidth / 360 else 3
        binding.rvGallery.layoutManager = GridLayoutManager(this, spanSize)
        binding.rvGallery.addItemDecoration(GridSpacingItemDecoration(spanSize, 4, true))
    }

    class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            super.getItemOffsets(outRect, view, parent, state)
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing
                }
                outRect.bottom = spacing // item bottom
            } else {
                outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing // item top
                }
            }
        }
    }


    /***********************************************************************************************
     * RecyclerView Adapter CallBack
     **********************************************************************************************/
    override fun onSelectedItem(item: GalleryItem) {
        NftGalleryDetailDialog.showDialog(supportFragmentManager, item)
    }

    override fun onCheckBoxSelectedItem(idx: Int) {
        binding.adapter?.notifyItemChanged(idx)
    }

}