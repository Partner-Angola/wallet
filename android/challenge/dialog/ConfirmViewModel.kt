package com.joeware.android.gpulumera.challenge.dialog

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import org.koin.java.standalone.KoinJavaComponent.inject

class ConfirmViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by inject(CandyPlusAPI::class.java)
    var isLoading = ObservableBoolean(false)

    private val _showToastMessage = MutableLiveData<String>()
    private val _challenge = MutableLiveData<Challenge>()

    val showToastMessage: LiveData<String> get() = _showToastMessage
    val challenge: LiveData<Challenge> get() = _challenge

    fun getChallengeInfo(challengeId: String) {
        isLoading.set(true)

        runDisposable(api.getChallengeInfo(challengeId), { response ->
            isLoading.set(false)
            if (response.success) {
                _challenge.postValue(response.data?.info)
            } else {
                _showToastMessage.postValue(response.error?.error)
            }
        }) { e ->
            isLoading.set(false)
            onError(e)
        }
    }
}