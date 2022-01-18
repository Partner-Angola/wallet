package com.joeware.android.gpulumera.reward.ui.wallet.transaction

import android.graphics.Bitmap
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.reward.api.WalletAPI
import com.joeware.android.gpulumera.reward.model.TokenType
import com.joeware.android.gpulumera.reward.model.WalletHistory
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class WalletTransactionViewModel : CandyViewModel() {



    private val walletApi: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)



//    private val walletApi: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)
//
//    private val _getHistory = MutableLiveData<List<WalletHistory>>()
//    private val _clickRefresh = SingleLiveEvent<Void>()
//    private val _clickBack = SingleLiveEvent<Void>()
//    private val _pubKey = MutableLiveData<String>()
//    private val _angolaAmount = MutableLiveData<String>("0")
//    private val _visibilityProgress = MutableLiveData<Int>()
//
//    val getHistory: MutableLiveData<List<WalletHistory>> get() = _getHistory
//    val clickRefresh : SingleLiveEvent<Void> get() = _clickRefresh
//    val clickBack : SingleLiveEvent<Void> get() = _clickBack
//    val pubKey: LiveData<String> get() = _pubKey
//    val angolaAmount : LiveData<String> get() = _angolaAmount
//    val visibilityProgress : LiveData<Int> get() = _visibilityProgress
//
//    fun getHistory(pubKey: String) {
//        _pubKey.postValue(pubKey)
//        _visibilityProgress.postValue(View.VISIBLE)
//        runDisposable(walletApi.getTokenHistory(myPubKey = pubKey), {
//            _getHistory.postValue(it)
//            _visibilityProgress.postValue(View.INVISIBLE)
//            JPLog.i("호일", "성공: $it")
//        }) {
//            JPLog.i("호일", "잘못된 요청 $it")
//            _visibilityProgress.postValue(View.INVISIBLE)
//        }
//    }
//
//    fun clickRefresh() = _clickRefresh.call()
//    fun clickBack() = _clickBack.call()
//    fun setAngolaAmount(angolaAmount : String) = _angolaAmount.postValue(angolaAmount)


    /************************************************************************************
     * WalletTransactionFragment
     ***********************************************************************************/

    var type: TokenType = TokenType.ANGOLA
    var pubKey: String? = null
    var amount: String = "0"

    private val _items = MutableLiveData<Pair<String, List<WalletHistory>>>()   // 수량, 거래내역
    private val _emptyData = MutableLiveData<Boolean>()

    val items: LiveData<Pair<String, List<WalletHistory>>> get() = _items
    val emptyData: LiveData<Boolean> = _emptyData

    fun getHistory(type: TokenType, pubKey: String?, amount : String) {
        this.type = type
        this.pubKey = pubKey
        this.amount = amount
        if (pubKey == null) { _emptyData.postValue(true); return }
        onProgress(true)
        if (type == TokenType.ANGOLA) {
            runDisposable(walletApi.getTokenHistory(myPubKey = pubKey), {
                onProgress(false)
                _items.postValue(Pair(amount, it.result))
                _emptyData.postValue(it.result.isEmpty())
            }) {
                onProgress(false)
                _emptyData.postValue(true)
            }
        } else if (type == TokenType.SOLANA) {
            runDisposable(walletApi.getSolanaHistory(myPubKey = pubKey), {
                onProgress(false)
                _items.postValue(Pair(amount, it.result))
                _emptyData.postValue(it.result.isEmpty())
            }) {
                onProgress(false)
                _emptyData.postValue(true)
            }
        }
    }

    fun clickRefresh() = getHistory(this.type, this.pubKey, this.amount)
}