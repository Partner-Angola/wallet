package com.joeware.android.gpulumera.account.wallet.create

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.account.wallet.model.WalletInfo
import com.joeware.android.gpulumera.api.WalletAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletBackupViewModel : CandyViewModel() {

    private val walletAPI: WalletAPI by inject(WalletAPI::class.java)

    private val _seed = MutableLiveData<String>()
    private val _walletInfo = MutableLiveData<WalletInfo>()

    val seed: LiveData<String> get() = _seed
    val walletInfo: LiveData<WalletInfo> get() = _walletInfo

    fun getSeed(password: String) {
        runDisposable(walletAPI.createWallet(password), { walletInfo ->
            _seed.postValue(walletInfo.mnemonic)
            _walletInfo.postValue(walletInfo)
        }) { onError(it) }
    }
}