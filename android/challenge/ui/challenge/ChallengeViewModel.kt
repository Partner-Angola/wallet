package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.joeware.android.gpulumera.util.safeLet
import org.koin.java.standalone.KoinJavaComponent

class ChallengeViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _refreshLogin = SingleLiveEvent<Void>()
    private val _finishActivity = SingleLiveEvent<Void>()
    private val _refreshEnd = SingleLiveEvent<Void>()
    private val _voteItems = MutableLiveData<List<Challenge>>()
    private val _participateItems = MutableLiveData<List<Challenge>>()
    private val _endItems = MutableLiveData<List<Challenge>>()
    private val _showToastMessage = MutableLiveData<String>()

    val refreshLogin: LiveData<Void> get() = _refreshLogin
    val finishActivity: LiveData<Void> get() = _finishActivity
    val refreshEnd: LiveData<Void> get() = _refreshEnd
    val voteItems: LiveData<List<Challenge>> get() = _voteItems
    val participateItems: LiveData<List<Challenge>> get() = _participateItems
    val endItems: LiveData<List<Challenge>> get() = _endItems
    val showToastMessage: LiveData<String> get() = _showToastMessage
    val isLoading = ObservableBoolean(false)

    private var isVoteLoadingEnd: Boolean = false
    private var isLoadingEnd: Boolean = false
    private var isLoadingVote: Boolean = false

    fun onClickClose() = _finishActivity.call()

    init {
        if (C.IS_CANDY_POINT_API_SERVER_LOGIN) {
            safeLet(C.FCM_TOKEN, C.LOCALE_LANG) { token, country -> runDisposable(api.updateUserVolatileInformation(token, country)) {} }
        }
    }

    fun getUserInfo(userId: String) {
        runDisposable(api.getUserInfo(userId), { response ->
            if (response.success) {
                C.me.setMyInfo(response.data as User)
                _refreshLogin.call()
            }
        }) { e -> onError(e) }
    }

    fun initVoteChallengeList() {
//        _voteItems.value = listOf()
        isVoteLoadingEnd = false

        getVoteChallengeList(true)
    }

    fun getVoteChallengeList(refresh: Boolean = false) {
        if (isVoteLoadingEnd || isLoadingVote) {
            return
        }

        isLoadingVote = true
        val limit = C.LIMIT_10
        runDisposable(
            api.getChallengeList(
                if (refresh) 0 else _voteItems.value?.size ?: 0,
                limit,
                "vote",
                C.ChallengeStatus.active.toString()
            ),
            { response ->
                isLoadingVote = false
                if (response.success) {
                    isVoteLoadingEnd = response.data?.list?.size ?: 0 < limit
                    response.data?.list?.let {
                        _voteItems.postValue(if (refresh) it else _voteItems.value.orEmpty() + it)
                    }
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoadingVote = false
            onError(e)
        }
    }

    fun initProgressChallengeList() {
        _participateItems.value = listOf()
        isLoading.set(false)
        isLoadingEnd = false

        getProgressChallengeList()
    }

    fun getProgressChallengeList() {
        if (isLoadingEnd || isLoading.get()) {
            return
        }

        isLoading.set(true)
        val limit = C.LIMIT_30
        runDisposable(
            api.getChallengeList(
                _participateItems.value?.size ?: 0,
                limit,
                "join",
                C.ChallengeStatus.active.toString()
            ),
            { response ->
                isLoading.set(false)
                _refreshEnd.call()
                if (response.success) {
                    isLoadingEnd = response.data?.list?.size ?: 0 < limit
                    response.data?.list?.let {
                        _participateItems.postValue(_participateItems.value.orEmpty() + it)
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

    fun initEndChallengeList() {
        _endItems.value = listOf()
        isLoading.set(false)
        isLoadingEnd = false

        getEndChallengeList()
    }

    fun getEndChallengeList() {
        if (isLoadingEnd || isLoading.get()) {
            return
        }

        isLoading.set(true)
        val limit = C.LIMIT_30
        runDisposable(
            api.getChallengeList(
                _endItems.value?.size ?: 0,
                limit,
                "",
                C.ChallengeStatus.inactive.toString()
            ),
            { response ->
                isLoading.set(false)
                _refreshEnd.call()
                if (response.success) {
                    isLoadingEnd = response.data?.list?.size ?: 0 < limit
                    response.data?.list?.let {
                        _endItems.postValue(_endItems.value.orEmpty() + it)
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

    fun login() {
        FirebaseAuth.getInstance().currentUser?.let { user ->
            runDisposable(api.getUserInfo(user.uid), { response ->
                if (response.success && response.data != null) {
                    C.IS_CANDY_POINT_API_SERVER_LOGIN = true
                    C.me.setMyInfo(response.data)
                    _refreshLogin.call()
                } else {
                    C.IS_CANDY_POINT_API_SERVER_LOGIN = false
                }
            }){
                C.IS_CANDY_POINT_API_SERVER_LOGIN = false
            }
        }
    }
}