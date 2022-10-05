package com.joeware.android.gpulumera.challenge.dialog

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.FragmentManager
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogReportBinding

interface ReportDialogInterface {
    fun onSelect(text: String)
}


class ReportDialog : BaseDialogFragment() {

    companion object {
        @JvmStatic
        val TAG: String = ReportDialog::class.java.simpleName

        fun showDialog(
            manager: FragmentManager?,
            listener: ReportDialogInterface? = null
        ) = manager?.let {
            ReportDialog()
                .apply {
                    this.listener = listener
                }
                .show(it, TAG)
        }
    }

    private lateinit var binding: DialogReportBinding
    private var listener: ReportDialogInterface? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogReportBinding.inflate(inflater, container, false)
        binding.apply {
            lifecycleOwner = this@ReportDialog
            view = this@ReportDialog
        }
        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
    }

    fun onItem(v: View) {
        listener?.onSelect((v as AppCompatTextView).text.toString())
        dismiss()
    }
}