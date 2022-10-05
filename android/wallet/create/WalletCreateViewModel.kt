package com.joeware.android.gpulumera.account.wallet.create

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.account.wallet.model.*
import com.joeware.android.gpulumera.api.CandyPlusAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import com.joeware.android.gpulumera.util.safeLet
import com.jpbrothers.base.util.log.JPLog
import org.koin.java.standalone.KoinJavaComponent

class WalletCreateViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by KoinJavaComponent.inject(PrefUtil::class.java)
    private val api: CandyPlusAPI by KoinJavaComponent.inject(CandyPlusAPI::class.java)

    private var mode: WalletCreateMode = WalletCreateMode.CREATE_MODE
    private var currentFragmentPosition = -1
    private var limitBackButtonPosition = -1

    private var _cancelFinishActivity = SingleLiveEvent<Void>()
    private var _successFinishActivity = SingleLiveEvent<Void>()
    private val _showFragment = MutableLiveData<Fragment>()

    val cancelFinishActivity: LiveData<Void> get() = _cancelFinishActivity
    val successFinishActivity: LiveData<Void> get() = _successFinishActivity
    val showFragment: LiveData<Fragment> get() = _showFragment

    fun initWalletCreate(mode: WalletCreateMode) {
        this.mode = mode
        currentFragmentPosition = -1
        nextPage()
    }

    fun cancelFinish() = _cancelFinishActivity.call()
    fun successFinish() = _successFinishActivity.call()

    fun nextPage() {
        currentFragmentPosition += 1
        val nextFragment = getFragment(currentFragmentPosition)
        if (nextFragment != null) {
            _showFragment.postValue(nextFragment)
        } else {
            successFinish()
        }
    }

    fun prevPage() {
        if (currentFragmentPosition >= limitBackButtonPosition) return
        currentFragmentPosition -= 1
        val prevFragment = getFragment(currentFragmentPosition)
        if (prevFragment != null) {
            _showFragment.postValue(prevFragment)
        } else {
            cancelFinish()
        }
    }

    private fun getFragment(position: Int) : Fragment?{
        when(mode) {
            WalletCreateMode.CREATE_MODE -> {
                limitBackButtonPosition = 5
                return when(position) {
                    0 -> WalletGuideFragment()
                    1 -> WalletPinFragment().apply { arguments = Bundle().apply { putString("mode", WalletPinMode.CREATE_MODE.name) }}
                    2 -> WalletPinFragment().apply { arguments = Bundle().apply { putString("mode", WalletPinMode.CREATE_CHECK_MODE.name) }}
                    3 -> WalletPasswordFragment().apply { arguments = Bundle().apply { putString("mode", WalletPasswordMode.CREATE_MODE.name) }}
                    4 -> WalletPasswordFragment().apply { arguments = Bundle().apply { putString("mode", WalletPasswordMode.CREATE_CHECK_MODE.name) }}
                    5 -> WalletBackupFragment()
                    6 -> WalletFinishFragment().apply { arguments = Bundle().apply { putString("mode", WalletFinishMode.CREATE_MODE.name) } }
                    else -> null
                }
            }
            WalletCreateMode.RESTORE_MODE -> {
                limitBackButtonPosition = 5
                return when(position) {
                    0 -> WalletPinFragment().apply { arguments = Bundle().apply { putString("mode", WalletPinMode.CREATE_MODE.name) }}
                    1 -> WalletPinFragment().apply { arguments = Bundle().apply { putString("mode", WalletPinMode.CREATE_CHECK_MODE.name) }}
                    2 -> WalletPasswordFragment().apply { arguments = Bundle().apply { putString("mode", WalletPasswordMode.CREATE_MODE.name) }}
                    3 -> WalletPasswordFragment().apply { arguments = Bundle().apply { putString("mode", WalletPasswordMode.CREATE_CHECK_MODE.name) }}
                    4 -> WalletRestoreFragment()
                    5 -> WalletFinishFragment().apply { arguments = Bundle().apply { putString("mode", WalletFinishMode.RESTORE_MODE.name) } }
                    else -> null
                }
            }
        }
    }

    /****************************************
     * Wallet Pin
     ****************************************/
    private var pin: String? = null
    fun getPin(): String? = this.pin

    fun setPin(pin: String) {
        this.pin = pin
        nextPage()
    }


    /****************************************
     * Wallet Password
     ****************************************/
    private var password: String? = null
    fun getPassword(): String? = this.password

    fun setPassword(password: String) {
        this.password = password
        nextPage()
    }

    /****************************************
     * Wallet Bio
     ****************************************/
    private var bio: Boolean = false

    fun setUseBio(bio: Boolean) {
        this.bio = bio
    }

    /****************************************
     * Wallet Seed
     ****************************************/
    private var walletInfo: WalletInfo? = null

    fun setWalletInfo(walletInfo: WalletInfo) {
        this.walletInfo = walletInfo
    }

    fun successCreateWallet() {
        prefUtil.userWalletSeed = this.walletInfo?.mnemonic
        prefUtil.userWalletPin = this.pin
        prefUtil.userWalletPassword = this.password
        prefUtil.userWalletEthereumAddress = this.walletInfo?.eth?.address
        prefUtil.userWalletSolanaAddress = this.walletInfo?.sol?.address
        prefUtil.userWalletEthKeystore = this.walletInfo?.eth?.keystore
        safeLet(prefUtil.userWalletSolanaAddress, prefUtil.userWalletEthereumAddress) { sol, eth ->
            runDisposable(api.sendWalletAddress(sol, eth)) { prefUtil.isSendWalletInfo = true }
        }
        runDisposable(api.sendAnalyticsEventCount("Angola", "Create Wallet"), {
            JPLog.i("호일", "Success SendAnalyticsEventCount!")
        }) {
            JPLog.i("호일", "Fail SendAnalyticsEventCount! $it")
        }
        nextPage()
    }



}