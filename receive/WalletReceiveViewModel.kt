package com.joeware.android.gpulumera.reward.ui.wallet.receive

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.journeyapps.barcodescanner.BarcodeEncoder

class WalletReceiveViewModel : CandyViewModel() {

    private val _createQrBitmap = MutableLiveData<Bitmap>()
    private val _pubKey = MutableLiveData<String>()
    private val _copyPubKey = MutableLiveData<String>()
    private val _clickBack = SingleLiveEvent<Void>()

    val createQrBitmap: LiveData<Bitmap> get() = _createQrBitmap
    val pubKey: LiveData<String> get() = _pubKey
    val copyPubKey: LiveData<String> get() = _copyPubKey
    val clickBack: LiveData<Void> get() = _clickBack

    fun clickBack() = _clickBack.call()

    fun createQRCode(mnemonic: String, pubKey: String) {
        _pubKey.postValue(pubKey)
        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(pubKey, BarcodeFormat.QR_CODE, 300, 300)
            val barcodeEncoder = BarcodeEncoder()
            val qr = barcodeEncoder.createBitmap(bitMatrix)
            _createQrBitmap.postValue(qr)
        } catch (e: Exception) {

        }
    }

    fun copyPubKey(){
        _pubKey.value?.let { _copyPubKey.postValue(it) }
    }
}