package com.joeware.android.gpulumera.nft.ui.my

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.FragmentNftMyCollectionBinding
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.joeware.android.gpulumera.nft.ui.my.NftMyCollectionAdapter.NftMyCollectionType
import com.joeware.android.gpulumera.nft.ui.my.detail.NftMyPhotoDetailDialog
import com.jpbrothers.base.display.DisplayHelper
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftMyCollectionFragment : BaseFragment(), OnCollectionCallback {

    private lateinit var binding: FragmentNftMyCollectionBinding
    private val viewModel: NftMyCollectionViewModel by viewModel()
    private val mainViewModel: NftMyViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftMyCollectionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.adapter = NftMyCollectionAdapter().apply { setCallBack(this@NftMyCollectionFragment) }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.collectionType.observe(this, Observer { type -> setSpanCount(type) })
        viewModel.currentItems.observe(this, Observer { item ->
            if (item.isNotEmpty()) {
                binding.tvEmpty.visibility = View.GONE
                binding.adapter?.setItems(item)
            } else {
                binding.tvEmpty.visibility = View.VISIBLE
            }
            binding.lySwipeRefresh.isRefreshing = false
        })
        mainViewModel.callResume.observe(this, Observer {
            if (C.IS_REFRESH_MY_PHOTO) {
                C.IS_REFRESH_MY_PHOTO = false
                binding.lySwipeRefresh.isRefreshing = true
                viewModel.getMyNft()
            }
        })
    }

    override fun init() {
        binding.lySwipeRefresh.isRefreshing = true
        binding.lySwipeRefresh.setOnRefreshListener { viewModel.getMyNft()}
    }


    /**************************************************************************
     * GridLayout Settings
     **************************************************************************/
    private val defaultSpanSize = 2

    private fun setSpanCount(type: NftMyCollectionType) {
        binding.rvCollection.layoutManager = (binding.rvCollection.layoutManager as GridLayoutManager).apply {
            spanCount = defaultSpanSize
            spanSizeLookup = when(type) {
//                NftMyCollectionType.CAMERA, NftMyCollectionType.PHOTO -> object : GridLayoutManager.SpanSizeLookup() {
                NftMyCollectionType.CAMERA -> object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = if (position == 0) defaultSpanSize else 1
                }
                else -> object : GridLayoutManager.SpanSizeLookup() {
                    override fun getSpanSize(position: Int): Int = 1
                }
            }
        }

        DisplayHelper.getInstance(requireContext()).let { helper ->
            if (binding.rvCollection.itemDecorationCount > 0) binding.rvCollection.removeItemDecorationAt(0)
            binding.rvCollection.addItemDecoration(
                Spacing(
                    helper.convertDPtoPX(20f).toInt(),
                    helper.convertDPtoPX(20f).toInt(),
                    helper.convertDPtoPX(20f).toInt(),
                    type
                )
            )
        }

        when (type) {
            NftMyCollectionType.CAMERA -> {
                binding.lySwipeRefresh.visibility = View.GONE
                binding.ivSoonBack.visibility = View.VISIBLE
                binding.ivSoonTxt.visibility = View.VISIBLE
                binding.ivSoonBack.setImageResource(R.drawable.nft_img_comingsoon_camera)
            }
            NftMyCollectionType.CAMERA_BOX -> {
                binding.lySwipeRefresh.visibility = View.GONE
                binding.ivSoonBack.visibility = View.VISIBLE
                binding.ivSoonTxt.visibility = View.VISIBLE
                binding.ivSoonBack.setImageResource(R.drawable.nft_img_comingsoon_box)
            }
            else -> {
                binding.lySwipeRefresh.visibility = View.VISIBLE
                binding.ivSoonBack.visibility = View.GONE
                binding.ivSoonTxt.visibility = View.GONE
            }
        }

    }

    inner class Spacing(private val outPadding: Int, private val spacing: Int, private val topSpacing: Int, private val type: NftMyCollectionType) : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            super.getItemOffsets(outRect, view, parent, state)
            val index = (view.layoutParams as GridLayoutManager.LayoutParams).spanIndex
            val position = parent.getChildLayoutPosition(view)

            when(type) {
//                NftMyCollectionType.CAMERA, NftMyCollectionType.PHOTO -> {
                NftMyCollectionType.CAMERA -> {
                    if (position != 0) {
                        if (index == 0) {
                            outRect.left = outPadding
                            outRect.right = spacing / 2
                        }
                        else {
                            outRect.left = spacing / 2
                            outRect.right = outPadding
                        }
                        outRect.top = topSpacing
                    }
                } else -> {
                    if (index == 0) {
                        outRect.left = outPadding
                        outRect.right = spacing / 2
                    }
                    else {
                        outRect.left = spacing / 2
                        outRect.right = outPadding
                    }
                    outRect.top = if (position < defaultSpanSize) 0 else topSpacing
                }
            }
        }
    }

    override fun onSelectedItem(item: NftMyCollectionPhoto) {
        NftMyPhotoDetailDialog.showDialog(parentFragmentManager, item)
    }

}