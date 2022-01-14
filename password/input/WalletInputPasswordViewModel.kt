package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent

class WalletInputPasswordViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _dismissDialog = SingleLiveEvent<Void>()
    private val _successCheckPassword = SingleLiveEvent<Void>()
    private val _passwordInconsistency = MutableLiveData<Boolean>(false)

    val dismissDialog: LiveData<Void> get() = _dismissDialog
    val successCheckPassword: LiveData<Void> get() = _successCheckPassword
    val passwordInconsistency: LiveData<Boolean> get() = _passwordInconsistency

    fun prevPage() = _dismissDialog.call()

    fun checkPassword(password: String) {
        if (password == prefUtil.walletPassword) {
            _successCheckPassword.postValue(null)
            _dismissDialog.call()
        } else {
            _passwordInconsistency.postValue(true)
        }
    }
}