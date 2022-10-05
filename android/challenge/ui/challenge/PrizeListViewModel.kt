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

class PrizeListViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private val _voteItems = MutableLiveData<List<Join>>()
    private val _itemCount = MutableLiveData<Int>()
    private val _showToastMessage = MutableLiveData<String>()

    val voteItems: LiveData<List<Join>> get() = _voteItems
    val itemCount: LiveData<Int> get() = _itemCount
    val showToastMessage: LiveData<String> get() = _showToastMessage

    lateinit var challenge: Challenge

    var isLoading = ObservableBoolean(false)
    private var isLoadingEnd: Boolean = false

    fun initJoinList() {
        _voteItems.value = listOf()
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
        val offset = _voteItems.value?.size ?: 0

        runDisposable(
            api.getChallengeJoinList(
                challenge.id,
                offset,
                limit,
                ""
            ),
            { response ->
                isLoading.set(false)
                if (response.success) {
                    isLoadingEnd = (response.data?.list?.size ?: 0) < limit
                    if (offset == 0) {
                        _itemCount.postValue(response.data?.count)
                    }
                    response.data?.list?.let {
                        _voteItems.postValue(_voteItems.value.orEmpty() + it)
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