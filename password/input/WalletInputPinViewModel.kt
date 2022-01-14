package com.joeware.android.gpulumera.reward.ui.wallet.password.input

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent
import java.util.*

class WalletInputPinViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private var walletPin: String? = prefUtil.walletPin
    private val _successAuth = SingleLiveEvent<Void>()
    private val _failAuth = SingleLiveEvent<Void>()
    private val _prevPage = SingleLiveEvent<Void>()
    private val _myPinData = MutableLiveData<String>()
    private val _pinInconsistency = MutableLiveData<Boolean>(false)
    private val _pinNumPad1RealValue = MutableLiveData("1")
    private val _pinNumPad2RealValue = MutableLiveData("2")
    private val _pinNumPad3RealValue = MutableLiveData("3")
    private val _pinNumPad4RealValue = MutableLiveData("4")
    private val _pinNumPad5RealValue = MutableLiveData("5")
    private val _pinNumPad6RealValue = MutableLiveData("6")
    private val _pinNumPad7RealValue = MutableLiveData("7")
    private val _pinNumPad8RealValue = MutableLiveData("8")
    private val _pinNumPad9RealValue = MutableLiveData("9")
    private val _pinNumPad0RealValue = MutableLiveData("0")

    val successAuth: LiveData<Void> get() = _successAuth
    val failAuth: LiveData<Void> get() = _failAuth
    val prevPage: LiveData<Void> get() = _prevPage
    val myPinData: LiveData<String> get() = _myPinData
    val pinInconsistency: LiveData<Boolean> get() = _pinInconsistency
    val pinNumPad1RealValue: LiveData<String> get() = _pinNumPad1RealValue
    val pinNumPad2RealValue: LiveData<String> get() = _pinNumPad2RealValue
    val pinNumPad3RealValue: LiveData<String> get() = _pinNumPad3RealValue
    val pinNumPad4RealValue: LiveData<String> get() = _pinNumPad4RealValue
    val pinNumPad5RealValue: LiveData<String> get() = _pinNumPad5RealValue
    val pinNumPad6RealValue: LiveData<String> get() = _pinNumPad6RealValue
    val pinNumPad7RealValue: LiveData<String> get() = _pinNumPad7RealValue
    val pinNumPad8RealValue: LiveData<String> get() = _pinNumPad8RealValue
    val pinNumPad9RealValue: LiveData<String> get() = _pinNumPad9RealValue
    val pinNumPad0RealValue: LiveData<String> get() = _pinNumPad0RealValue

    fun prevPage() = _prevPage.call()
    fun onClickNumPad1() = setPinData(_pinNumPad1RealValue.value ?: "1")
    fun onClickNumPad2() = setPinData(_pinNumPad2RealValue.value ?: "2")
    fun onClickNumPad3() = setPinData(_pinNumPad3RealValue.value ?: "3")
    fun onClickNumPad4() = setPinData(_pinNumPad4RealValue.value ?: "4")
    fun onClickNumPad5() = setPinData(_pinNumPad5RealValue.value ?: "5")
    fun onClickNumPad6() = setPinData(_pinNumPad6RealValue.value ?: "6")
    fun onClickNumPad7() = setPinData(_pinNumPad7RealValue.value ?: "7")
    fun onClickNumPad8() = setPinData(_pinNumPad8RealValue.value ?: "8")
    fun onClickNumPad9() = setPinData(_pinNumPad9RealValue.value ?: "9")
    fun onClickNumPad0() = setPinData(_pinNumPad0RealValue.value ?: "0")

    fun shufflePinNumber() {
        var keyNumberArr = mutableListOf<String>().apply { for (i in 0..9) add(i.toString()) }
        val random = Random()
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad0RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad1RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad2RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad3RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad4RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad5RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad6RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad7RealValue)
        keyNumberArr = setPinNumber(keyNumberArr, random, _pinNumPad8RealValue)
        setPinNumber(keyNumberArr, random, _pinNumPad9RealValue)
    }

    private fun setPinNumber(numList: MutableList<String>, random: Random, pin: MutableLiveData<String>): MutableList<String> {
        val randomValue = numList[random.nextInt(numList.size)]
        pin.postValue(randomValue)
        numList.forEachIndexed { index, s -> if (s == randomValue) {
            numList.removeAt(index)
            return numList
        } }
        return mutableListOf()
    }

    fun onClickNumPadDel() {
        var pin = _myPinData.value ?: ""
        if (pin.isNotEmpty()) {
            pin = pin.substring(0, pin.length - 1)
            _myPinData.postValue(pin)
        }
    }

    private fun setPinData(num: String) {
        var pin = _myPinData.value ?: ""
        if (pin.length < 6) {
            pin += num
            _myPinData.postValue(pin)

            if (pin.length == 6) {
                if (pin == walletPin) {
                    _successAuth.call()
                } else {
                    _failAuth.call()
                }
            }
        }
    }

    /************************************************************************************
     * WalletCreateBioFragment
     ***********************************************************************************/
    private val _failAuthBio = SingleLiveEvent<Void>()

    val failAuthBio: LiveData<Void> get() = _failAuthBio

    private var biometricPrompt: BiometricPrompt? = null

    fun checkBio(context: Context, fragment: Fragment) {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> { //  생체 인증 가능
                if (prefUtil.walletBio) {
                    biometricPrompt = setBiometricPrompt(context, fragment)
                    useBio()
                }
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {} //  기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {} //  기기에서 생체 인증을 활성화 안되어있는 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {} //  생체 인식 정보가 등록되지 않은 경우
            else -> {}
        }

    }

    private fun setBiometricPrompt(context: Context, fragment: Fragment) : BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(context)
        return BiometricPrompt(fragment, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                JPLog.i("호일", "인증 에러 $errString")
                _failAuthBio.call()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                JPLog.i("호일", "인증 성공")
                _successAuth.call()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                _failAuthBio.call()
                JPLog.i("호일", "인증 실패")
            }
        })
    }

    private fun useBio() {
        biometricPrompt?.authenticate(
            BiometricPrompt.PromptInfo.Builder()
            .setTitle("생체인식 인증")
            .setSubtitle("지문을 이용 로그인 합니다.")
            .setNegativeButtonText("취소")
            .build()
        )
    }

}