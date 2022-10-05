package com.joeware.android.gpulumera.challenge.ui.setting

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityIntroInputBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class IntroInputActivity : BaseActivity() {
    private lateinit var binding: ActivityIntroInputBinding
    private val viewModel: IntroInputViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_intro_input)
        binding.apply {
            lifecycleOwner = this@IntroInputActivity
            activity = this@IntroInputActivity
            vm = viewModel
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
    }

    fun onOk() {
        viewModel.saveUser(
            C.me.id,
            C.me.nickname,
            viewModel.intro.value!!,
            C.me.point,
            null
        )
    }
}