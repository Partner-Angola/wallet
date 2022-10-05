package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.common.C
import org.koin.java.standalone.KoinJavaComponent

class PrizeDetailViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private val _items = MutableLiveData<List<Join>>()
    private val _showToastMessage = MutableLiveData<String>()

    val items: LiveData<List<Join>> get() = _items
    val showToastMessage: LiveData<String> get() = _showToastMessage

    var isLoading = ObservableBoolean(false)
    private var isLoadingEnd: Boolean = false

    lateinit var challenge: Challenge

    fun setList(list: List<Join>) {
        _items.postValue(list)
    }

    fun getJoinList(cb: () -> Unit) {
        if (isLoadingEnd || isLoading.get()) {
            return
        }

        isLoading.set(true)
        val limit = C.LIMIT_30
        runDisposable(
            api.getChallengeJoinList(
                challenge.id,
                _items.value?.size ?: 0,
                limit,
                ""
            ),
            { response ->
                isLoading.set(false)
                if (response.success) {
                    isLoadingEnd = (response.data?.list?.size ?: 0) < limit
                    response.data?.list?.let {
                        _items.postValue(_items.value.orEmpty() + it)
                        cb()
                    }
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoading.set(false)
            onError(e)
        }
    }
}