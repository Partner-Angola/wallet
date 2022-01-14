package com.joeware.android.gpulumera.reward.ui.wallet.transaction

import android.text.util.Linkify
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.databinding.ItemWalletTransactionBinding
import com.joeware.android.gpulumera.databinding.ItemWalletTransactionHeaderBinding
import com.joeware.android.gpulumera.reward.model.WalletHistory
import com.jpbrothers.base.util.log.JPLog
import java.text.SimpleDateFormat
import java.util.*
import java.util.regex.Pattern

class WalletTransactionAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var amount: String = "0"
    private var myPubKey: String? = null
    private var items: List<WalletHistory>? = null

    fun setItems(amount: String, items: List<WalletHistory>, pubKey: String?) {
        this.items = items
        this.amount = amount
        this.myPubKey = pubKey
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int = position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == 0) TransactionHistoryViewHeaderHolder(ItemWalletTransactionHeaderBinding.inflate(inflater, parent, false))
        else TransactionHistoryViewHolder(ItemWalletTransactionBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is TransactionHistoryViewHolder -> items?.get(position - 1)?.let { item -> holder.bind(item) }
            is TransactionHistoryViewHeaderHolder -> holder.bind()
        }
    }

    override fun getItemCount(): Int = items?.size?.plus(1) ?: 0

    inner class TransactionHistoryViewHeaderHolder(val binding: ItemWalletTransactionHeaderBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind() {
            binding.msgTokenAmount.text = amount
        }
    }

    inner class TransactionHistoryViewHolder(val binding: ItemWalletTransactionBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletHistory) {

            binding.tvHash.text = item.signatures
            Linkify.addLinks(binding.tvHash, Pattern.compile(item.signatures), "", null, { _, _ ->
                    "https://solscan.io/tx/${item.signatures}?cluster=devnet"
            })

            if (item.from == myPubKey) {
                binding.tvLabelReceive.text = "보내기"
                binding.icReceive.setBackgroundResource(R.drawable.point_ic_send)
            } else {
                binding.tvLabelReceive.text = "받기"
                binding.icReceive.setBackgroundResource(R.drawable.point_ic_receive)
            }



//            binding.tvProgress.text = SimpleDateFormat("MM월 dd일 HH:mm").format(Date(item.blockTime))
            binding.tvAmount.visibility = View.INVISIBLE
        }
    }


}