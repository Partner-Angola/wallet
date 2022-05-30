package com.joeware.android.gpulumera.nft.ui.mint

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.nft.model.GalleryItem
import com.joeware.android.gpulumera.nft.model.NftMint
import com.joeware.android.gpulumera.util.PhotoUtil
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.java.standalone.KoinJavaComponent
import java.io.File

class NftMintingViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)
    private val _isCheckedAll = MutableLiveData(false)
    private val _isChecked0 = MutableLiveData(false)
    private val _isChecked1 = MutableLiveData(false)
    private val _isChecked2 = MutableLiveData(false)
    private val _isChecked3 = MutableLiveData(false)
    private val _enableMintNft = MutableLiveData(false)
    private val _emoji = MutableLiveData("")
    private val _completeMinting = MutableLiveData<NftMint>()
    private val _startMinting = SingleLiveEvent<Void>()
    private val _visibleDelete = MutableLiveData<Int>()
    private val _nftID = MutableLiveData("#00000001")

    val isCheckedAll: LiveData<Boolean> get() = _isCheckedAll
    val isChecked0: LiveData<Boolean> get() = _isChecked0
    val isChecked1: LiveData<Boolean> get() = _isChecked1
    val isChecked2: LiveData<Boolean> get() = _isChecked2
    val isChecked3: LiveData<Boolean> get() = _isChecked3
    val enableMintNft: LiveData<Boolean> get() = _enableMintNft
    val emoji: LiveData<String> get() = _emoji
    val completeMinting: LiveData<NftMint> get() = _completeMinting
    val startMinting: LiveData<Void> get() = _startMinting
    val visibleDelete: LiveData<Int> get() = _visibleDelete
    val nftID: LiveData<String> get() = _nftID

    private var emojiLength = 0
    var itemList: ArrayList<GalleryItem> = arrayListOf()

    fun inputEmoji(emoji: String) {
        if (emojiLength < 5) {
            _emoji.value = "${_emoji.value ?: ""}$emoji"
            emojiLength += 1
        }
        if(emojiLength == 0){
            _visibleDelete.postValue(View.INVISIBLE)
        }else{
            _visibleDelete.postValue(View.VISIBLE)
        }
        setEnableMintNft()
    }

    fun deleteAllEmoji() {
        _emoji.value = ""
        emojiLength = 0

        if(emojiLength == 0){
            _visibleDelete.postValue(View.INVISIBLE)
        }else{
            _visibleDelete.postValue(View.VISIBLE)
        }
        setEnableMintNft()
    }

    fun onClickCheckBox(position: Int) {
        when(position) {
            0 -> {
                val toggleCheck = !(_isCheckedAll.value ?: false)
                _isChecked0.value = toggleCheck
                _isChecked1.value = toggleCheck
                _isChecked2.value = toggleCheck
                _isChecked3.value = toggleCheck
            }
            1 -> _isChecked0.value = !(_isChecked0.value ?: false)
            2 -> _isChecked1.value = !(_isChecked1.value ?: false)
            3 -> _isChecked2.value = !(_isChecked2.value ?: false)
            4 -> _isChecked3.value = !(_isChecked3.value ?: false)
        }
        _isCheckedAll.value = _isChecked0.value ?: false &&_isChecked1.value ?: false && _isChecked2.value ?: false && _isChecked3.value ?: false
        setEnableMintNft()
    }

    private fun setEnableMintNft() {
        if (_emoji.value.isNullOrEmpty() || _isCheckedAll.value == false) {
            _enableMintNft.postValue(false)
        } else {
            _enableMintNft.postValue(true)
        }
    }

    fun onClickMintingNft() {
        if (_enableMintNft.value == true) {
            _startMinting.call()
            mintServerNFT()
        }
    }

    private fun mintServerNFT() {
        runDisposable(api.getNftNo(), {
            if (it.success && it.data != null) {
                _nftID.value = convertHashNumber(it.data.no.toInt())
                prefUtil.walletAddress?.let { address ->
                    var file = File(itemList[0].path)
                    PhotoUtil.resizeImage(file)?.let { result -> file = result }
                    PhotoUtil.cropImage(file)?.let { result -> file = result }
                    val requestBody = RequestBody.create(MediaType.get("image/*"), file)
                    val uploadFile = MultipartBody.Part.createFormData("file", file.name, requestBody)
                    runDisposable(api.mintNft(address, _nftID.value!!, _emoji.value!!, uploadFile), { response ->
                            if(response.success)
                                _completeMinting.postValue(response.data)
                        })
                    { error -> JPLog.e("david server 2 {$error}");ToastUtils.showLong(error.localizedMessage)}
                }
            } else {

            }

        }) { error -> JPLog.e("david server {$error}") }
    }

    private fun convertHashNumber(num: Int): String {
        var retStr = "#$num"
        if (num <= 99999999) {
            when (num) {
                in 1000000..9999999 -> retStr = "#0$num"
                in 100000..999999 -> retStr = "#00$num"
                in 10000..99999 -> retStr = "#000$num"
                in 1000..9999 -> retStr = "#0000$num"
                in 100..999 -> retStr = "#00000$num"
                in 10..99 -> retStr = "#000000$num"
                in 0..9 -> retStr = "#0000000$num"
            }
        }
        return retStr
    }

    private val _challengeList = MutableLiveData<List<Challenge>>()

    val challengeList: LiveData<List<Challenge>> get() = _challengeList

    fun getChallengeList() {
        runDisposable(api.getChallengeList(0, 10, "join", C.ChallengeStatus.active.toString()), { response ->
            if (response.success) {
                response.data?.let { _challengeList.postValue(it.list) }
            }
        }) { e -> onError(e) }
    }

}