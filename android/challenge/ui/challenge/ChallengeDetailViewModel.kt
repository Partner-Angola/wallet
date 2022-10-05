package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class ChallengeDetailViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private val _myItems = MutableLiveData<List<Join>>()
    private val _items = MutableLiveData<List<Join>>()
    private val _finishActivity = SingleLiveEvent<Void>()
    private val _shareChallenge = SingleLiveEvent<Void>()
    private val _showToastMessage = MutableLiveData<String>()
    private val _itemCount = MutableLiveData<Int>()

    val finishActivity: LiveData<Void> get() = _finishActivity
    val shareChallenge: LiveData<Void> get() = _shareChallenge
    val items: LiveData<List<Join>> get() = _items
    val myItems: LiveData<List<Join>> get() = _myItems
    val showToastMessage: LiveData<String> get() = _showToastMessage
    val itemCount: LiveData<Int> get() = _itemCount

    lateinit var challenge: Challenge

    var isLoading = ObservableBoolean(false)
    private var isLoadingEnd: Boolean = false

    fun onClickClose() = _finishActivity.call()
    fun onClickShare() = _shareChallenge.call()

    fun getMyJoinList() {
        runDisposable(
            api.getChallengeJoinList(
                challenge.id,
                0,
                10,
                "mine"
            ),
            { response ->
                if (response.success) {
                    response.data?.list?.let {
                        _myItems.postValue(it)
                    }
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            onError(e)
        }
    }

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
        val offset = _items.value?.size ?: 0
        val excludes = _items.value?.joinToString(separator = ",") { item -> item.id }
        runDisposable(api.getChallengeJoinRandomList(challenge.id, limit, excludes),{ response ->
            isLoading.set(false)
            if (response.success) {
                isLoadingEnd = (response.data?.list?.size ?: 0) < limit
                if (offset == 0) {
                    _itemCount.postValue(response.data?.count)
                }
                response.data?.list?.let {
                    _items.postValue(_items.value.orEmpty() + it)
                }
            } else {
                _showToastMessage.postValue(response.reason)
            }
        }){ e ->
            isLoading.set(false)
            onError(e)
        }
    }

}