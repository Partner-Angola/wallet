package com.joeware.android.gpulumera.account.wallet.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.account.wallet.model.WalletInfo
import com.joeware.android.gpulumera.api.WalletAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import org.koin.java.standalone.KoinJavaComponent

class WalletRestoreViewModel : CandyViewModel() {

    private val walletAPI: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)

    private val _walletInfo = MutableLiveData<WalletInfo>()

    val walletInfo: LiveData<WalletInfo> get() = _walletInfo

    fun getSeed(password: String, seed: String) {
        runDisposable(walletAPI.restoreWallet(password, seed), { walletInfo ->
            _walletInfo.postValue(walletInfo)
        }) { onError(it) }
    }
}