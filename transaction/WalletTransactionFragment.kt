package com.joeware.android.gpulumera.reward.ui.wallet.transaction

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletTransactionBinding
import com.joeware.android.gpulumera.reward.model.TokenType
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletTransactionFragment : CandyFragment() {

    companion object {
        fun newInstants(type: TokenType, pubKey: String?, amount : String) = WalletTransactionFragment().apply {
            arguments = Bundle().apply {
                putString("tokenType", type.name)
                putString("pubKey", pubKey)
                putString("amount", amount)
            }
        }
    }

    private lateinit var binding: FragmentWalletTransactionBinding
    private val viewModel: WalletTransactionViewModel by viewModel()
    private var _myPubKey: String? = null

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletTransactionBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        binding.adapter =  WalletTransactionAdapter()
        return binding.root
    }

    override fun setObserveLiveData() {
        viewModel.items.observe(this, Observer { binding.adapter?.setItems(it.first, it.second, _myPubKey) })
    }

    override fun init() {
        val type = if (arguments?.getString("tokenType") == TokenType.SOLANA.name) TokenType.SOLANA else TokenType.ANGOLA
        _myPubKey = arguments?.getString("pubKey")
        val amount = arguments?.getString("amount") ?: "0"
        viewModel.getHistory(type, _myPubKey, amount)
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0
}