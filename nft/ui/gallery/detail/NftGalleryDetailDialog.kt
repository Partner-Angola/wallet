package com.joeware.android.gpulumera.nft.ui.gallery.detail

import android.graphics.Color
import android.graphics.PorterDuff
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.ViewCompat
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogNftGalleryDetailBinding
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.nft.model.NFTCameraSkinVO
import com.joeware.android.gpulumera.nft.ui.mint.NftMintingDialog
import com.jpbrothers.base.util.Util
import org.json.JSONArray
import org.json.JSONObject
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class NftGalleryDetailDialog() : BaseDialogFragment() {

    companion object {
        val TAG: String = NftGalleryDetailDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, item: GalleryItem) {
            manager?.let {
                NftGalleryDetailDialog().apply { this.item = item }.show(it, TAG)
            }
        }
    }
    private val viewModel: NftGalleryDetailViewModel by viewModel()

    private lateinit var binding: DialogNftGalleryDetailBinding
    private lateinit var item: GalleryItem

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftGalleryDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.clickChoice.observe(this, Observer {
            dismiss()
            NftMintingDialog.showDialog(parentFragmentManager, arrayListOf(item))
//                galleryViewModel.selectPhoto(item.id, !item.isSelect!!)
//
            }
        )
    }

    override fun init() {
        if(item.isSelect == false) {
            binding.btnSelect.setBackgroundResource(R.drawable.back_rounded_corners_main_color_15dp)
            binding.btnSelect.setTextColor(resources.getColor(R.color.white))
            binding.btnSelect.text = getString(R.string.nft_select)
        }else{
            binding.btnSelect.setBackgroundResource(R.drawable.back_rounded_corners_with_stroke_grey)
            binding.btnSelect.setTextColor(resources.getColor(R.color.sub_color))
            binding.btnSelect.text = getString(R.string.nft_unselect)
        }
        binding.btnSelect.text =getString(R.string.nft_make)
//        binding.tvTitleId.text = Random().nextInt(999999999).toString()
        binding.tvTitleId.text = "00000001"
        binding.tvTitleDate.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Date(File(item.path).lastModified()))
        binding.btnClose.setOnClickListener {dismiss() }
        binding.ivMain.setOnClickListener { changeCameraSkin() }
    }

    override fun onResume() {
        super.onResume()
        loadNFTCameraSkinDateFromJson()
        changeCameraSkin()
        context?.let { Glide.with(it).load(item.path).centerCrop().into(binding.ivMain) }
    }

    /**
     * 추후 서버 데이터 파싱으로 변경 시작.
     * 스킨컬러 변경에 쓰이는 ImageView List
     */
    private val skinImgViewList: MutableList<ImageView> = mutableListOf()

    /***************************************************************************************************************
     * 카메라의 스킨 컬러를 랜덤을로 변경 한다.
     ***************************************************************************************************************/
    private fun randomSkin() {
        val rnd = Random()
        // 최소 1~3개 값이 255 이상을 유지.
        var array: JSONArray? = null
        try {
            val jsonObj = JSONObject(Util.loadJSONFromAsset(context, "marterial_colors.json"))
            array = jsonObj.getJSONArray("colors")

            loop@ for (i in 0 until skinImgViewList.count()){
                val backColor: Int = Color.parseColor(array[rnd.nextInt(array.length())].toString())
                when (i) {
                    skinImgViewList.size-2 -> {
                        // 마지막 바디
                        skinImgViewList[i].setColorFilter(backColor, PorterDuff.Mode.SRC_IN)
                    }
                    skinImgViewList.size -1-> {
                        // 제일 마지막 라인 바디일경우 컬러 필터 X
                        break@loop
                    }
                    else -> {
                        skinImgViewList[i].setColorFilter(backColor, PorterDuff.Mode.SRC_IN)
                    }
                }
                binding.tvTitleSrc.setBackgroundColor(backColor)
                binding.tvTitleId.setTextColor(backColor)
            }
        } catch (e: java.lang.Exception) {
        }
    }


    /***************************************************************************************************************
     * 카메라 스킨 변경 관련 시작.
     ***************************************************************************************************************/
    var cameraSkinIdx = 0;
    private lateinit var cameraNFTCameraSkin: NFTCameraSkinVO
    private var nftCameraSkinInfoList = arrayListOf<NFTCameraSkinVO>()

    private fun loadNFTCameraSkinDateFromJson() {
        // 카메라 스킨 더미 데이터 불러오기
        try {
            val jsonObj = JSONObject(Util.loadJSONFromAsset(context, "camera_skins.json"))
            val jsonArray = jsonObj.getJSONArray("skins");
            var index = 0
            nftCameraSkinInfoList.clear()
            while (index < jsonArray.length()) {
                val nftCameraSkinVO: NFTCameraSkinVO = Gson().fromJson(jsonArray[index].toString(), NFTCameraSkinVO::class.java)
                nftCameraSkinInfoList.add(nftCameraSkinVO)
                index++
            }

        } catch (e: java.lang.Exception) {

        }
    }

    private fun changeCameraSkin() {
        binding.lyCameraThumb.removeAllViews()
        cameraSkinIdx = 0
        cameraNFTCameraSkin = nftCameraSkinInfoList[cameraSkinIdx];
        setNFTCameraSkin("${cameraNFTCameraSkin.name}${cameraNFTCameraSkin.resName}", cameraNFTCameraSkin.skinRatio, cameraNFTCameraSkin.percentWidth)
//        randomSkin()
    }

    private fun setNFTCameraSkin(skinResName: String, ratio: String, percentWidth: Float) {
        skinImgViewList.clear()
        context?.let {
            for (i in 0 until cameraNFTCameraSkin.resLength) {
                generateSkin((it.resources.getIdentifier("$skinResName${i + 1}", "drawable", it.packageName)), ratio, 1f)
            }
            generateSkin((it.resources.getIdentifier("${skinResName}line", "drawable", it.packageName)), ratio, 1f)
        }
    }

    private fun generateSkin(resId: Int, ratio: String, percentWidth: Float) {
        val img = ImageView(context)
        img.setImageResource(resId)
        img.layoutParams = ViewGroup.LayoutParams(0, 0)
        img.id = ViewCompat.generateViewId()
        binding.lyCameraThumb.addView(img)

        val set = ConstraintSet()
        set.clone(binding.lyCameraThumb)
        set.setDimensionRatio(img.id, ratio)
        set.constrainDefaultWidth(img.id, ConstraintSet.MATCH_CONSTRAINT_PERCENT)
        set.constrainPercentWidth(img.id, percentWidth)
        set.connect(img.id, ConstraintSet.BOTTOM, binding.lyCameraThumb.id, ConstraintSet.BOTTOM)
        set.connect(img.id, ConstraintSet.TOP, binding.lyCameraThumb.id, ConstraintSet.TOP)
        set.connect(img.id, ConstraintSet.LEFT, binding.lyCameraThumb.id, ConstraintSet.LEFT)
        set.connect(img.id, ConstraintSet.RIGHT, binding.lyCameraThumb.id, ConstraintSet.RIGHT)
        set.applyTo(binding.lyCameraThumb)
        skinImgViewList.add(img)
    }


}