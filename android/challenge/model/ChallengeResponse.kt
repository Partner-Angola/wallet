package com.joeware.android.gpulumera.challenge.model

import com.google.gson.annotations.SerializedName

data class ChallengeList(
    @SerializedName("count")
    val count: Int,
    @SerializedName("challenge_list")
    val list: List<Challenge>
)

data class ChallengeJoinList(
    @SerializedName("join_list")
    val list: List<Join>,
    @SerializedName("count")
    val count: Int
)

data class ChallengeInfo(
    @SerializedName("challenge")
    val info: Challenge
)