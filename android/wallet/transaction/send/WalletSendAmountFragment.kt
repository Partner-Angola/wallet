package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletToken
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletSendAmountBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletSendAmountFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletSendAmountBinding
    private val parentViewModel: WalletSendViewModel by sharedViewModel()
    private var selectToken: WalletToken? = null

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletSendAmountBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        parentViewModel.selectToken.observe(this) { token ->
            selectToken = token
            binding.tvSymbol.text = token.symbol
            binding.tvMyAmount.text = "${token.amountStr} ${token.symbol}"
        }

    }

    override fun init() {
        setAmountInput()
        binding.btnMax.setOnClickListener {
            var amountStr = selectToken?.amountStr ?: "0"
            if (amountStr == "0.0") amountStr = "0"
            binding.etAmount.setText(amountStr)
        }
        binding.btnBack.setOnClickListener {
            parentViewModel.prevPage()
        }
        binding.btnNext.setOnClickListener {
            try {
                val amount: Double = binding.etAmount.text.toString().toDouble()
                if ((selectToken?.amountDouble ?: 0.0) < amount) {
                    Toast.makeText(requireContext(), getString(R.string.wallet_send_amount_error1), Toast.LENGTH_SHORT).show()
                } else if(amount == 0.0) {
                    Toast.makeText(requireContext(), getString(R.string.wallet_send_amount_error2), Toast.LENGTH_SHORT).show()
                } else {
                    parentViewModel.setSendAmount(amount)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), getString(R.string.wallet_send_amount_error3), Toast.LENGTH_SHORT).show()
            }
        }

    }

    /******************************************************
     * EditText 입력 제한
     * 소수점 제한, 소수점 8자리 이상 제한
     ******************************************************/
    private var prevAmount = ""

    private fun setAmountInput() {
        binding.etAmount.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, end: Int) {
                prevAmount = s.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, count: Int, end: Int) {
                if (s.toString() == prevAmount) return
                if (s.toString().count { it == '.' } > 1) {
                    binding.etAmount.setText(prevAmount)
                    binding.etAmount.setSelection(binding.etAmount.length())
                }
                val dec = s.toString().split(".")
                if (dec.size > 1 && dec[dec.lastIndex].length > 8) {
                    binding.etAmount.setText(prevAmount)
                    binding.etAmount.setSelection(binding.etAmount.length())
                }
            }

            override fun afterTextChanged(s: Editable?) {
            }
        })


    }


}