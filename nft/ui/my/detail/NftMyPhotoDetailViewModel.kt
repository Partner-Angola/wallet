package com.joeware.android.gpulumera.nft.ui.my.detail

import androidx.lifecycle.LiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class NftMyPhotoDetailViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _finishActivity = SingleLiveEvent<Void>()
    private val _clickJoin = SingleLiveEvent<Void>()

    val finishActivity: LiveData<Void> get() = _finishActivity
    val clickJoin: LiveData<Void> get() = _clickJoin


    fun clickJoin(){
        _clickJoin.call()
    }

}