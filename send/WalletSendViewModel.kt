package com.joeware.android.gpulumera.reward.ui.wallet.send

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.reward.api.WalletAPI
import com.joeware.android.gpulumera.reward.model.TokenType
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent
import java.lang.Exception
import java.math.BigDecimal

class WalletSendViewModel : CandyViewModel() {

    private val walletApi: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private lateinit var _myPubKey: String
    private lateinit var _myMnemonic: String

    private val _prevPage = SingleLiveEvent<Void>()
    private val _showToastMessage = MutableLiveData<String>()
    private val _showWalletSendChooseTokenFragment = SingleLiveEvent<Void>()
    private val _showWalletSendAmountFragment = SingleLiveEvent<Void>()
    private val _showWalletSendAddressFragment = SingleLiveEvent<Void>()
    private val _showWalletSendConfirmFragment = SingleLiveEvent<Void>()
    private val _showWalletSendSuccessFragment = SingleLiveEvent<Void>()
    private val _showWalletInputPasswordDialog = SingleLiveEvent<Void>()

    val prevPage: LiveData<Void> get() = _prevPage
    val showToastMessage: LiveData<String> get() = _showToastMessage
    val showWalletSendChooseTokenFragment: LiveData<Void> get() = _showWalletSendChooseTokenFragment
    val showWalletSendAmountFragment: LiveData<Void> get() = _showWalletSendAmountFragment
    val showWalletSendAddressFragment: LiveData<Void> get() = _showWalletSendAddressFragment
    val showWalletSendConfirmFragment: LiveData<Void> get() = _showWalletSendConfirmFragment
    val showWalletSendSuccessFragment: LiveData<Void> get() = _showWalletSendSuccessFragment
    val showWalletInputPasswordDialog: LiveData<Void> get() = _showWalletInputPasswordDialog

    fun showWalletSendChooseTokenFragment() = _showWalletSendChooseTokenFragment.call()
    fun showWalletSendAmountFragment() = _showWalletSendAmountFragment.call()
    fun showWalletSendAddressFragment() = _showWalletSendAddressFragment.call()
    fun showWalletSendConfirmFragment() = _showWalletSendConfirmFragment.call()
    fun showWalletSendSuccessFragment() = _showWalletSendSuccessFragment.call()
    fun showWalletInputPasswordDialog() = _showWalletInputPasswordDialog.call()

    fun prevPage() = _prevPage.call()

    /************************************************************************************
     * WalletSendChooseTokenFragment
     ***********************************************************************************/
    private val _myTokenType = MutableLiveData<TokenType>(TokenType.ANGOLA)
    private val _myTokenAngolaAmountStr = MutableLiveData<String>("0")
    private val _myTokenSolanaAmountStr = MutableLiveData<String>("0")

    val myTokenType: LiveData<TokenType> get() = _myTokenType
    val myTokenAngolaAmountStr: LiveData<String> get() = _myTokenAngolaAmountStr
    val myTokenSolanaAmountStr: LiveData<String> get() = _myTokenSolanaAmountStr

    fun onClickChooseToken(type: TokenType) {
        _myTokenType.value = type
        when(type) {
            TokenType.ANGOLA -> {
                _myTokenAmount = (_myTokenAngolaAmountStr.value ?: "0").toBigDecimal()
                _myTokenAmountStr.value = _myTokenAngolaAmountStr.value
            }
            TokenType.SOLANA -> {
                _myTokenAmount = (_myTokenSolanaAmountStr.value ?: "0").toBigDecimal()
                _myTokenAmountStr.value = _myTokenSolanaAmountStr.value
            }
        }
    }

    fun getMyTokenAmount() {
        prefUtil.walletMnemonic?.let { mnemonic ->
            _myMnemonic = mnemonic
            onProgress(true)
            runDisposable(walletApi.restoreWallet(mnemonic), {
                _myPubKey = it.pubKey
                var isLoadAngola = false
                var isLoadSolana = false
                runDisposable(walletApi.tokenGetBalance(pubKey = it.pubKey), { amountInfo ->
                    _myTokenAmount = amountInfo.amount.toBigDecimal()
                    _myTokenAngolaAmountStr.value = amountInfo.amount
                    isLoadAngola = true
                    if (isLoadAngola && isLoadSolana) {
                        onClickChooseToken(_myTokenType.value!!)
                        onProgress(false)
                    }
                }) { onProgress(false) }
                runDisposable(walletApi.solanaGetBalance(pubKey = it.pubKey), { amountInfo ->
                    _myTokenAmount = amountInfo.amount.toBigDecimal()
                    _myTokenSolanaAmountStr.value = amountInfo.amount
                    isLoadSolana = true
                    if (isLoadAngola && isLoadSolana) {
                        onClickChooseToken(_myTokenType.value!!)
                        onProgress(false)
                    }
                }) { onProgress(false) }
            }) { onProgress(false) }
        }
    }

