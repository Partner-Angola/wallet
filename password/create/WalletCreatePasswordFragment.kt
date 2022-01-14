package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.text.InputFilter
import android.text.Spanned
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.joeware.android.gpulumera.base.CandyFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletCreatePasswordBinding
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import java.util.regex.Pattern

class WalletCreatePasswordFragment : CandyFragment() {

    private lateinit var binding: FragmentWalletCreatePasswordBinding
    private val viewModel: WalletCreateAuthViewModel by sharedViewModel()
    private var isConfirm = false

    override fun getLayoutView(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletCreatePasswordBinding.inflate(inflater, container, false)
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
                binding.tvTitle.text = "인증 비밀번호 확인"
                binding.tvContents.text = "입력한 인증 비밀번호를\n다시 한 번 입력해 주세요."
                binding.etPassword.hint = "인증번호를 다시 입력해 주세요."
                binding.btnWalletCreate.text = "지갑 생성"
            } else {
                binding.tvTitle.text = "인증 비밀번호 생성"
                binding.tvContents.text = "대문자와 소문자, 특수문자(#제외)를 포함하여\n9자리 이상의 새로운 인증 비밀번호를 입력해주세요."
                binding.etPassword.hint = "새로운 인증 비밀번호를 입력해 주세요."
                binding.btnWalletCreate.text = "다음"
            }
        }
        binding.btnWalletCreate.setOnClickListener { viewModel.checkPassword(isConfirm, binding.etPassword.text.toString()) }
        binding.etPassword.privateImeOptions = "defaultInputmode=english;"
        binding.etPassword.filters = arrayOf(object: InputFilter {
            override fun filter(source: CharSequence?, start: Int, end: Int, dest: Spanned?, dstart: Int, dend: Int): CharSequence {
                val pattern = Pattern.compile("^[a-zA-Z0-9!@$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?~`]+$")
                if (source != null) {
                    if(source == "" || pattern.matcher(source).matches()){
                        return source
                    }
                    Toast.makeText(context, "입력 불가능한 문자를 입력하였습니다.", Toast.LENGTH_SHORT).show()
                } else {
                    return ""
                }
                return ""
            }

        })

    }

    override fun findViews(root: View?) {}
    override fun getLayoutRes(): Int = 0

    override fun onDestroyView() {
        if (isConfirm) {
            viewModel.refreshMyPasswordConfirmData()
            viewModel.refreshMyPasswordData()
        } else {
            viewModel.refreshMyPasswordData()
            viewModel.refreshMyPinConfirmData()
        }
        super.onDestroyView()
    }
}