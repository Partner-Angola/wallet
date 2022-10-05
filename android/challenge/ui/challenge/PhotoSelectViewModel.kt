package com.joeware.android.gpulumera.challenge.ui.challenge

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.challenge.widget.gallery.Gallery
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.joeware.android.gpulumera.util.PrefUtil
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class PhotoSelectViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _items = MutableLiveData<List<Gallery>>()

    val items: LiveData<List<Gallery>> get() = _items

    fun getMyNft() {
        prefUtil.walletAddress?.let { address ->
//            runDisposable(api.getMyNft(address), {
//                it.data?.let { list ->
//                    val collectionPhotoItems = mutableListOf<Gallery>()
//                    for (item in list) {
//                        collectionPhotoItems.add(
//                            Gallery(
//                                0,
//                                item.metadata.name,
//                                item.metadata.image
//                            )
//                        )
//                    }
//                    _items.postValue(collectionPhotoItems)
//                }
//            }) {
//                JPLog.e("david", "NFT 조회 에러: $it")
//            }
        }
    }

}