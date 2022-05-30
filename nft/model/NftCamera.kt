package com.joeware.android.gpulumera.nft.model

data class NFTCameraSkinVO(
    val name: String,
    val resName: String,
    val skinRatio: String,
    val resLength: Int,
    val percentWidth: Float,
    val skinColors: ArrayList<String>,
    val backColors: ArrayList<String>
)