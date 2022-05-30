package com.joeware.android.gpulumera.nft.ui.my

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.challenge.ui.setting.SettingActivity
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.FragmentNftMyBinding
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.java.standalone.KoinJavaComponent
import kotlin.math.abs

class NftMyFragment : BaseFragment() {

    private lateinit var binding: FragmentNftMyBinding
    private val viewModel: NftMyViewModel by sharedViewModel()
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentNftMyBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        binding.vpContainer.isUserInputEnabled = false
        binding.vpContainer.adapter = NftMyPagerAdapter(requireActivity())

        if (C.me.isLogin) {
            binding.me = C.me
        } else if (prefUtil.isLogin) {
            viewModel.login()
        }
        return binding.root
    }

    override fun setObserveData() {
        viewModel.refreshLogin.observe(this, Observer {
            binding.me = C.me
        })
    }

    override fun init() {
        binding.btnSetting.setOnClickListener{
            startActivity(Intent(requireActivity(), SettingActivity::class.java))
        }
//        this.initTabLayout()
//        this.initAnimation()
    }

    override fun onResume() {
        super.onResume()
        viewModel.callResume()
    }

    private fun initAnimation() {
            binding.abLayout.addOnOffsetChangedListener(AppBarLayout.OnOffsetChangedListener { appBarLayout, verticalOffset ->
            val maxScroll = appBarLayout.totalScrollRange
            val percentage = abs(verticalOffset).toFloat() / maxScroll.toFloat()

            if (percentage >= 0.9f) {
                if (binding.lyToolbar.visibility == View.INVISIBLE) {
                    binding.lyToolbar.startAnimation(AlphaAnimation(0f, 1f).apply { duration = 200L;fillAfter = true })
                    binding.lyToolbar.visibility = View.VISIBLE
                }
            } else {
                if (binding.lyToolbar.visibility == View.VISIBLE) {
                    binding.lyToolbar.startAnimation(AlphaAnimation(1f, 0f).apply { duration = 200L;fillAfter = true })
                    binding.lyToolbar.visibility = View.INVISIBLE
                }
            }
        })
    }

    private fun initTabLayout() {
        val tabTitleArray = arrayOf(
            getString(R.string.nft_title_collection),
            getString(R.string.nft_title_feed)
        )
        TabLayoutMediator(binding.lyTab, binding.vpContainer) { tab, position ->
            tab.text = tabTitleArray[position]
        }.attach()
    }

    private inner class NftMyPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val fragmentList = listOf(
            NftMyCollectionFragment()
//            NftMyFeedFragment()
        )
        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}