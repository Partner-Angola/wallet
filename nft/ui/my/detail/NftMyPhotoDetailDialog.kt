package com.joeware.android.gpulumera.nft.ui.my.detail

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeActivity
import com.joeware.android.gpulumera.databinding.DialogNftMyPhotoDetailBinding
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent

class NftMyPhotoDetailDialog() : BaseDialogFragment() {

    companion object {
        val TAG: String = NftMyPhotoDetailDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, item: NftMyCollectionPhoto) {
            manager?.let {
                NftMyPhotoDetailDialog().apply { this.item = item }.show(it, TAG)
            }
        }
    }

    private val viewModel: NftMyPhotoDetailViewModel by viewModel()
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private lateinit var binding: DialogNftMyPhotoDetailBinding
    private lateinit var item: NftMyCollectionPhoto

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setWindowAnimations(R.style.MyAnimation_Window)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppCompatTheme_NoActionBarWhite);
    }

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftMyPhotoDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveData() {
        viewModel.clickJoin.observe(this, Observer {
            startActivity(Intent(requireActivity(), ChallengeActivity::class.java))
            requireActivity().overridePendingTransition(R.anim.slide_up_chall, R.anim.fade_out)
            dismiss()
        })
    }

    override fun init() {
        Glide.with(binding.ivMain.context).load(item.image).apply(
            RequestOptions()
                .skipMemoryCache(false)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .placeholder(R.color.transparent)).listener(
                    object : RequestListener<Drawable>{
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            binding.pbLoading.visibility = View.GONE
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            binding.pbLoading.visibility = View.GONE
                            return false
                        }
                    }
                )
            .timeout(10000).transition(DrawableTransitionOptions().transition(R.anim.fade_in_gallery_thumbnail)).into(binding.ivMain)
        binding.tvTitleImgId.text = item.name.replace("#", "")
        binding.tvEmoji.text = item.description
    }

}