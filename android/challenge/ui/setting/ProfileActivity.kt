package com.joeware.android.gpulumera.challenge.ui.setting

import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.setting.SettingActivity
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.ProfileAdapter
import com.joeware.android.gpulumera.challenge.dialog.PhotoDetailDialog
import com.joeware.android.gpulumera.challenge.dialog.PhotoDetailDialogInterface
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeActivity
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityProfileBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ProfileActivity : BaseActivity() {
    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_profile)
        binding.apply {
            lifecycleOwner = this@ProfileActivity
            activity = this@ProfileActivity
            vm = viewModel

            adapter = ProfileAdapter().apply {
//                setHasStableIds(true)
                setOnClickListener(object : ProfileAdapter.OnClickListener {
                    override fun onClickCell(join: Join) {
                        PhotoDetailDialog.showDialog(
                            supportFragmentManager,
                            join,
                            listener = object : PhotoDetailDialogInterface {
                                override fun onChallenge(challenge: Challenge) {
                                    go2ChallengePage(challenge)
                                }
                            }
                        )
                    }
                })
            }

            rcvList.apply {
                removeItemDecoration(HorizontalSpaceItemDecoration(1))
                addItemDecoration(HorizontalSpaceItemDecoration(1))
                removeItemDecoration(VerticalSpaceItemDecoration(1))
                addItemDecoration(VerticalSpaceItemDecoration(1))
            }

            swipeContainer.setOnRefreshListener {
                viewModel.initJoinList()
            }
        }
    }

    override fun setObserveData() {
        viewModel.items.observe(this, Observer { items ->
            run {
                binding.adapter?.setItems(items)
                binding.rlyEmpty.visibility = if (items.isNotEmpty()) View.GONE else View.VISIBLE
            }
        })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
        viewModel.refreshEnd.observe(this, Observer {
            binding.swipeContainer.isRefreshing = false
        })
    }

    override fun init() {
        val user = intent.getParcelableExtra<User>("user")
        user?.let {
            binding.apply {
                isMyProfile = it.id == C.me.id
                binding.user = if (isMyProfile) C.me else it
            }
            viewModel.user = it
            viewModel.initJoinList()
        }
    }

    fun onParticipate() {
        val intent = Intent(this, ChallengeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        startActivity(intent)
    }

    fun onSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
    }
}