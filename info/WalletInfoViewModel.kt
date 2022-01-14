package com.joeware.android.gpulumera.reward.ui.wallet.info

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.reward.api.WalletAPI
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class WalletInfoViewModel : CandyViewModel() {

    /************************************************************************************
     * 지갑 정보
     ***********************************************************************************/
    private val walletApi: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _clickReceiveToken = SingleLiveEvent<Void>()
    private val _clickSendToken = SingleLiveEvent<Void>()
    private val _clickBack = SingleLiveEvent<Void>()
    private val _clickRefresh = SingleLiveEvent<Void>()
    private val _showWalletReceiveDialog = MutableLiveData<Map<String, String>>()
    private val _showWalletTransactionDialog = MutableLiveData<Map<String, String>>()
    private val _tokenAngolaAmount = MutableLiveData("0")
    private val _tokenSolanaAmount = MutableLiveData("0")
    private val _visibilityProgress = MutableLiveData<Int>()

    val clickReceiveToken: LiveData<Void> get() = _clickReceiveToken
    val clickSendToken: LiveData<Void> get() = _clickSendToken
    val clickBack: LiveData<Void> get() = _clickBack
    val clickRefresh: LiveData<Void> get() = _clickRefresh
    val showWalletReceiveDialog: LiveData<Map<String, String>> get() = _showWalletReceiveDialog
    val showWalletTransactionDialog: LiveData<Map<String, String>> get() = _showWalletTransactionDialog
    val tokenAngolaAmount: LiveData<String> get() = _tokenAngolaAmount
    val tokenSolanaAmount: LiveData<String> get() = _tokenSolanaAmount
    val visibilityProgress: LiveData<Int> get() = _visibilityProgress

    fun clickReceiveToken() = _clickReceiveToken.call()
    fun clickSendToken() = _clickSendToken.call()

    private lateinit var mnemonic: String
    private lateinit var pubKey: String
    private var progressState = mutableMapOf("ang" to false, "sol" to false)

    fun initWalletInfo(mnemonic: String, pubKey: String) {
        this.mnemonic = mnemonic
        this.pubKey = pubKey
        getTokenGetBalance()
    }

    fun showWalletReceiveDialog() {
        _showWalletReceiveDialog.postValue(mapOf("mnemonic" to mnemonic, "pubKey" to pubKey))
    }
    fun showWalletTransactionDialog() {
        _showWalletTransactionDialog.postValue(mapOf("mnemonic" to mnemonic, "pubKey" to pubKey))
    }

    fun getTokenGetBalance() {
        progressState["ang"] = false;progressState["sol"] = false
        _visibilityProgress.postValue(View.VISIBLE)
        runDisposable(walletApi.tokenGetBalance(pubKey = pubKey), { amountInfo ->
            progressState["ang"] = true;if (progressState["ang"] == true && progressState["sol"] == true) _visibilityProgress.postValue(View.INVISIBLE)
            _tokenAngolaAmount.postValue(amountInfo.amount)
        }) {
            progressState["ang"] = true;if(progressState["ang"] == true && progressState["sol"] == true) _visibilityProgress.postValue(View.INVISIBLE)
            _tokenAngolaAmount.postValue("서버 통신 실패 0")
        }
        runDisposable(walletApi.solanaGetBalance(pubKey = pubKey), { amountInfo ->
            progressState["sol"] = true;if(progressState["ang"] == true && progressState["sol"] == true) _visibilityProgress.postValue(View.INVISIBLE)
            _tokenSolanaAmount.postValue(amountInfo.amount)
            _visibilityProgress.postValue(View.INVISIBLE)
        }) {
            progressState["sol"] = true;if (progressState["ang"] == true && progressState["sol"] == true) _visibilityProgress.postValue(View.INVISIBLE)
            _tokenSolanaAmount.postValue("서버 통신 실패 0")
        }
    }

    fun logoutWallet() {
        prefUtil.walletMnemonic = null
        prefUtil.walletPin = null
        prefUtil.walletBio = false
        prefUtil.walletPassword = null
    }

    fun clickBack() = _clickBack.call()
    fun clickRefresh() = _clickRefresh.call()
}