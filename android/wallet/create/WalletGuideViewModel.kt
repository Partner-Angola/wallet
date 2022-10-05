package com.joeware.android.gpulumera.account.wallet.create

import android.content.Context
import android.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletGuide
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.SingleLiveEvent

class WalletGuideViewModel : CandyViewModel() {

    private val _pagerItems = MutableLiveData<List<WalletGuide>>()
    private val _currentPagerIndex = MutableLiveData<Int>()
    private val _finishWalletDescription = SingleLiveEvent<Void>()
    private val _finishParentActivity = SingleLiveEvent<Void>()

    val pagerItems: LiveData<List<WalletGuide>> get() = _pagerItems
    val currentPagerIndex: LiveData<Int> get() = _currentPagerIndex
    val finishWalletDescription: LiveData<Void> get() = _finishWalletDescription
    val finishParentActivity: LiveData<Void> get() = _finishParentActivity

    fun makeViewPagerList(context: Context) {
        _pagerItems.postValue(listOf(
            WalletGuide(
                context.getString(R.string.nft_login_desc_title_1),
                context.getString(R.string.nft_login_desc_guide_1),
                R.drawable.onboarding_wallet_1,
                Color.parseColor("#99454545")
            ),
            WalletGuide(
                context.getString(R.string.nft_login_desc_title_2),
                context.getString(R.string.nft_login_desc_guide_2),
                R.drawable.onboarding_wallet_2,
                Color.parseColor("#99454545")
            ),
            WalletGuide(
                context.getString(R.string.nft_login_desc_title_3),
                context.getString(R.string.nft_login_desc_guide_3),
                R.drawable.onboarding_wallet_3,
                Color.parseColor("#99454545")
            ),
            WalletGuide(
                context.getString(R.string.nft_login_desc_title_4),
                context.getString(R.string.nft_login_desc_guide_4),
                R.drawable.onboarding_wallet_4,
                Color.parseColor("#ff752f")
            )
        ))
    }

    fun nextPage(position: Int) {
        _pagerItems.value?.let { list ->
            if (list.lastIndex == position) {
                _finishWalletDescription.call()
            } else {
                _currentPagerIndex.postValue(position + 1)
            }
        } ?: run { _finishParentActivity.call() }

    }
}