package com.joeware.android.gpulumera.nft.ui.mint

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeActivity
import com.joeware.android.gpulumera.databinding.DialogNftMintingComplteBinding
import com.joeware.android.gpulumera.nft.model.NftMint
import com.joeware.android.gpulumera.nft.ui.camera.NftCameraViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent
import java.text.SimpleDateFormat


class NftMintingCompleteDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = NftMintingCompleteDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, nftData: NftMint) {
            manager?.let { NftMintingCompleteDialog().apply { this.nftData = nftData }.show(it, TAG) }
        }
    }

    private val viewModel: NftMintingViewModel by viewModel()
    private val cameraViewModel: NftCameraViewModel by sharedViewModel()
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private lateinit var binding: DialogNftMintingComplteBinding
    private var toast: Toast? = null
    private lateinit var nftData: NftMint

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftMintingComplteBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.adapter = NftMintingCompleteAdapter().apply {
            setMoveChallenge {
                startActivity(Intent(requireActivity(), ChallengeActivity::class.java))
                requireActivity().overridePendingTransition(R.anim.slide_up_chall, R.anim.fade_out)
                finishMinting()
            }
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.challengeList.observe(this, androidx.lifecycle.Observer {
            binding.adapter?.setItems(it)
        })
    }

    override fun init() {
        Glide.with(requireContext())
            .load(nftData.metadata.image)
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
        binding.tvMinted.text = getString(R.string.nft_mint_complete)
        binding.msgAddress.text = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml("<u>${nftData.transactionHash}</u>", HtmlCompat.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml("<u>${nftData.transactionHash}</u>")
        }
        binding.msgAddress.setOnClickListener {
            activity?.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://sidescan.luniverse.io/chains/1634284028186342722/transactions/${nftData.transactionHash}")))
        }
        binding.msgAddress.visibility = View.GONE
        viewModel.inputEmoji(nftData.metadata.description)
        binding.msgId.text = nftData.metadata.name
        binding.msgTime.text = SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse(nftData.metadata.createdAt))
        viewModel.getChallengeList()
    }

    private fun finishMinting() {
        dismiss()
    }
}