package com.joeware.android.gpulumera.reward.ui.wallet.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.joeware.android.gpulumera.base.BaseDialogFragment
import com.joeware.android.gpulumera.databinding.DialogWalletTransactionBinding
import com.joeware.android.gpulumera.reward.model.TokenType
import com.joeware.android.gpulumera.reward.model.WalletHistory
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletTransactionDialog : BaseDialogFragment() {

    companion object {
        val TAG: String = WalletTransactionDialog::class.java.simpleName
        fun showDialog(manager: FragmentManager?, pubKey: String, angolaAmount : String, solanaAmount: String) = manager?.let {
            val bundle = Bundle().apply {
                putString("pubKey", pubKey)
                putString("angolaAmount", angolaAmount)
                putString("solanaAmount", solanaAmount)
            }
            WalletTransactionDialog().apply { arguments = bundle }.show(it, TAG)
        }
    }

    private lateinit var binding: DialogWalletTransactionBinding
    private val viewModel: WalletTransactionViewModel by viewModel()

    private var pubKey: String? = null
    private var angolaAmount = "0"
    private var solanaAmount = "0"

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DialogWalletTransactionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        arguments?.let {
            pubKey = it.getString("pubKey")
            angolaAmount = it.getString("angolaAmount") ?: "0"
            solanaAmount = it.getString("solanaAmount") ?: "0"
        }
        activity?.let { binding.vpMain.adapter = ViewPagerAdapter(it) }
        return binding.root
    }

    override fun setObserveData() {
//        viewModel.getHistory.observe(this, Observer {
//            var listHistory = it as MutableList<WalletHistory>
//            binding.adapter?.setItems(this.angolaAmount, listHistory)
//            if(listHistory.size == 0) binding.labelEmptyAmount.visibility = View.VISIBLE
//        })
//        viewModel.clickRefresh.observe(this, Observer {
//            pubKey?.let {
//                binding.adapter?.clear()
//                viewModel.getHistory(it)
//            }
//        })
//        viewModel.visibilityProgress.observe(this, Observer {
//            binding.pbBeauty.visibility = it
//        })
//        viewModel.clickBack.observe(this, Observer {
//            dismiss()
//        })
    }

    override fun init() {
        binding.btnBack.setOnClickListener { dismiss() }
//        arguments?.getString("pubKey")?.let { pubKey ->
//            this.pubKey = pubKey
//            viewModel.getHistory(pubKey)
//        }
//
//        arguments?.getString("angolaAmount")?.let { angolaAmount ->
//            viewModel.setAngolaAmount(angolaAmount)
//            this.angolaAmount = angolaAmount
//        }
//
//        arguments?.getString("solanaAmount")?.let { solanaAmount ->
//            viewModel.setAngolaAmount(solanaAmount)
////            binding.msgTokenAmount.text = "$angolaAmount AGLA"
//        }
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int  = 2
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                1 -> WalletTransactionFragment.newInstants(TokenType.ANGOLA, pubKey, angolaAmount)
                else -> WalletTransactionFragment.newInstants(TokenType.SOLANA, pubKey, solanaAmount)
            }
        }

    }

}
