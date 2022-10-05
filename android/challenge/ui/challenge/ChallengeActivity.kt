package com.joeware.android.gpulumera.challenge.ui.challenge

import android.content.Intent
import android.view.View
import android.view.animation.AlphaAnimation
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.Observer
import com.google.android.material.appbar.AppBarLayout
import com.google.firebase.auth.FirebaseAuth
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.dialog.WalletPinDialog
import com.joeware.android.gpulumera.account.setting.SettingActivity
import com.joeware.android.gpulumera.account.user.LoginDialog
import com.joeware.android.gpulumera.account.wallet.main.WalletMainActivity
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.fragment.ChallengeEndFragment
import com.joeware.android.gpulumera.challenge.fragment.ChallengeProgressFragment
import com.joeware.android.gpulumera.challenge.ui.setting.ProfileActivity
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.ActivityChallengeBinding
import com.joeware.android.gpulumera.nft.ui.NftMainActivity
import com.joeware.android.gpulumera.util.AccountUtil
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent
import kotlin.math.abs

class ChallengeActivity : BaseActivity() {

    companion object {
        private const val PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR = 0.9f
        private const val ALPHA_ANIMATIONS_DURATION = 200L
    }

    private lateinit var binding: ActivityChallengeBinding
    private val viewModel: ChallengeViewModel by viewModel()
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)
    private val accountUtil: AccountUtil by KoinJavaComponent.inject(AccountUtil::class.java)

    private var isTitleVisible = false

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge)
        binding.apply {
            lifecycleOwner = this@ChallengeActivity
            activity = this@ChallengeActivity
            vm = viewModel

            if (C.me?.isLogin == true) {
                me = C.me
            } else if (prefUtil.isLogin) {
                viewModel.login()
            }
            adapter = ChallengePagerAdapter(supportFragmentManager)
            lyTab.setupWithViewPager(vpContainer)
        }
    }

    override fun setObserveData() {
        viewModel.finishActivity.observe(this, Observer { finish();})
        viewModel.refreshLogin.observe(this, Observer {
            binding.me = C.me
        })
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down_chall)
    }

    override fun init() {
        startAlphaAnimation(binding.tvMainTitle, 0, View.INVISIBLE)
        binding.abLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

            handleToolbarTitleVisibility(percentage)
        })
        binding.btnNftCamera.setOnClickListener {
            startActivity(Intent(this, NftMainActivity::class.java))
//            setResult(RESULT_OK)
//            finish()
        }
        binding.btnWallet.setOnClickListener {
            accountUtil.checkLoginAndWallet(supportFragmentManager, {
                if (prefUtil.isUseWalletLock) {
                    WalletPinDialog.showCheckDialog(supportFragmentManager, {
                        startActivity(Intent(this, WalletMainActivity::class.java))
                    })
                } else {
                    startActivity(Intent(this, WalletMainActivity::class.java))
                }
            }){
                Toast.makeText(this, getString(R.string.sign_up_fail_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun handleToolbarTitleVisibility(percentage: Float) {
        if (percentage >= PERCENTAGE_TO_SHOW_TITLE_AT_TOOLBAR) {
            if (!isTitleVisible) {
                startAlphaAnimation(binding.tvMainTitle, ALPHA_ANIMATIONS_DURATION, View.VISIBLE)
                isTitleVisible = true
            }
        } else {
            if (isTitleVisible) {
                startAlphaAnimation(binding.tvMainTitle, ALPHA_ANIMATIONS_DURATION, View.INVISIBLE)
                isTitleVisible = false
            }
        }
    }

    private fun startAlphaAnimation(
        v: View,
        duration: Long,
        visibility: Int
    ) {
        val alphaAnimation = if (visibility == View.VISIBLE)
            AlphaAnimation(0f, 1f)
        else
            AlphaAnimation(1f, 0f)

        alphaAnimation.duration = duration
        alphaAnimation.fillAfter = true
        v.startAnimation(alphaAnimation)
    }

    fun onSetting() {
        startActivity(Intent(this, SettingActivity::class.java))
    }

    fun onProfile() {
        if (C.me.isLogin) {
            val intent = Intent(this, ProfileActivity::class.java)
            intent.putExtra("user", C.me)
            startActivity(intent)
        } else {
            requestLogin()
        }
    }

    fun requestLogin() {
        LoginDialog.showDialog(supportFragmentManager, {
            if (C.IS_CANDY_POINT_API_SERVER_LOGIN) {
                FirebaseAuth.getInstance().currentUser?.let { user ->
                    viewModel.getUserInfo(user.uid)
                }
            }
        }) {}

    }

    inner class ChallengePagerAdapter(fm: FragmentManager) :
        FragmentPagerAdapter(fm, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {

        override fun getCount(): Int = 2

        override fun getItem(p0: Int): Fragment =
            when (p0) {
                0 -> ChallengeProgressFragment.newInstance()
                1 -> ChallengeEndFragment.newInstance()
                else -> ChallengeProgressFragment.newInstance()
            }

        override fun getPageTitle(position: Int): CharSequence {
            return when (position) {
                0 -> getString(R.string.progressing_challenge)
                1 -> getString(R.string.ended_challenge)
                else -> ""
            }
        }
    }
}