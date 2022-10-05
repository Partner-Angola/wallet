package com.joeware.android.gpulumera.challenge.ui.vote

import android.content.Intent
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.user.LoginDialog
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.VotePagerAdapter
import com.joeware.android.gpulumera.challenge.dialog.PhotoDetailDialog
import com.joeware.android.gpulumera.challenge.dialog.ReportDialog
import com.joeware.android.gpulumera.challenge.dialog.ReportDialogInterface
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.challenge.ui.setting.ProfileActivity
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityVoteDetailBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoteDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityVoteDetailBinding
    private val viewModel: VoteDetailViewModel by viewModel()
    private val voteListDataUtil: VoteListDataUtil by inject()

    override fun onBackPressed() {
        finish()
    }

    override fun finish() {
        val intent = Intent()
        voteListDataUtil.clear()
        binding.adapter?.getItems()?.toCollection(ArrayList())?.let {
            voteListDataUtil.setRequestVoteDetailActivity(it)
        }
        setResult(RESULT_OK, intent)
        super.finish()
    }

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_detail)
        binding.apply {
            lifecycleOwner = this@VoteDetailActivity
            activity = this@VoteDetailActivity
            vm = viewModel

            adapter = VotePagerAdapter().apply {
                setOnClickListener(object : VotePagerAdapter.OnClickListener {
                    override fun onClickVisitProfile(user: User) {
                        val intent = Intent(this@VoteDetailActivity, ProfileActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }
                })
            }
            vpContainer.apply {
                clipToPadding = false
                setPadding(ConvertUtils.dp2px(23f), 0, ConvertUtils.dp2px(23f), 0)
                addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
                    override fun onPageScrolled(
                        position: Int,
                        positionOffset: Float,
                        positionOffsetPixels: Int
                    ) {
                    }

                    override fun onPageSelected(position: Int) {
                        val selectedItem = binding.adapter?.getVote(position)
                        selectedItem?.let {
                            voteEnable = !it.voted
                            if (C.me.id == selectedItem.user.id) {
                                binding.btnBlame.visibility = View.INVISIBLE
                            } else {
                                binding.btnBlame.visibility = View.VISIBLE
                            }
                        }

                        if (position + 3 > binding.adapter?.count!!) {
                            viewModel.getJoinList()
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            }
        }
    }

    override fun setObserveData() {
        viewModel.items.observe(this, Observer { binding.adapter?.setItems(it) })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
    }

    override fun init() {
        // 투표버튼 현시/비현시처리 (참가상세화면에서 넘어왔을때에는 비현시처리)
        val voteBtnShow = voteListDataUtil.voteBtnShow
        binding.btnVote.visibility = if (voteBtnShow) View.VISIBLE else View.GONE

        voteListDataUtil.selectChallenge?.let { challenge ->
            binding.challenge = challenge
            viewModel.challenge = challenge
            binding.tvSubtitle.text = challenge.voteLeftTime
        }

        val pos = voteListDataUtil.selectPosition
        val joinList = voteListDataUtil.selectChallengeList

        joinList?.let {
            viewModel.setList(it)
            binding.voteEnable = !it[pos].voted
        }
        if (pos > 0) {
            binding.vpContainer.postDelayed({
                binding.vpContainer.setCurrentItem(pos, false)
            }, 100)
        }
    }

    fun onBlame() {
        if (C.me.isLogin) {
            val pos = binding.vpContainer.currentItem
            val join = binding.adapter?.getVote(pos)

            join?.let {
                if (C.me.id == join.user.id) {
                    it.rank = pos + 1   // 순위정보설정
                    PhotoDetailDialog.showDialog(
                        supportFragmentManager,
                        it,
                        showMedal = false,
                        showInfo = false
                    )
                } else {
                    ReportDialog.showDialog(supportFragmentManager, object : ReportDialogInterface {
                        override fun onSelect(text: String) {
                            viewModel.reportJoin(it.id, text)
                        }
                    })
                }
            }
        } else {
            LoginDialog.showDialog(supportFragmentManager, {
                onLoginSuccess()
            }) {}
        }
    }

    fun onVote() {
        if (C.me.isLogin) {
            val pos = binding.vpContainer.currentItem
            val join = binding.adapter?.getVote(pos)
            join?.let {
                if (!it.voted) viewModel.vote(it.id, it.user.uid) {
                    voteSuccess(pos, true)
                } else viewModel.cancelVote(it.id) {
                    voteSuccess(pos, false)
                }
            }
        } else {
            LoginDialog.showDialog(supportFragmentManager, {
                onLoginSuccess()
            }) {}
        }
    }

    private fun voteSuccess(pos: Int, isVoted: Boolean) {
        binding.apply {
            voteEnable = !isVoted
            adapter?.toggleVote(pos)
        }
    }

    private fun onLoginSuccess() {
        if (C.IS_CANDY_POINT_API_SERVER_LOGIN) {
            FirebaseAuth.getInstance().currentUser?.let { user ->
                viewModel.getUserInfo(user.uid)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