    /************************************************************************************
     * WalletSendAmountFragment
     ***********************************************************************************/
    private var _myTokenAmount = BigDecimal.ZERO
    private var _sendTokenAmount = BigDecimal.ZERO

    private val _myTokenAmountStr = MutableLiveData<String>("0")
    private val _setMaxAmount = SingleLiveEvent<String>()

    val myTokenAmountStr: LiveData<String> get() = _myTokenAmountStr
    val setMaxAmount: LiveData<String> get() = _setMaxAmount

    fun onClickBtnMaxAmount() = _setMaxAmount.postValue(_myTokenAmount.toString())

    fun finishSetAmount(amountStr: String) {
        try {
            val amount = amountStr.toBigDecimal()
            val zero = BigDecimal(0)

            when {
                amount <= zero -> _showToastMessage.postValue("전송 수량을 잘못 입력하였습니다.")
                _myTokenAmount >= amount -> {
                    _sendTokenAmount = amount
                    _showWalletSendAddressFragment.call()
                }
                else -> _showToastMessage.postValue("보유 수량보다 전송 수량이 많습니다.")
            }
        } catch (e: Exception) {
            _showToastMessage.postValue("전송 수량을 잘못 입력하였습니다.")
        }
    }

    /************************************************************************************
     * WalletSendAddressFragment
     ***********************************************************************************/
    private var _sendAddress = ""

    private val _showQRActivity = SingleLiveEvent<Void>()

    val showQRActivity: LiveData<Void> get() = _showQRActivity

    fun showQRActivity() = _showQRActivity.call()

    private val _setSendAddress = MutableLiveData<String>()

    val setSendAddress: LiveData<String> get() = _setSendAddress

    fun setSendAddress(address: String) {
        _setSendAddress.postValue(address)
    }

    fun finishSetAddress(address: String) {
        if (address.isNotEmpty()) {
            _sendAddress = address
            _showWalletInputPasswordDialog.call()
        } else {
            _showToastMessage.postValue("지갑 주소를 입력해주세요.")
        }
    }

    /************************************************************************************
     * WalletSendConfirmFragment
     ***********************************************************************************/
    private val _confirmAddress = MutableLiveData<String>()
    private val _confirmAmount = MutableLiveData<String>()
    private val _confirmFee = MutableLiveData<String>()

    val confirmAddress: LiveData<String> get() = _confirmAddress
    val confirmAmount: LiveData<String> get() = _confirmAmount
    val confirmFee: LiveData<String> get() = _confirmFee

    fun showConfirmData() {
        _confirmAddress.postValue(_sendAddress)
        _confirmAmount.postValue(_sendTokenAmount.toString())
    }

    fun finalSendToken() {
        onProgress(true)
        when (_myTokenType.value) {
            TokenType.ANGOLA -> runDisposable(walletApi.tokenTransfer(mnemonic=_myMnemonic, amount=(_sendTokenAmount), to=_sendAddress), { //.multiply(BigDecimal(1000000000))
                onProgress(false)
                if(it.status) {
                    _showWalletSendSuccessFragment.call()
                } else {
                    _showToastMessage.postValue("전송 실패하였습니다. 다시 시도해주세요.")
                }
            }) {
                onProgress(false)
                _showToastMessage.postValue("전송 실패하였습니다. 다시 시도해주세요.")
                onError(it)
            }
            TokenType.SOLANA -> runDisposable(walletApi.solanaTransfer(mnemonic=_myMnemonic, amount=(_sendTokenAmount), to=_sendAddress), { //.multiply(BigDecimal(1000000000))
                onProgress(false)
                if(it.status) {
                    _showWalletSendSuccessFragment.call()
                } else {
                    _showToastMessage.postValue("전송 실패하였습니다. 다시 시도해주세요.")
                }
            }) {
                onProgress(false)
                _showToastMessage.postValue("전송 실패하였습니다. 다시 시도해주세요.")
                onError(it)
            }
        }
    }



    /************************************************************************************
     * WalletSendSuccessFragment
     ***********************************************************************************/
    private val _finishActivity = SingleLiveEvent<Void>()

    val finishActivity: LiveData<Void> get() = _finishActivity

    fun finishActivity() {
        _finishActivity.call()
    }




}