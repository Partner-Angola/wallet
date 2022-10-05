package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.databinding.ActivityChallengeParticipateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChallengeParticipateActivity : BaseActivity() {
    private lateinit var binding: ActivityChallengeParticipateBinding
    private val viewModel: ChallengeParticipateViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_participate)
        binding.apply {
            lifecycleOwner = this@ChallengeParticipateActivity
            activity = this@ChallengeParticipateActivity
            vm = viewModel
        }
    }

    override fun setObserveData() {
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
        viewModel.joinSuccess.observe(this, Observer {
            setResult(RESULT_OK)
            finish()
        })
    }

    override fun init() {
        val filePath = intent.getStringExtra("filePath")
        val challenge = intent.getParcelableExtra<Challenge>("challenge")

        viewModel.challenge = challenge as Challenge
        if (!filePath.isNullOrEmpty()) {
            viewModel.filePath = filePath
            Glide.with(this@ChallengeParticipateActivity)
                .load(filePath)
                .placeholder(R.drawable.transparent)
                .centerCrop()
                .transition(DrawableTransitionOptions().transition(R.anim.fade_in))
                .into(binding.ivPhoto)
        }
    }
}