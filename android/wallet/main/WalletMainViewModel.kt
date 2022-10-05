package com.joeware.android.gpulumera.account.wallet.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.WalletAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.AccountUtil
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletMainViewModel : CandyViewModel() {

    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)
    private val walletAPI: WalletAPI by inject(WalletAPI::class.java)

    private val _refreshData = MutableLiveData<Void?>()
    private val _crrtPoint = MutableLiveData("0")
    private val _showErrorMessage = MutableLiveData(false)
    private val _showTokenList = MutableLiveData(false)
    private val _ethAngolaBalance = MutableLiveData<Double>()
    private val _ethBalance = MutableLiveData<Double>()
    private val _solAngolaBalance = MutableLiveData<Double>()
    private val _solBalance = MutableLiveData<Double>()

    val refreshData: LiveData<Void?> get() = _refreshData
    val crrtPoint: LiveData<String> get() = _crrtPoint
    val showErrorMessage: LiveData<Boolean> get() = _showErrorMessage
    val showTokenList: LiveData<Boolean> get() = _showTokenList
    val ethAngolaBalance: LiveData<Double> get() = _ethAngolaBalance
    val ethBalance: LiveData<Double> get() = _ethBalance
    val solAngolaBalance: LiveData<Double> get() = _solAngolaBalance
    val solBalance: LiveData<Double> get() = _solBalance

    fun initWalletMain() {
        _crrtPoint.postValue(accountUtil.getUserPoint())
        this.initToken()
    }

    private fun initToken() {
        _showTokenList.postValue(false)
        onProgress(true)
        _showErrorMessage.postValue(false)
        val ethAddress = accountUtil.getWalletEthereumAddress() ?: ""
        val ethContract = accountUtil.getEthAngolaContract()
        val solAddress = accountUtil.getWalletSolanaAddress() ?: ""
        val solContract = accountUtil.getSolAngolaContract()
        runDisposable(walletAPI.getWalletBalanceInfo(ethAddress, ethContract, solAddress, solContract), { info ->
            onProgress(false)
            if (info.code == 200) {
                _showTokenList.postValue(true)
                _ethAngolaBalance.postValue(info.asset.ethTokenBalance)
                _ethBalance.postValue(info.asset.ethBalance)
                _solAngolaBalance.postValue(info.asset.solTokenBalance)
                _solBalance.postValue(info.asset.solBalance)
            } else {
                _showErrorMessage.postValue(true)
            }
        }){
            onProgress(false)
            _showErrorMessage.postValue(true)
        }
    }

    fun refreshCall() = initWalletMain()
}