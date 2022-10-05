package com.joeware.android.gpulumera.account.wallet.create

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletPasswordMode
import com.joeware.android.gpulumera.account.wallet.model.WalletPinMode
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.SingleLiveEvent

class WalletPasswordViewModel : CandyViewModel() {

    private var mode = WalletPasswordMode.CREATE_MODE
    private var createPassword: String? = null

    private val _title = MutableLiveData<String>()
    private val _subTitle = MutableLiveData<String>()
    private val _passwordIncorrectTitle = MutableLiveData<String>()
    private val _passwordIncorrect = MutableLiveData(false)
    private val _finishInputPassword = SingleLiveEvent<String>()

    val title: LiveData<String> get() = _title
    val subTitle: LiveData<String> get() = _subTitle
    val passwordIncorrectTitle: LiveData<String> get() = _passwordIncorrectTitle
    val passwordIncorrect: LiveData<Boolean> get() = _passwordIncorrect
    val finishInputPassword: LiveData<String> get() = _finishInputPassword

    fun initWalletPassword(context: Context, mode: WalletPasswordMode, createPassword: String?) {
        this.mode = mode
        this.createPassword = createPassword
        when(mode) {
            WalletPasswordMode.CREATE_MODE -> {
                _title.postValue(context.getString(R.string.password_title_create))
                _subTitle.postValue(context.getString(R.string.password_desc_create))
            }
            WalletPasswordMode.CREATE_CHECK_MODE -> {
                _title.postValue(context.getString(R.string.password_title_confirm))
                _subTitle.postValue(context.getString(R.string.password_desc_confirm))
            }
            WalletPasswordMode.CHECK_INPUT_MODE -> {
                _title.postValue(context.getString(R.string.password_title_input))
                _subTitle.postValue(context.getString(R.string.password_desc_input))
            }
            WalletPasswordMode.RESTORE_INPUT_MODE-> {
                _title.postValue(context.getString(R.string.pin_title_restore_input))
                _subTitle.postValue(context.getString(R.string.pin_desc_input))
            }
            WalletPasswordMode.RESTORE_CREATE_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_restore_create))
                _subTitle.postValue(context.getString(R.string.pin_desc_create))
            }
            WalletPasswordMode.RESTORE_CREATE_CHECK_MODE -> {
                _title.postValue(context.getString(R.string.pin_title_restore_confirm))
                _subTitle.postValue(context.getString(R.string.pin_desc_confirm))
            }

        }
    }

    fun inputPassword(context: Context, password: String) {
        _passwordIncorrect.postValue(false)
        if (password.isEmpty()) {
            visibleIncorrectMessage(context.getString(R.string.password_empty))
            return
        }
        if(createPassword == null) {
            var isLowerCase = false
            var isUpperCase = false
            var isDigit = false
            var isSpecialCase = false
            for (data in password) {
                when {
                    data.isUpperCase() -> isUpperCase = true
                    data.isLowerCase() -> isLowerCase = true
                    data.isDigit() -> isDigit = true
                    else -> isSpecialCase = true
                }
            }
            if (password.length < 10) {
                visibleIncorrectMessage(context.getString(R.string.password_10_less))
            } else if (!isLowerCase || !isUpperCase || !isDigit || !isSpecialCase) {
                visibleIncorrectMessage(context.getString(R.string.password_not_condition))
            } else {
                _finishInputPassword.postValue(password)
            }
        } else {
            if (createPassword == password) {
                _finishInputPassword.postValue(password)
            } else {
                visibleIncorrectMessage(context.getString(R.string.password_not_match))
            }
        }
    }

    private fun visibleIncorrectMessage(message: String) {
        _passwordIncorrectTitle.postValue(message)
        _passwordIncorrect.postValue(true)
        postDelayRX({ _passwordIncorrect.postValue(false) }, 5000)
    }
}