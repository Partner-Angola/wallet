package com.joeware.android.gpulumera.challenge.ui.vote

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class VoteListViewModel : CandyViewModel() {
    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private val _items = MutableLiveData<List<Join>>()
    private val _showToastMessage = MutableLiveData<String>()
    private val _refreshEnd = SingleLiveEvent<Void>()

    val items: LiveData<List<Join>> get() = _items
    val showToastMessage: LiveData<String> get() = _showToastMessage
    val refreshEnd: LiveData<Void> get() = _refreshEnd

    var isLoading = ObservableBoolean(false)
    private var isLoadingEnd: Boolean = false

    lateinit var challenge: Challenge

    fun setItems(list: List<Join>) = _items.postValue(list)

    fun initJoinList() {
        _items.value = listOf()
        isLoading.set(false)
        isLoadingEnd = false

        getJoinList()
    }

    fun getJoinList() {
        if (isLoadingEnd || isLoading.get()) {
            return
        }
        isLoading.set(true)
        val limit = C.LIMIT_60
        val excludes = _items.value?.joinToString(separator = ",") { item -> item.id }
        runDisposable(api.getChallengeJoinRandomList(challenge.id, limit, excludes),{ response ->
            isLoading.set(false)
            _refreshEnd.call()
            if (response.success) {
                isLoadingEnd = (response.data?.list?.size ?: 0) < limit
                response.data?.list?.let {
                    _items.postValue(_items.value.orEmpty() + it)
                }
            } else {
                _showToastMessage.postValue(response.reason)
            }
        }){ e ->
            isLoading.set(false)
            _refreshEnd.call()
            onError(e)
        }








    }
}