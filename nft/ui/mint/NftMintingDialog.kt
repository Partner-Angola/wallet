package com.joeware.android.gpulumera.nft.ui.mint

import android.view.*
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintSet
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.aghajari.emojiview.emoji.Emoji
import com.aghajari.emojiview.listener.OnEmojiActions
import com.aghajari.emojiview.listener.PopupListener
import com.aghajari.emojiview.view.AXEmojiView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.DialogNftMintingBinding
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.nft.ui.camera.NftCameraViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.jpbrothers.base.animation.base.JPAni
import com.jpbrothers.base.animation.base.Techniques
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent

class NftMintingDialog : BaseDialogFragment() {

    private val viewModel: NftMintingViewModel by viewModel()
    private val cameraViewModel: NftCameraViewModel by sharedViewModel()
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private lateinit var binding: DialogNftMintingBinding
    private var toast: Toast? = null
    private lateinit var itemList :ArrayList<GalleryItem>

    companion object {
        val TAG: String = NftMintingDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, itemList: ArrayList<GalleryItem>) {
            manager?.let { NftMintingDialog().apply {this.itemList = itemList }.show(it, TAG) }
        }
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftMintingBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.nftID.observe(this, Observer {
            binding.tvTitleId.text = it
        })
        viewModel.startMinting.observe(this, Observer {
            JPAni.with(Techniques.FadeIn).duration(1000).onStart {binding.lyProgress.visibility = View.VISIBLE}.playOn(binding.lyProgress)
            JPAni.with(Techniques.BounceInDown).duration(2000).playOn(binding.ivAni)
        })
        viewModel.completeMinting.observe(this, Observer {
            C.IS_REFRESH_MY_PHOTO = true
            NftMintingCompleteDialog.showDialog(parentFragmentManager, it)
            dismiss()
        })
        viewModel.visibleDelete.observe(this, Observer {
            binding.btnInputDelete.visibility = it
        })
    }

    override fun init() {
        viewModel.itemList = itemList
        initEmojiPopupLayout()
        Glide.with(requireContext())
            .load(viewModel.itemList[0].path)
            .apply(
                RequestOptions()
                    .skipMemoryCache(false)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.color.transparent)
            )
            .transform(RoundedCorners(30)).centerCrop()
            .transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail))
            .into(binding.ivNftPhoto)
        binding.btnClose.setOnClickListener { cameraViewModel.backToCamera();dismiss() }
        layoutChangedListener = ViewTreeObserver.OnGlobalLayoutListener {
            binding.lyScroll.scrollTo(0, 0)
            binding.lyScroll.viewTreeObserver.removeOnGlobalLayoutListener(layoutChangedListener)
            layoutChangedListener = null
        }
        binding.lyScroll.viewTreeObserver.addOnGlobalLayoutListener(layoutChangedListener)
    }

    private var layoutChangedListener: ViewTreeObserver.OnGlobalLayoutListener? = null


    override fun onResume() {
        super.onResume()
        dialog?.setOnKeyListener { _, keyCode, _ ->
            if ((keyCode == KeyEvent.KEYCODE_BACK)) binding.lyEmoji.onBackPressed()
            else false
        }
    }

    private fun initEmojiPopupLayout() {
//        binding.etEmoji.visibility = View.GONE
        val emojiView = AXEmojiView(requireContext())
        emojiView.editText = binding.etEmoji
        emojiView.onEmojiActionsListener = object: OnEmojiActions {
            override fun onClick(view: View?, emoji: Emoji?, fromRecent: Boolean, fromVariant: Boolean) {
                emoji?.let { viewModel.inputEmoji(emoji = it.toString()) }
            }
            override fun onLongClick(view: View?, emoji: Emoji?, fromRecent: Boolean, fromVariant: Boolean): Boolean = true
        }
        binding.lyEmoji.initPopupView(emojiView)
        binding.lyEmoji.isPopupAnimationEnabled = true
        binding.lyEmoji.popupAnimationDuration = 250
        val constraintSet = ConstraintSet().apply { clone(binding.root) }
        binding.lyEmoji.setPopupListener(object: PopupListener {
            override fun onDismiss() {}
            override fun onShow() {}
            override fun onKeyboardOpened(height: Int) {}
            override fun onKeyboardClosed() {}
            override fun onViewHeightChanged(height: Int) {
                binding.lyScroll.apply {
                    setPadding(0,0,0, height)
                    scrollTo(0, binding.tvEmoji.bottom)
                }
                if (height == 0) {
                    constraintSet.connect(binding.lyScroll.id, ConstraintSet.BOTTOM, binding.btnMakeNft.id, ConstraintSet.TOP)
                } else {
                    constraintSet.connect(binding.lyScroll.id, ConstraintSet.BOTTOM, binding.root.id, ConstraintSet.BOTTOM)
                }
                constraintSet.applyTo(binding.root)
            }
        })

        binding.tvEmoji.setOnClickListener {
            JPLog.i("호일", "Call Me~!")
            binding.lyEmoji.toggle()
        }
    }

}