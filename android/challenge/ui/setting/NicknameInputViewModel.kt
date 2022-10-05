package com.joeware.android.gpulumera.challenge.ui.setting

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.PhotoUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.koin.java.standalone.KoinJavaComponent
import retrofit2.adapter.rxjava2.HttpException
import java.io.File

class NicknameInputViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    var isLoading = ObservableBoolean(false)
    var nickname = MutableLiveData<String>().apply { value = C.me.nickname }

    private val _saveSuccess = SingleLiveEvent<Void>()
    private val _showToastMessage = MutableLiveData<String>()

    val saveSuccess: LiveData<Void> get() = _saveSuccess
    val showToastMessage: LiveData<String> get() = _showToastMessage

    fun saveUser(id: String, nickname: String, intro: String, point: Int, profile: File? = null) {
        isLoading.set(true)
        var uploadFile: MultipartBody.Part? = null
        if (profile != null) {
            var file = File(profile.absolutePath)

            PhotoUtil.resizeImage(file)?.let {file = it}

            val requestBody = RequestBody.create(MediaType.get("image/*"), file)
            uploadFile = MultipartBody.Part.createFormData("image", file.name, requestBody)
        }
        runDisposable(api.editAccount(
            RequestBody.create(MultipartBody.FORM, id),
            RequestBody.create(MultipartBody.FORM, nickname),
            RequestBody.create(MultipartBody.FORM, intro),
            RequestBody.create(MultipartBody.FORM, "$point"),
            uploadFile
        ), { response ->
            isLoading.set(false)
            if (response.success) {
                C.me.setMyInfo(response.data as User)
                _saveSuccess.call()
            } else {
                _showToastMessage.postValue(response.error?.error)
            }
        }) { e ->
            isLoading.set(false)
            if ((e as HttpException).code() == 400) {
                // nickname duplicate
                _showToastMessage.postValue(StringUtils.getString(R.string.nickname_duplicate))
            } else {
                onError(e)
            }
        }
    }

}