package com.joeware.android.gpulumera.challenge.ui.setting

import android.text.InputFilter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityNicknameInputBinding
import org.koin.androidx.viewmodel.ext.android.viewModel


class NicknameInputActivity : BaseActivity() {
    private lateinit var binding: ActivityNicknameInputBinding
    private val viewModel: NicknameInputViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nickname_input)
        binding.apply {
            lifecycleOwner = this@NicknameInputActivity
            activity = this@NicknameInputActivity
            vm = viewModel

            val filter = InputFilter { source, _, _, _, _, _ ->
                source.toString().replace(regex = "[^a-zA-Z0-9_.]".toRegex(), replacement = "")
            }
            etIntro.filters = arrayOf(filter, InputFilter.LengthFilter(20))
        }
    }

    override fun setObserveData() {
        viewModel.showToastMessage.observe(this, Observer {
            ToastUtils.showShort(it)
        })
        viewModel.saveSuccess.observe(this, Observer {
            ToastUtils.showShort(R.string.save_success)
            finish()
        })
    }

    override fun init() {
        binding.etIntro.privateImeOptions = "defaultInputmode=english;"  // 키보드 뜰 때 영어로 뜨도록 설정
    }

    fun onOk() {
        viewModel.saveUser(
            C.me.id,
            viewModel.nickname.value!!,
            C.me.intro,
            C.me.point,
            null
        )
    }
}