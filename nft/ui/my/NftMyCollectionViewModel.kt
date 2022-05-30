package com.joeware.android.gpulumera.nft.ui.my

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.nft.model.NftMyCollection
import com.joeware.android.gpulumera.nft.model.NftMyCollectionCamera
import com.joeware.android.gpulumera.nft.model.NftMyCollectionCameraBox
import com.joeware.android.gpulumera.nft.model.NftMyCollectionPhoto
import com.joeware.android.gpulumera.nft.ui.my.NftMyCollectionAdapter.NftMyCollectionType
import com.joeware.android.gpulumera.util.PrefUtil
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class NftMyCollectionViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _collectionType = MutableLiveData<NftMyCollectionType>(NftMyCollectionType.PHOTO)
    private val _currentItems = MutableLiveData<List<NftMyCollection>>()

    val collectionType: LiveData<NftMyCollectionType> get() = _collectionType
    val currentItems: LiveData<List<NftMyCollection>> get() = _currentItems

    private val collectionCameraItems = mutableListOf<NftMyCollectionCamera>()
    private val collectionCameraBoxItems = mutableListOf<NftMyCollectionCameraBox>()
    private val collectionPhotoItems = mutableListOf<NftMyCollectionPhoto>()

    init {
        getMyNft()
//        testInitCamera()
//        testInitCameraBox()
//        testInitPhoto()
//        onClickCameraCollection()
//        onClickPhotoCollection()
    }

    fun getMyNft() {
        prefUtil.walletAddress?.let { address ->
            runDisposable(api.getMyNft(address), {
                it.data?.let { list ->
                    collectionPhotoItems.clear()
                    for (item in list) {
                        collectionPhotoItems.add(
                            NftMyCollectionPhoto(
                                item.metadata.id,
                                item.metadata.image,
                                item.metadata.description,
                                item.metadata.name
                            )
                        )
                    }
                }
                _currentItems.postValue(collectionPhotoItems)
            }) {
                JPLog.e("david", "NFT 조회 에러: $it")
            }
        }

    }

    private fun testInitCamera() {
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00001",
            "https://jp-brothers.synology.me/web_images/img_sample_1.jpg",
            "A",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            true,
            "#F6785A"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00002",
            "https://jp-brothers.synology.me/web_images/img_sample_2.jpg",
            "B",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#D596FF"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00003",
            "https://jp-brothers.synology.me/web_images/img_sample_3.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#D596FF"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00004",
            "https://jp-brothers.synology.me/web_images/img_sample_4.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#5AA5FA"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00005",
            "https://jp-brothers.synology.me/web_images/img_sample_5.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#5AA5FA"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00006",
            "https://jp-brothers.synology.me/web_images/img_sample_6.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#ACD83C"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00007",
            "https://jp-brothers.synology.me/web_images/img_sample_7.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#ACD83C"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00008",
            "https://jp-brothers.synology.me/web_images/img_sample_8.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#C1C9D1"
        ))
        collectionCameraItems.add(NftMyCollectionCamera(
            "#00009",
            "https://jp-brothers.synology.me/web_images/img_sample_9.jpg",
            "C",
            2,
            100,
            3,
            80,
            30,
            30,
            30,
            30,
            30,
            false,
            "#C1C9D1"
        ))
    }

    private fun testInitCameraBox() {
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
        collectionCameraBoxItems.add(
            NftMyCollectionCameraBox(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg"
            ))
    }

    private fun testInitPhoto() {
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
        collectionPhotoItems.add(
            NftMyCollectionPhoto(
                "id",
                "https://s3.ap-northeast-2.amazonaws.com/candyplus.challenge/1011645242628248.jpg", "", ""
            )
        )
    }

    fun onClickCameraCollection() {
        _collectionType.postValue(NftMyCollectionType.CAMERA)
        _currentItems.postValue(collectionCameraItems)
    }

    fun onClickCameraBoxCollection() {
        _collectionType.postValue(NftMyCollectionType.CAMERA_BOX)
        _currentItems.postValue(collectionCameraBoxItems)
    }

    fun onClickPhotoCollection() {
        _collectionType.postValue(NftMyCollectionType.PHOTO)
        _currentItems.postValue(collectionPhotoItems)
    }
}