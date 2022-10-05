package com.joeware.android.gpulumera.account.wallet.history

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletHistoryType
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletHistoryBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletHistoryActivity : BaseActivity() {

    companion object {
        fun startActivityIntent(context: Context, tokenType: WalletTokenType) : Intent {
            return Intent(context, WalletHistoryActivity::class.java).apply {
                putExtra("tokenType", tokenType.name)
            }
        }
    }

    private lateinit var binding: ActivityWalletHistoryBinding
    private val viewModel: WalletHistoryViewModel by viewModel()
    private var tokenType: WalletTokenType? = null

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_history)
        binding.lifecycleOwner = this
        tokenType = when(intent.getStringExtra("tokenType")) {
            WalletTokenType.ETH.name -> WalletTokenType.ETH
            WalletTokenType.SOL.name -> WalletTokenType.SOL
            else -> null
        }
    }

    override fun setObserveData() {

    }

    override fun init() {
        tokenType?.let {
            val pagerAdapter = when(it) {
                WalletTokenType.ETH -> {
                    binding.tvTopTitle.text = getString(R.string.eth_wallet)
                    binding.lyTab.addTab(binding.lyTab.newTab().setText(getString(R.string.angola_token)))
                    binding.lyTab.addTab(binding.lyTab.newTab().setText(getString(R.string.eth_token)))
                    WalletHistoryPagerAdapter(supportFragmentManager, lifecycle, listOf(
                        Pair(getString(R.string.angola_token), WalletHistoryFragment().apply { arguments = Bundle().apply { putString("type", WalletHistoryType.ETH_ANG.name) } }),
                        Pair(getString(R.string.eth_token), WalletHistoryFragment().apply { arguments = Bundle().apply { putString("type", WalletHistoryType.ETH.name) } })
                    ))
                }
                WalletTokenType.SOL -> {
                    binding.tvTopTitle.text = getString(R.string.sol_wallet)
                    binding.lyTab.addTab(binding.lyTab.newTab().setText(getString(R.string.angola_token)))
                    binding.lyTab.addTab(binding.lyTab.newTab().setText(getString(R.string.sol_token)))
                    WalletHistoryPagerAdapter(supportFragmentManager, lifecycle, listOf(
                        Pair(getString(R.string.angola_token), WalletHistoryFragment().apply { arguments = Bundle().apply { putString("type", WalletHistoryType.SOL_ANG.name) } }),
                        Pair(getString(R.string.sol_token), WalletHistoryFragment().apply { arguments = Bundle().apply { putString("type", WalletHistoryType.SOL.name) } })
                    ))
                }
            }
            binding.vpMain.adapter = pagerAdapter
            TabLayoutMediator(binding.lyTab, binding.vpMain) { tab, position ->
                tab.text = pagerAdapter.getTitle(position)
            }.attach()
        } ?: run { finish() }
        binding.btnBack.setOnClickListener { finish() }
    }

    inner class WalletHistoryPagerAdapter(fm: FragmentManager, lifecycle: Lifecycle, private val fragmentList: List<Pair<String, Fragment>>) :
        FragmentStateAdapter(fm, lifecycle) {
        fun getTitle(position: Int): String = fragmentList[position].first
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment = fragmentList[position].second
    }
}
