package com.joeware.android.gpulumera.challenge.ui.vote

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent
import java.util.*

class VoteViewModel : CandyViewModel() {
    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private val _voteItems = MutableLiveData<List<Challenge>>()
    private val _showToastMessage = MutableLiveData<String>()
    private val _refreshEnd = SingleLiveEvent<Void>()

    val voteItems: LiveData<List<Challenge>> get() = _voteItems
    val showToastMessage: LiveData<String> get() = _showToastMessage
    val refreshEnd: LiveData<Void> get() = _refreshEnd

    var isLoading = ObservableBoolean(false)
    private var isLoadingEnd: Boolean = false

    fun initVoteList() {
        _voteItems.value = listOf()
        isLoading.set(false)
        isLoadingEnd = false

        getVoteList()
    }

    fun getVoteList() {
        if (isLoadingEnd || isLoading.get()) {
            return
        }

        isLoading.set(true)
        val limit = C.LIMIT_10
        runDisposable(
            api.getChallengeList(
                _voteItems.value?.size ?: 0,
                limit,
                "vote",
                C.ChallengeStatus.active.toString()
            ),
            { response ->
                isLoading.set(false)
                _refreshEnd.call()
                if (response.success) {
                    isLoadingEnd = response.data?.list?.size ?: 0 < limit
                    response.data?.list?.let {
                        _voteItems.postValue(_voteItems.value.orEmpty() + it)
                    }
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoading.set(false)
            _refreshEnd.call()
            onError(e)
        }
    }
}