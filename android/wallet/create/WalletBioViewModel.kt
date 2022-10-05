package com.joeware.android.gpulumera.account.wallet.create

import android.content.Context
import android.hardware.fingerprint.FingerprintManager
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class WalletBioViewModel(var context : Context) : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)
    private var biometricPrompt: BiometricPrompt? = null

    private val _toastMessage = MutableLiveData<String>()
    private val _cancelFinish = SingleLiveEvent<Void>()
    private val _successFinish = SingleLiveEvent<Void>()

    val toastMessage: LiveData<String> get() = _toastMessage
    val cancelFinish: LiveData<Void> get() = _cancelFinish
    val successFinish: LiveData<Void> get() = _successFinish

    private fun notSupportedBio() {
        prefUtil.isSupportBio = false
        _cancelFinish.call()
    }

    fun checkBio(activity: FragmentActivity) {
        val biometricManager = BiometricManager.from(activity)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK)) {
            BiometricManager.BIOMETRIC_SUCCESS -> { //  생체 인증 가능
                biometricPrompt = setBiometricPrompt(activity)
                prefUtil.isSupportBio = true
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> notSupportedBio() //  기기에서 생체 인증을 지원하지 않는 경우
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> notSupportedBio() //  기기에서 생체 인증을 활성화 안되어있는 경우
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> notSupportedBio() //  생체 인식 정보가 등록되지 않은 경우
            else -> notSupportedBio()
        }
    }

    private fun setBiometricPrompt(activity: FragmentActivity) : BiometricPrompt {
        val executor = ContextCompat.getMainExecutor(activity)
        return BiometricPrompt(activity, executor, object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                JPLog.i("호일", "인증 에러 $errorCode / $errString")
                _toastMessage.postValue(context.getString(R.string.nft_login_bio_fail_msg))
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                JPLog.i("호일", "인증 성공")
                _successFinish.call()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                JPLog.i("호일", "인증 실패")
                _toastMessage.postValue(context.getString(R.string.nft_login_bio_fail_msg))
            }
        })
    }

    fun useBio() {
        biometricPrompt?.authenticate(
            BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.nft_login_bio_fingerprint))
            .setSubtitle(context.getString(R.string.nft_login_bio_fingerprint_msg))
            .setNegativeButtonText(context.getString(R.string.cancel))
            .build()
        )
    }
}