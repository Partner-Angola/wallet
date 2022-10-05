package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.util.PhotoUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.java.standalone.KoinJavaComponent
import java.io.File

class ChallengeParticipateViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    lateinit var challenge: Challenge
    lateinit var filePath: String

    private val _showToastMessage = MutableLiveData<String>()
    private val _joinSuccess = SingleLiveEvent<Void>()
    private val _isLoading = MutableLiveData<Boolean>(false)

    val joinSuccess: LiveData<Void> get() = _joinSuccess
    val showToastMessage: LiveData<String> get() = _showToastMessage

    val isLoading: LiveData<Boolean> get() = _isLoading

    fun participate() {
        JPLog.e("david participate")
        if (filePath.isEmpty() || isLoading.value == true) {
            return
        }
        _isLoading.value = true


        var file = File(filePath)

        PhotoUtil.resizeImage(file)?.let {file = it}

        val requestBody = RequestBody.create(MediaType.get("image/*"), file)
        val uploadFile = MultipartBody.Part.createFormData("image", file.name, requestBody)


        runDisposable(api.uploadChallengePhoto(challenge.id, uploadFile), {
            it.data?.get("image")?.let { url ->
                runDisposable(
                    api.joinChallenge(
                        challenge.id,
                        url
                    ),
                    { response ->
                        if (response.success) {
                            _joinSuccess.call()
                        } else {
                            _isLoading.value = false
                            _showToastMessage.postValue(response.reason)
                        }
                    }) { e ->
                    _isLoading.value = false
                    ToastUtils.showShort(R.string.join_expired)
                    onError(e)
                }
            } ?: run {
                _isLoading.value = false
                ToastUtils.showShort(R.string.join_expired)
            }
        }){ e ->
            _isLoading.value = false
            ToastUtils.showShort(R.string.join_expired)
            onError(e)
        }
    }

}