package com.joeware.android.gpulumera.challenge.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.challenge.adapter.bindImage
import com.joeware.android.gpulumera.challenge.adapter.boolean2Visibility
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.databinding.DialogPhotoDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

interface PhotoDetailDialogInterface {
    fun onChallenge(challenge: Challenge)
}

class PhotoDetailDialog : BaseDialogFragment() {

    companion object {
        @JvmStatic
        val TAG: String = PhotoDetailDialog::class.java.simpleName

        fun showDialog(
            manager: FragmentManager?,
            join: Join? = null,
            showMedal: Boolean = true,
            showInfo: Boolean = true,
            listener: PhotoDetailDialogInterface? = null
        ) = manager?.let {
            PhotoDetailDialog().apply {
                this.showMedal = showMedal
                this.showInfo = showInfo
                this.join = join
                this.listener = listener
            }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogPhotoDetailBinding
    private val viewModel: ConfirmViewModel by viewModel()

    private var listener: PhotoDetailDialogInterface? = null
    private var showMedal = true
    private var showInfo = true
    private var join: Join? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogPhotoDetailBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@PhotoDetailDialog
            view = this@PhotoDetailDialog
            vm = viewModel

            join?.let {
                bindImage(ivContent, it.image, 12f)
                ivMedal.visibility = boolean2Visibility(showMedal && it.rank == 1)
                tvTitle.text = it.challengeTitleCountry ?: it.challengeTitle
                tvInfo.visibility = boolean2Visibility(showInfo && it.rank > 0)
                tvInfo.text = String.format(
                    getString(R.string.photo_info),
                    it.rank,
                    NumberFormat.getInstance().format(it.prize)
                )
            }
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.showToastMessage.observe(this, Observer {
            ToastUtils.showShort(it)
        })
        viewModel.challenge.observe(this, Observer {
            dismiss()
            listener?.onChallenge(it)
        })
    }

    override fun init() {
    }

    fun onTitle() {
        if (listener != null) {
            join?.let {
                viewModel.getChallengeInfo(it.challengeId)
            }
        } else {
            dismiss()
        }
    }
}