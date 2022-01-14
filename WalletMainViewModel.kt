package com.joeware.android.gpulumera.reward.ui.wallet

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.reward.api.WalletAPI
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletMainViewModel : CandyViewModel() {

    /************************************************************************************
     * 지갑 정보
     ***********************************************************************************/

    private val walletApi: WalletAPI by inject(WalletAPI::class.java)
    private val prefUtil: PrefUtil by inject(PrefUtil::class.java)

    private var _myPubKey: String? = null
    private var _mnemonic: String? = null

    private val _angolaAmount = MutableLiveData<String>("0")
    private val _solanaAmount = MutableLiveData<String>("0")
    private val _showWalletCreateGuidDialog = SingleLiveEvent<Void>()
    private val _showWalletRestoreDialog = SingleLiveEvent<Void>()
    private val _isSuccessRestore = SingleLiveEvent<Boolean>()
    private val _showWalletCreateAuthActivity = MutableLiveData<String?>()

    val angolaAmount: LiveData<String> get() = _angolaAmount
    val solanaAmount: LiveData<String> get() = _solanaAmount
    val showWalletCreateGuidDialog: LiveData<Void> get() = _showWalletCreateGuidDialog
    val showWalletRestoreDialog: LiveData<Void> get() = _showWalletRestoreDialog
    val isSuccessRestore: LiveData<Boolean> get() = _isSuccessRestore
    val showWalletCreateAuthActivity: LiveData<String?> get() = _showWalletCreateAuthActivity

    fun getTokenGetBalance(mnemonic: String) {
        _mnemonic = mnemonic
        if (_myPubKey == null) {
            runDisposable(walletApi.restoreWallet(mnemonic), {
                _myPubKey = it.pubKey
                runDisposable(walletApi.tokenGetBalance(pubKey = it.pubKey), { info ->
                    _angolaAmount.postValue( "${info.amount}")
                }) { _angolaAmount.postValue("서버 통신 실패") }

                runDisposable(walletApi.solanaGetBalance(pubKey = it.pubKey), { info ->
                    _solanaAmount.postValue( "${info.amount}")
                }) { _solanaAmount.postValue("서버 통신 실패") }

            }) { _angolaAmount.postValue("서버 통신 실패") }
        } else {
            runDisposable(walletApi.tokenGetBalance(pubKey = _myPubKey!!), { info ->
                _angolaAmount.postValue(info.amount)
            }) { _angolaAmount.postValue("서버 통신 실패") }

            runDisposable(walletApi.solanaGetBalance(pubKey = _myPubKey!!), { info ->
                _solanaAmount.postValue( "${info.amount}")
            }) { _solanaAmount.postValue("서버 통신 실패") }
        }
    }

    fun showWalletCreateGuildDialog() = _showWalletCreateGuidDialog.call()
    fun showWalletRestoreDialog() = _showWalletRestoreDialog.call()
    fun showWalletCreateAuthActivity() {
        _showWalletCreateAuthActivity.postValue(_mnemonic)
        _mnemonic = null
        _myPubKey = null
    }

    fun restoreWallet(mnemonic: String) {
        runDisposable(walletApi.restoreWallet(mnemonic), {
            _myPubKey = it.pubKey
            _mnemonic = mnemonic
            _isSuccessRestore.postValue(true)
        }) { _isSuccessRestore.postValue(false) }
    }

    private val _showWalletInputPinDialog = SingleLiveEvent<Void>()

    val showWalletInputPinDialog: LiveData<Void> get() = _showWalletInputPinDialog

    fun showWalletInputPinDialog() {
        if (prefUtil.walletMnemonic != null && _myPubKey != null && prefUtil.walletPin != null && prefUtil.walletPassword != null) {
            _showWalletInputPinDialog.call()
        }
    }


    private val _showWalletInfoActivity = MutableLiveData<Map<String, String>>()

    val showWalletInfoActivity: LiveData<Map<String, String>> get() = _showWalletInfoActivity

    fun showWalletInfoActivity() {
        if (prefUtil.walletMnemonic != null && _myPubKey != null && prefUtil.walletPin != null && prefUtil.walletPassword != null) {
            _showWalletInfoActivity.postValue(
                mapOf(
                    "mnemonic" to prefUtil.walletMnemonic!!,
                    "pubKey" to _myPubKey!!
                )
            )
        }
    }

    fun logout() {
        _myPubKey = null
        _mnemonic = null
    }


}