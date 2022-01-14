package com.joeware.android.gpulumera.reward.ui.wallet.info

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.databinding.ItemWalletInfoBinding
import com.joeware.android.gpulumera.reward.util.RewardPointUtil
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletInfoAdapter : RecyclerView.Adapter<WalletInfoAdapter.RewardHistoryViewHolder>() {

    private var items: List<String> = arrayListOf("거래 내역 보기", "보안 인증 초기화", "복구 Seed 보기", "로그아웃")

    private var onClick: WalletInfoActivity.WalletInfoClickListener? = null

    fun setOnClickListener(onClick: WalletInfoActivity.WalletInfoClickListener) = run {
        this.onClick = onClick;
    }

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
                onClick?.onWalletInfoClick(items.indexOf(item))
            }
        }
    }
}