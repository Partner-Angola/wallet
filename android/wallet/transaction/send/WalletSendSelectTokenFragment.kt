package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletToken
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendSelectTokenBinding
import com.joeware.android.gpulumera.databinding.ItemWalletSendSelectTokenBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendSelectTokenFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendSelectTokenBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendSelectTokenBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.adapter = WalletTokenAdapter()
        return binding.root
    }

    override fun setObserveData() {
        parentViewModel.selectTokenItems.observe(this) { item ->
            binding.isLoading = false
            binding.adapter?.setItems(item)
        }
    }

    override fun init() {
        when(parentViewModel.getTokenType()) {
            WalletTokenType.ETH -> {
                binding.tvTopTitle.text = getString(R.string.eth_wallet)
            }
            WalletTokenType.SOL -> {
                binding.tvTopTitle.text = getString(R.string.sol_wallet)
            }
        }
        binding.isLoading = true
        parentViewModel.initTokenList()
        binding.btnBack.setOnClickListener { parentViewModel.prevPage() }
        binding.btnNext.setOnClickListener {
            binding.adapter?.getSelectToken()?.let { token ->
                parentViewModel.setSelectToken(token)
            }
        }
    }

    /****************************************
     * 어뎁터
     ****************************************/
    class WalletTokenAdapter: RecyclerView.Adapter<WalletTokenAdapter.WalletTokenViewHolder>() {
        private var currentPosition = 0
        private var items: List<WalletToken>? = null

        fun setItems(items: List<WalletToken>) {
            if (items.isNotEmpty()) {
                this.items = items
                notifyDataSetChanged()
            }
        }

        fun getSelectToken() : WalletToken? {
            return items?.get(currentPosition)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletTokenViewHolder {
            val inflate = LayoutInflater.from(parent.context)
            return WalletTokenViewHolder(ItemWalletSendSelectTokenBinding.inflate(inflate, parent, false))
        }

        override fun onBindViewHolder(holder: WalletTokenViewHolder, position: Int) {
            items?.get(position)?.let { item -> holder.bind(item, position) }
        }

        override fun getItemCount(): Int = items?.size ?: 0

        inner class WalletTokenViewHolder(private val binding: ItemWalletSendSelectTokenBinding): RecyclerView.ViewHolder(binding.root) {
            fun bind(item: WalletToken, position: Int) {
                binding.tvSymbolName.text = item.name
                binding.tvSymbol.text = item.symbol
                binding.tvAmount.text = item.amountStr
                Glide.with(binding.root.context).load(item.iconRes).fitCenter().into(binding.ivSymbolIcon)
                item.won?.let { won -> binding.tvWon.text = won } ?: run { binding.tvWon.visibility = View.INVISIBLE}
                Glide.with(binding.root.context).load(if (currentPosition == position) R.drawable.background_wallet_send_select else R.drawable.background_wallet_send_not_select).fitCenter().into(binding.viBackground)
                binding.root.setOnClickListener {
                    currentPosition = position
                    notifyDataSetChanged()
                }
            }
        }
    }
}