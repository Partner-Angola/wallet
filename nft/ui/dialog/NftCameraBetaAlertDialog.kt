package com.joeware.android.gpulumera.nft.ui.dialog

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogNftCameraBetaAlertBinding
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.java.standalone.KoinJavaComponent

class NftCameraBetaAlertDialog() : BaseDialogFragment() {
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)
    companion object {
        val TAG: String = NftCameraBetaAlertDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) {
            manager?.let {
                NftCameraBetaAlertDialog().show(it, TAG)
            }
        }
    }

    private lateinit var binding: DialogNftCameraBetaAlertBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setWindowAnimations(R.style.MyAnimation_Window)
        }
    }


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftCameraBetaAlertBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
    }

    override fun init() {
        binding.btnNotShow.setOnClickListener { prefUtil.isShowBetaAlert = false; dismiss() }
        binding.btnClose2.setOnClickListener { dismiss() }
        binding.btnClose.setOnClickListener { dismiss() }
        var str = getString(R.string.nft_msg_beta_alert)
        binding.tvDesc.text =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Html.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY)
            } else {
                Html.fromHtml(str)
            }
        binding.btnAng.setOnClickListener { requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://agla.io"))) }
        binding.btnDiscord.setOnClickListener { requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://discord.gg/4R8KCXqH"))) }
        binding.btnTwitter.setOnClickListener { requireActivity().startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/ANGOLApartner"))) }
    }
}