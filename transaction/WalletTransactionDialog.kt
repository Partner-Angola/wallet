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
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
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
        binding.vpMain.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                binding.lyTab.getTabAt(position)?.select()
            }
        })
        binding.lyTab.addOnTabSelectedListener(object: TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                binding.vpMain.currentItem = tab?.position ?: 0
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        return binding.root
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnBack.setOnClickListener { dismiss() }
    }

    inner class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int  = 2
        override fun createFragment(position: Int): Fragment {
            return when(position) {
                0 -> WalletTransactionFragment.newInstants(TokenType.ANGOLA, pubKey, angolaAmount)
                else -> WalletTransactionFragment.newInstants(TokenType.SOLANA, pubKey, solanaAmount)
            }
        }

    }

}
