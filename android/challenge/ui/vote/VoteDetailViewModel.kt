package com.joeware.android.gpulumera.challenge.ui.vote

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.PrefUtil
import org.koin.java.standalone.KoinJavaComponent

class VoteDetailViewModel : CandyViewModel() {
    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

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

    fun getUserInfo(userId: String) {
        runDisposable(api.getUserInfo(userId), { response ->
            if (response.success) {
                C.me.setMyInfo(response.data as User)
            }
        }) { e -> onError(e) }
    }

    fun getJoinList() {
        if (isLoadingEnd || isLoading.get()) {
            return
        }

        isLoading.set(true)
        val limit = C.LIMIT_30
        val excludes = _items.value?.joinToString(separator = ",") { item -> item.id }
        runDisposable(api.getChallengeJoinRandomList(challenge.id, limit, excludes),{ response ->
            isLoading.set(false)
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
            onError(e)
        }
    }

    fun vote(joinId: String, ownerUid: String, cb: () -> Unit) {
        isLoading.set(true)
        runDisposable(
            api.voteChallengeJoin(challenge.id, joinId, ownerUid),
            { response ->
                isLoading.set(false)
                if (response.success) {
                    cb()
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoading.set(false)
            ToastUtils.showShort(R.string.vote_expired)
            onError(e)
        }
    }

    fun cancelVote(joinId: String, cb: () -> Unit) {
        isLoading.set(true)
        runDisposable(
            api.unVoteChallengeJoin(challenge.id, joinId),
            { response ->
                isLoading.set(false)
                if (response.success) {
                    cb()
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoading.set(false)
            onError(e)
        }
    }

    fun reportJoin(joinId: String, content: String) {
        isLoading.set(true)
        runDisposable(
            api.reportChallengeJoin(challenge.id, joinId, content),
            { response ->
                isLoading.set(false)
                if (response.success) {
                    _showToastMessage.postValue(StringUtils.getString(R.string.report_success))
                } else {
                    _showToastMessage.postValue(response.reason)
                }
            }) { e ->
            isLoading.set(false)
            onError(e)
        }
    }
}