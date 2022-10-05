package com.joeware.android.gpulumera.challenge.ui.vote

import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join

/**
 * VoteListActivity <-> VoteDetailActivity 간 데이터 이동 용 Singleton Util
 */
class VoteListDataUtil {

    private var _voteBtnShow = true
    private var _selectPosition = 0
    private var _selectChallenge: Challenge? = null
    private var _selectChallengeList: ArrayList<Join>? = null
    private var _requestVoteDetailActivity: ArrayList<Join>? = null

    val voteBtnShow: Boolean get() = _voteBtnShow
    val selectPosition: Int get() = _selectPosition
    val selectChallenge: Challenge? get() = _selectChallenge
    val selectChallengeList: ArrayList<Join>? get() = _selectChallengeList
    val requestVoteDetailActivity: ArrayList<Join>? get() = _requestVoteDetailActivity

    fun setChallenge(voteBtnShow: Boolean, position: Int, challenge: Challenge, items: ArrayList<Join>) {
        _voteBtnShow = voteBtnShow
        _selectPosition = position
        _selectChallenge = challenge
        _selectChallengeList = items
    }

    fun setRequestVoteDetailActivity(items: ArrayList<Join>?) {
        this._requestVoteDetailActivity = items
    }

    fun clear() {
        _voteBtnShow = true
        _selectPosition = 0
        _selectChallenge = null
        _selectChallengeList = null
    }

    fun clearRequestVoteDetailActivity() {
        _requestVoteDetailActivity = null
    }



}