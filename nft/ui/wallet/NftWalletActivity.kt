package com.joeware.android.gpulumera.nft.ui.wallet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityNftWalletBinding
import com.joeware.android.gpulumera.databinding.ItemWalletInfoBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftWalletActivity : BaseActivity() {

    private lateinit var binding: ActivityNftWalletBinding
    private val viewModel: NftWalletViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nft_wallet)
        binding.lifecycleOwner = this
        binding.rvContent.adapter = WalletInfoAdapter()
    }

    override fun setObserveData() {

    }

    override fun init() {
        binding.btnClose.setOnClickListener { finish() }
    }


    /****************************************
     * 어뎁터
     ****************************************/
    inner class WalletInfoAdapter : RecyclerView.Adapter<WalletInfoAdapter.RewardHistoryViewHolder>() {

        private var items: List<String> = arrayListOf(
            getString(R.string.nft_title_wallet_tranaction),
            getString(R.string.nft_title_wallet_reset_lock),
            getString(R.string.nft_title_wallet_view_seed),
            getString(R.string.nft_title_wallet_logout)
        )

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RewardHistoryViewHolder {
            val inflater = LayoutInflater.from(parent.context)
            return RewardHistoryViewHolder(ItemWalletInfoBinding.inflate(inflater, parent, false))
        }

        override fun onBindViewHolder(holder: RewardHistoryViewHolder, position: Int) {
            items[position].let { item -> holder.bind(item) }
        }

        override fun getItemCount(): Int = items.size

        inner class RewardHistoryViewHolder(val binding: ItemWalletInfoBinding) : RecyclerView.ViewHolder(binding.root) {
            fun bind(item: String) {
                binding.tvTitle.text = item
                binding.tvTitle.setOnClickListener {
//                    onClick?.onWalletInfoClick(items.indexOf(item))
                }
            }
        }
    }
}