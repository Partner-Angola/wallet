package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class WalletInputSeedViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _dismissDialog = SingleLiveEvent<Void>()
    private val _successCheckSeed = SingleLiveEvent<Void>()
    private val _passwordInconsistency = MutableLiveData<Boolean>(false)

    val dismissDialog: LiveData<Void> get() = _dismissDialog
    val successCheckSeed: LiveData<Void> get() = _successCheckSeed
    val passwordInconsistency: LiveData<Boolean> get() = _passwordInconsistency

    fun prevPage() = _dismissDialog.call()

    fun checkSeed(seed: String) {
        if (seed == prefUtil.walletMnemonic) {
            _successCheckSeed.call()
        } else {
            _passwordInconsistency.postValue(true)
        }
    }
}