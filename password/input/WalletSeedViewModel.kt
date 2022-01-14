package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class WalletSeedViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _walletMnemonic = MutableLiveData<String>()
    private val _clickClose = SingleLiveEvent<Void>()

    val walletMnemonic: MutableLiveData<String> get() = _walletMnemonic
    val clickClose: SingleLiveEvent<Void> get() = _clickClose


    fun init() {
        _walletMnemonic.value = prefUtil.walletMnemonic
    }

    fun clickClose() = _clickClose.call()


}