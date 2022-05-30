package com.joeware.android.gpulumera.nft.ui

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.aghajari.emojiview.AXEmojiManager
import com.aghajari.emojiview.iosprovider.AXIOSEmojiProvider
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityNftMainBinding
import com.joeware.android.gpulumera.nft.ui.camera.NftCameraFragment
import com.joeware.android.gpulumera.nft.ui.community.NftCommunityFragment
import com.joeware.android.gpulumera.nft.ui.gallery.NftGalleryActivity
import com.joeware.android.gpulumera.nft.ui.market.NftMarketFragment
import com.joeware.android.gpulumera.nft.ui.my.NftMyFragment
import com.joeware.android.gpulumera.nft.ui.search.NftSearchFragment
import com.joeware.android.gpulumera.nft.ui.wallet.NftWalletActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

class NftMainActivity : BaseActivity() {

    private lateinit var binding: ActivityNftMainBinding
    private val viewModel: NftMainViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_nft_main)
        binding.lifecycleOwner = this
        binding.vpMain.isUserInputEnabled = false
        binding.vpMain.offscreenPageLimit = 1
        binding.vpMain.adapter = NftMainPagerAdapter(this)
        AXEmojiManager.install(this, AXIOSEmojiProvider(this))
    }

    override fun setObserveData() {
    }

    override fun init() {
        setOnClickEvent()
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.fade_in, R.anim.slide_down)
    }

    private fun setOnClickEvent() {
        binding.btnClose.setOnClickListener { finish() }
        binding.btnWallet.setOnClickListener { startActivity(Intent(this, NftWalletActivity::class.java)) }
        binding.btnMenuHome.setOnClickListener { binding.vpMain.setCurrentItem(0, false);updateMenuIcon(0)}
        binding.btnMenuSearch.setOnClickListener { binding.vpMain.setCurrentItem(1, false) ;updateMenuIcon(1)}
        binding.btnMenuMy.setOnClickListener { binding.vpMain.setCurrentItem(2, false) ;updateMenuIcon(2)}
        binding.btnMenuCommunity.setOnClickListener { binding.vpMain.setCurrentItem(3, false) ;updateMenuIcon(3)}
        binding.btnMenuMarket.setOnClickListener { binding.vpMain.setCurrentItem(4, false) ;updateMenuIcon(4)}
        binding.btnMenuMinting.setOnClickListener { startActivity(Intent(this, NftGalleryActivity::class.java))}
    }

    private fun updateMenuIcon(idx: Int) {
        when (idx) {
            0 -> {
                binding.btnMenuHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_home_on, 0, 0)
                binding.btnMenuMy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_collection_off, 0, 0)
                binding.btnMenuSearch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_explore_off, 0, 0)
                binding.btnMenuCommunity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_commu_off, 0, 0)
                binding.btnMenuMarket.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_market_off, 0, 0)
            }
            1 -> {
                binding.btnMenuHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_home_off, 0, 0)
                binding.btnMenuMy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_collection_off, 0, 0)
                binding.btnMenuSearch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_explore_on, 0, 0)
                binding.btnMenuCommunity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_commu_off, 0, 0)
                binding.btnMenuMarket.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_market_off, 0, 0)
            }
            2 -> {
                binding.btnMenuHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_home_off, 0, 0)
                binding.btnMenuMy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_collection_on, 0, 0)
                binding.btnMenuSearch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_explore_off, 0, 0)
                binding.btnMenuCommunity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_commu_off, 0, 0)
                binding.btnMenuMarket.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_market_off, 0, 0)
            }
            3 -> {
                binding.btnMenuHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_home_off, 0, 0)
                binding.btnMenuMy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_collection_off, 0, 0)
                binding.btnMenuSearch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_explore_off, 0, 0)
                binding.btnMenuCommunity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_commu_on, 0, 0)
                binding.btnMenuMarket.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_market_off, 0, 0)
            }
            4 -> {
                binding.btnMenuHome.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_home_off, 0, 0)
                binding.btnMenuMy.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_collection_off, 0, 0)
                binding.btnMenuSearch.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_explore_off, 0, 0)
                binding.btnMenuCommunity.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_commu_off, 0, 0)
                binding.btnMenuMarket.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.menu_btn_market_on, 0, 0)
            }
        }
    }

    private inner class NftMainPagerAdapter(fa: FragmentActivity) : FragmentStateAdapter(fa) {
        private val fragmentList = listOf(
            NftCameraFragment(),
            NftSearchFragment(),
            NftMyFragment(),
            NftCommunityFragment(),
            NftMarketFragment()
        )

        override fun getItemCount(): Int = fragmentList.size
        override fun createFragment(position: Int): Fragment = fragmentList[position]
    }
}