package com.joeware.android.gpulumera.nft.ui.my

import androidx.lifecycle.LiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class NftMyViewModel : CandyViewModel() {

    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _callResume = SingleLiveEvent<Void>()
    private val _refreshLogin = SingleLiveEvent<Void>()

    val callResume: LiveData<Void> get() = _callResume
    val refreshLogin: LiveData<Void> get() = _refreshLogin

    fun callResume() {
        _callResume.call()
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