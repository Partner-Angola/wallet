package com.joeware.android.gpulumera.challenge.ui.challenge

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.viewpager.widget.ViewPager
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.VotePagerAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.challenge.ui.setting.ProfileActivity
import com.joeware.android.gpulumera.databinding.ActivityPrizeDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel
import java.text.NumberFormat

class PrizeDetailActivity : BaseActivity() {
    private lateinit var binding: ActivityPrizeDetailBinding
    private val viewModel: PrizeDetailViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prize_detail)
        binding.apply {
            lifecycleOwner = this@PrizeDetailActivity
            activity = this@PrizeDetailActivity
            vm = viewModel

            adapter = VotePagerAdapter(true).apply {
                setOnClickListener(object : VotePagerAdapter.OnClickListener {
                    override fun onClickVisitProfile(user: User) {
                        val intent = Intent(this@PrizeDetailActivity, ProfileActivity::class.java)
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
                        if (position + 3 > binding.adapter?.count!!) {
                            viewModel.getJoinList {}
                        }
                    }

                    override fun onPageScrollStateChanged(state: Int) {
                    }
                })
            }
        }
    }

    override fun setObserveData() {
        viewModel.items.observe(this, Observer { items -> binding.adapter?.setItems(items) })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
    }

    override fun init() {
        val challenge = intent.getParcelableExtra<Challenge>("challenge") as Challenge
        binding.challenge = challenge
        viewModel.challenge = challenge
        binding.tvSubtitle.text = String.format(
            getString(R.string.prize_entry_point),
            NumberFormat.getInstance()
                .format(if (challenge.prize.isNotEmpty()) challenge.prize.reduce { a, b -> a + b } else 0),
            NumberFormat.getInstance().format(challenge.joinTotalCount)
        )

        val pos = intent.getIntExtra("pos", 0)
        val joinList = intent.getParcelableArrayListExtra<Join>("joins")
        if (joinList.isNullOrEmpty()) {
            viewModel.getJoinList {
                setCurrentPos(pos)
            }
        } else {
            viewModel.setList(joinList)
            setCurrentPos(pos)
        }
    }

    private fun setCurrentPos(pos: Int) {
        if (pos > 0) {
            binding.vpContainer.postDelayed({
                binding.vpContainer.setCurrentItem(pos, false)
            }, 100)
        }
    }
}