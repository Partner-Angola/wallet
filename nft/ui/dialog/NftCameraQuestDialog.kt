package com.joeware.android.gpulumera.nft.ui.dialog

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogNftCameraQuestBinding

class NftCameraQuestDialog() : BaseDialogFragment() {

    companion object {
        val TAG: String = NftCameraQuestDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?) {
            manager?.let {
                NftCameraQuestDialog().show(it, TAG)
            }
        }
    }
    private lateinit var binding: DialogNftCameraQuestBinding

    override fun onStart() {
        super.onStart()
        dialog?.window?.let { window ->
            window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            window.setWindowAnimations(R.style.MyAnimation_Window)
        }
    }


    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogNftCameraQuestBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
    }

    override fun init() {
        binding.btnClose.setOnClickListener { dismiss() }
    }
}