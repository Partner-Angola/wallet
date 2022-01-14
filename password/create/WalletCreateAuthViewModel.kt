package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.reward.api.WalletAPI
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent
import java.util.*

class WalletCreateAuthViewModel : CandyViewModel() {

    /************************************************************************************
     * WalletCreateAuthActivity
     ***********************************************************************************/

    private val walletApi: WalletAPI by KoinJavaComponent.inject(WalletAPI::class.java)
    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)

    private val _prevPage = SingleLiveEvent<Void>()
    private val _showWalletCreateExplainFragment = SingleLiveEvent<Void>()
    private val _showWalletCreatePinFragment = SingleLiveEvent<Void>()
    private val _showWalletCreatePinConfirmFragment = SingleLiveEvent<Void>()
    private val _showWalletCreateBioDialog = SingleLiveEvent<Void>()
    private val _showWalletCreatePasswordFragment = SingleLiveEvent<Void>()
    private val _showWalletCreatePasswordConfirmFragment = SingleLiveEvent<Void>()
    private val _showWalletCreateSeedFragment = SingleLiveEvent<Void>()
    private val _successCreateAuthWallet = SingleLiveEvent<Void>()

    val prevPage: LiveData<Void> get() = _prevPage
    val showWalletCreateExplainFragment: LiveData<Void> get() = _showWalletCreateExplainFragment
    val showWalletCreatePinFragment: LiveData<Void> get() = _showWalletCreatePinFragment
    val showWalletCreatePinConfirmFragment: LiveData<Void> get() = _showWalletCreatePinConfirmFragment
    val showWalletCreateBioDialog: LiveData<Void> get() = _showWalletCreateBioDialog
    val showWalletCreatePasswordFragment: LiveData<Void> get() = _showWalletCreatePasswordFragment
    val showWalletCreatePasswordConfirmFragment: LiveData<Void> get() = _showWalletCreatePasswordConfirmFragment
    val showWalletCreateSeedFragment: LiveData<Void> get() = _showWalletCreateSeedFragment
    val successCreateAuthWallet: LiveData<Void> = _successCreateAuthWallet

    fun initData() {
        _showWalletCreateExplainFragment.call()
    }

    fun prevPage() = _prevPage.call()

    fun successCreateWallet() {
        _mnemonic.value?.let { successCreateWallet(it) }
    }

    private fun successCreateWallet(mnemonic: String) {
        prefUtil.walletMnemonic = mnemonic
        prefUtil.walletPin = _myPinData.value
        prefUtil.walletBio = isUseBio
        prefUtil.walletPassword = myPasswordData
        _successCreateAuthWallet.call()
    }

    /************************************************************************************
     * WalletCrateExplainFragment
     ***********************************************************************************/
    fun showWalletCreatePinFragment() = _showWalletCreatePinFragment.call()

    /************************************************************************************
     * WalletCratePinFragment
     ***********************************************************************************/

    private val _myPinData = MutableLiveData<String>()
    private val _myPinConfirmData = MutableLiveData<String>()
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

    val myPinData: LiveData<String> get() = _myPinData
    val myPinConfirmData: LiveData<String> get() = _myPinConfirmData
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

    fun onClickNumPad1(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad1RealValue.value ?: "1")
    fun onClickNumPad2(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad2RealValue.value ?: "2")
    fun onClickNumPad3(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad3RealValue.value ?: "3")
    fun onClickNumPad4(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad4RealValue.value ?: "4")
    fun onClickNumPad5(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad5RealValue.value ?: "5")
    fun onClickNumPad6(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad6RealValue.value ?: "6")
    fun onClickNumPad7(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad7RealValue.value ?: "7")
    fun onClickNumPad8(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad8RealValue.value ?: "8")
    fun onClickNumPad9(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad9RealValue.value ?: "9")
    fun onClickNumPad0(isConfirm: Boolean) = setPinData(isConfirm,_pinNumPad0RealValue.value ?: "0")

    fun refreshMyPinData() = _myPinData.postValue("")
    fun refreshMyPinConfirmData() = _myPinConfirmData.postValue("")

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

    private fun setPinData(isConfirm: Boolean, num: String) {
        var pin = (if (isConfirm) _myPinConfirmData.value else _myPinData.value) ?: ""
        if (pin.length < 6) {
            pin += num
            if (isConfirm) _myPinConfirmData.postValue(pin) else _myPinData.postValue(pin)

            if (pin.length == 6) {
                if (isConfirm) {
                    if (pin == _myPinData.value) {
                        _pinInconsistency.postValue(false)
                        _checkBio.call()
                    } else {
                        _pinInconsistency.postValue(true)
                        _myPinConfirmData.postValue("")
                    }
                } else {
                    _showWalletCreatePinConfirmFragment.call()
                }
            }
        }
    }

    fun onClickNumPadDel(isConfirm: Boolean) {
        var pin = (if (isConfirm) _myPinConfirmData.value else _myPinData.value) ?: ""
        if (pin.isNotEmpty()) {
            pin = pin.substring(0, pin.length - 1)
            if (isConfirm) _myPinConfirmData.postValue(pin) else _myPinData.postValue(pin)
        }
    }

    /************************************************************************************
     * WalletCreateBioFragment
     ***********************************************************************************/
    private var isUseBio = false

    private val _checkBio = SingleLiveEvent<Void>()
    private val _successCheckBio = SingleLiveEvent<Void>()

    val checkBio: LiveData<Void> get() = _checkBio
    val successCheckBio: LiveData<Void> get() = _successCheckBio

    private var biometricPrompt: BiometricPrompt? = null

    fun checkBio(activity: AppCompatActivity) {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> { //  생체 인증 가능
                biometricPrompt = setBiometricPrompt(activity)
                _showWalletCreateBioDialog.call()
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> _showWalletCreatePasswordFragment.call() //  기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> _showWalletCreatePasswordFragment.call() //  기기에서 생체 인증을 활성화 안되어있는 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> _showWalletCreatePasswordFragment.call() //  생체 인식 정보가 등록되지 않은 경우
            else -> _showWalletCreatePasswordFragment.call()
        }
    }

    private fun setBiometricPrompt(activity: AppCompatActivity) : BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)
        return BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                JPLog.i("호일", "인증 에러 $errString")
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                JPLog.i("호일", "인증 성공")
                isUseBio = true
                _successCheckBio.call()
                _showWalletCreatePasswordFragment.call()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                JPLog.i("호일", "인증 실패")
            }
        })
    }

    fun useBio() {
        biometricPrompt?.authenticate(BiometricPrompt.PromptInfo.Builder()
            .setTitle("생체인식 인증")
            .setSubtitle("지문을 이용 로그인 합니다.")
            .setNegativeButtonText("취소")
            .build()
        )
    }

    fun unUseBio() = _showWalletCreatePasswordFragment.call()

    /************************************************************************************
     * WalletCreatePasswordFragment
     ***********************************************************************************/
    private var myPasswordData = ""
    private var myPasswordConfirmData = ""

    private val _passwordInconsistency = MutableLiveData<Boolean>(false)
    private val _passwordInconsistencyText = MutableLiveData<String>()

    val passwordInconsistency: LiveData<Boolean> get() = _passwordInconsistency
    val passwordInconsistencyText: LiveData<String> get() = _passwordInconsistencyText

    fun refreshMyPasswordData() {
        myPasswordData = ""
    }

    fun refreshMyPasswordConfirmData() {
        myPasswordConfirmData = ""
    }

    fun checkPassword(isConfirm: Boolean, password: String) {
        _passwordInconsistency.postValue(false)
        if (password.isEmpty()) {
            _passwordInconsistencyText.postValue("비밀번호가 비어있습니다.")
            _passwordInconsistency.postValue(true)
        } else if (password.length < 9){
            _passwordInconsistencyText.postValue("비밀번호가 9자리 이하입니다.")
            _passwordInconsistency.postValue(true)
        } else {
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
            if (isLowerCase && isUpperCase && isDigit && isSpecialCase) {
                if (isConfirm) {
                    myPasswordConfirmData = password
                    if (myPasswordConfirmData == myPasswordData) {
                        _passwordInconsistency.postValue(false)

                        if (restoreMnemonic == null) {
                            _showWalletCreateSeedFragment.call()
                        } else {
                            successCreateWallet(restoreMnemonic!!)
                        }
                    } else {
                        _passwordInconsistencyText.postValue("비밀번호가 일치하지 않아요.")
                        _passwordInconsistency.postValue(true)
                    }
                } else {
                    myPasswordData = password
                    _showWalletCreatePasswordConfirmFragment.call()
                }
            } else {
                _passwordInconsistencyText.postValue("비밀번호가 조건을 충족하지 못했습니다.")
                _passwordInconsistency.postValue(true)
            }
        }
    }

    /************************************************************************************
     * WalletCreateSeedFragment
     ***********************************************************************************/
    private var restoreMnemonic: String? = null

    private var _myPubKey: String? = null

    private val _mnemonic = MutableLiveData<String>()

    val mnemonic: LiveData<String> get() = _mnemonic

    fun setRestoreMnemonic(mnemonic: String?) {
        this.restoreMnemonic = mnemonic
    }

    fun createWallet() {
        runDisposable(walletApi.createWallet(), {
            _myPubKey = it.pubKey
            _mnemonic.postValue(it.mnemonic)
        }) {
            JPLog.i("호일", "잘못된 요청")
        }
    }

}