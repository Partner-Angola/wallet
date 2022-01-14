package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletCreatePinBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel

class WalletCreatePinFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletCreatePinBinding
    private val viewModel: WalletCreateAuthViewModel by sharedViewModel()
    private var isConfirm = false

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletCreatePinBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vm = viewModel
        return binding.root
    }

    override fun setObserveLiveData() {

    }

    override fun init() {
        arguments?.getBoolean("isConfirm")?.let { isConfirm ->
            this.isConfirm = isConfirm
            binding.isConfirm = isConfirm
            if (isConfirm) {
                binding.tvTitle.text = "암호 확인"
                binding.tvContents.text = "입력한 암호 6자리를\n다시 한 번 입력해 주세요."
            } else {
                binding.tvTitle.text = "암호 생성"
                binding.tvContents.text = "지갑 잠금 해제를 위한\n새로운 암호 6자리를 입력해 주세요."
            }
        }
        viewModel.shufflePinNumber()
    }

    override fun onDestroyView() {
        if (isConfirm) {
            viewModel.refreshMyPinConfirmData()
            viewModel.refreshMyPinData()
        } else {
            viewModel.refreshMyPinData()
        }
        super.onDestroyView()
    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0


}