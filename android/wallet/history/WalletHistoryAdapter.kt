package com.joeware.android.gpulumera.account.wallet.history

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.blankj.utilcode.util.StringUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.*
import com.joeware.android.gpulumera.databinding.ItemWalletHistoryBinding
import com.joeware.android.gpulumera.util.AccountUtil
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletHistoryAdapter<T> : RecyclerView.Adapter<WalletHistoryAdapter<T>.WalletHistoryViewHolder>() {

    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)
    private var items: List<T>? = null
    private var historyType: WalletHistoryType? = null

    fun setHistoryType(type: WalletHistoryType) {
        this.historyType = type
    }

    fun setItems(items: List<T>) {
        this.items = items
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletHistoryViewHolder {
        val inflate = LayoutInflater.from(parent.context)
        return WalletHistoryViewHolder(ItemWalletHistoryBinding.inflate(inflate, parent, false))
    }

    override fun onBindViewHolder(holder: WalletHistoryViewHolder, position: Int) {
        items?.get(position)?.let { item ->
            when(item) {
                is EthHistory -> holder.bindEth(item)
                is EthTokenHistory -> holder.bindEthToken(item)
                is SolHistory -> {
                    if (historyType == WalletHistoryType.SOL) holder.bindSol(item)
                    else if (historyType == WalletHistoryType.SOL_ANG) holder.bindSolToken(item)
                }
            }

        }
    }

    override fun getItemCount(): Int = items?.size ?: 0

    inner class WalletHistoryViewHolder(private val binding: ItemWalletHistoryBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bindEth(item: EthHistory) {
            if (item.from == accountUtil.getWalletEthereumAddress()) {
                binding.type = WalletActionType.SEND
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_send)
                binding.tvAddress.text = item.to
            } else if (item.to == accountUtil.getWalletEthereumAddress()) {
                binding.type = WalletActionType.RECEIVED
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_receive)
                binding.tvAddress.text = item.from
            }
            binding.tvAmount.text = "${item.value.toLong().div(100000000)} ETH2"
            binding.tvDate.text = item.timeStamp.toString()
        }

        fun bindEthToken(item: EthTokenHistory) {
            if (item.from == accountUtil.getWalletEthereumAddress()) {
                binding.type = WalletActionType.SEND
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_send)
                binding.tvAddress.text = item.to
            } else if (item.to == accountUtil.getWalletEthereumAddress()) {
                binding.type = WalletActionType.RECEIVED
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_receive)
                binding.tvAddress.text = item.from
            }
            binding.tvAmount.text = "${item.value} ANGL"
            binding.tvDate.text = item.timeStamp.toString()
        }

        fun bindSol(item: SolHistory) {
            if (item.from == accountUtil.getWalletSolanaAddress()) {
                binding.type = WalletActionType.SEND
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_send)
                binding.tvAddress.text = item.to
            } else if (item.to == accountUtil.getWalletSolanaAddress()) {
                binding.type = WalletActionType.RECEIVED
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_receive)
                binding.tvAddress.text = item.from
            }
            binding.tvAmount.text = "${item.value} SOL"
            binding.tvDate.text = item.timeStamp.toString()
        }

        fun bindSolToken(item: SolHistory) {
            if (item.from == accountUtil.getWalletSolanaAddress()) {
                binding.type = WalletActionType.SEND
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_send)
                binding.tvAddress.text = item.to
            } else if (item.to == accountUtil.getWalletSolanaAddress()) {
                binding.type = WalletActionType.RECEIVED
                binding.tvTitle.text = StringUtils.getString(R.string.wallet_history_receive)
                binding.tvAddress.text = item.from
            }
            binding.tvAmount.text = "${item.value} ANGL"
            binding.tvDate.text = item.timeStamp.toString()
        }
    }


}