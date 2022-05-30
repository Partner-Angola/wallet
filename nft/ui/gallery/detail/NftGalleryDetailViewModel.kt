package com.joeware.android.gpulumera.nft.ui.gallery.detail

import androidx.lifecycle.LiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.SingleLiveEvent

class NftGalleryDetailViewModel : CandyViewModel() {

    private val _clickChoice = SingleLiveEvent<Void>()

    val clickChoice: LiveData<Void> get() = _clickChoice

    fun clickChoice(){
        _clickChoice.call()
    }

}