package com.joeware.android.gpulumera.account.wallet.transaction.send

import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.blankj.utilcode.util.StringUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.EthGasPrice
import com.joeware.android.gpulumera.account.wallet.model.WalletToken
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenDetailType
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.api.WalletAPI
import com.joeware.android.gpulumera.api.WalletEthAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.AccountUtil
import com.joeware.android.gpulumera.util.PrefUtil
import com.joeware.android.gpulumera.util.SingleLiveEvent
import org.koin.java.standalone.KoinJavaComponent
import org.koin.java.standalone.KoinJavaComponent.inject
import retrofit2.http.Field
import retrofit2.http.Header
import java.lang.Exception

class WalletSendViewModel : CandyViewModel() {

    private val prefUtil: PrefUtil by inject(PrefUtil::class.java)
    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)
    private val walletAPI: WalletAPI by inject(WalletAPI::class.java)
    private val walletEthAPI: WalletEthAPI by inject(WalletEthAPI::class.java)

    private var type: WalletTokenType = WalletTokenType.ETH
    private var currentFragmentPosition = -1
    private var limitBackButtonPosition = -1

    private var _cancelFinishActivity = SingleLiveEvent<Void>()
    private var _successFinishActivity = SingleLiveEvent<Void>()
    private val _showFragment = MutableLiveData<Fragment>()

    val cancelFinishActivity: LiveData<Void> get() = _cancelFinishActivity
    val successFinishActivity: LiveData<Void> get() = _successFinishActivity
    val showFragment: LiveData<Fragment> get() = _showFragment

    fun initWalletSend(type: WalletTokenType) {
        this.type = type
        currentFragmentPosition = -1
        nextPage()
    }

    fun cancelFinish() {
        _cancelFinishActivity.call()
    }

    fun successFinish() {
        _successFinishActivity.call()
    }

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

    private fun getFragment(position: Int) : Fragment? {
        limitBackButtonPosition = 5
        return when(position) {
            0 -> WalletSendSelectTokenFragment()
            1 -> WalletSendAmountFragment()
            2 -> WalletSendAddressFragment()
            3 -> WalletSendCommissionFragment()
            4 -> WalletSendPasswordFragment()
            5 -> WalletSendFinishFragment()
            else -> null
        }
    }

    fun getTokenType() = this.type

    /****************************************
     * WalletSendSelectToken
     ****************************************/

    private val _selectTokenItems = MutableLiveData<List<WalletToken>>()
    private val _selectToken = MutableLiveData<WalletToken>()

    val selectTokenItems: LiveData<List<WalletToken>> get() = _selectTokenItems
    val selectToken: LiveData<WalletToken> get() = _selectToken

    fun initTokenList() {
        val ethAddress = accountUtil.getWalletEthereumAddress() ?: ""
        val ethContract = accountUtil.getEthAngolaContract()
        val solAddress = accountUtil.getWalletSolanaAddress() ?: ""
        val solContract = accountUtil.getSolAngolaContract()

        onProgress(true)
        runDisposable(walletAPI.getWalletBalanceInfo(ethAddress, ethContract, solAddress, solContract), { info ->
            onProgress(false)
            if (info.code == 200) {
                when(type) {
                    WalletTokenType.ETH -> {
                        val angolaBalance = info.asset.ethTokenBalance
                        val ethBalance = info.asset.ethBalance
                        _selectTokenItems.postValue(listOf(
                            WalletToken(
                                R.drawable.logo_ang,
                                StringUtils.getString(R.string.angola_token),
                                "AGLA(ETH)",
                                angolaBalance,
                                angolaBalance.toString(),
                                null,
                                WalletTokenType.ETH,
                                WalletTokenDetailType.SUB,
                                ethAddress,
                                ethContract
                            ),
                            WalletToken(
                                R.drawable.logo_eth,
                                StringUtils.getString(R.string.eth_wallet),
                                "ETH",
                                ethBalance,
                                ethBalance.toString(),
                                null,
                                WalletTokenType.ETH,
                                WalletTokenDetailType.MAIN,
                                ethAddress,
                                null
                            )
                        ))
                    }
                    WalletTokenType.SOL -> {
                        val angolaBalance = info.asset.solTokenBalance
                        val solBalance = info.asset.solBalance
                        _selectTokenItems.postValue(listOf(
                            WalletToken(
                                R.drawable.logo_ang,
                                StringUtils.getString(R.string.angola_token),
                                "AGLA(SOL)",
                                angolaBalance,
                                angolaBalance.toString(),
                                null,
                                WalletTokenType.SOL,
                                WalletTokenDetailType.SUB,
                                ethAddress,
                                ethContract
                            ),
                            WalletToken(
                                R.drawable.logo_sol,
                                StringUtils.getString(R.string.sol_token),
                                "SOL",
                                solBalance,
                                solBalance.toString(),
                                null,
                                WalletTokenType.SOL,
                                WalletTokenDetailType.MAIN,
                                ethAddress,
                                null
                            )
                        ))
                    }
                }
            } else {
                //TODO 토큰 선택 조회 오류
            }
        }) {
            onProgress(false)
            //TODO 토큰 선택 조회 오류
        }
    }

    fun setSelectToken(token: WalletToken) {
        _selectToken.postValue(token)
        nextPage()
    }

    fun getSelectToken() = _selectToken.value

    /****************************************
     * WalletSendAmount
     ****************************************/
    private var sendAmount: Double = 0.0

    fun setSendAmount(amount: Double) {
        this.sendAmount = amount
        nextPage()
    }

    fun getSendAmount() = this.sendAmount
    fun getMyAddress(): String? {
        return when(type) {
            WalletTokenType.ETH -> accountUtil.getWalletEthereumAddress()
            WalletTokenType.SOL -> accountUtil.getWalletSolanaAddress()
        }
    }

    /****************************************
     * WalletSendAddress
     ****************************************/
    private var receiveAddress: String? = null

    private val _notFoundAddress = SingleLiveEvent<Void>()

    val notFoundAddress: LiveData<Void> get() = _notFoundAddress

    fun checkAddress(address: String) {
        val typeStr = if (type == WalletTokenType.ETH) "eth" else if(type == WalletTokenType.SOL) "sol" else ""
        runDisposable(walletAPI.sendWalletCheckAddress(typeStr, address), {
            try{
                if (it["status"] == true) {
                    this.receiveAddress = address
                    nextPage()
                } else {
                    _notFoundAddress.call()
                }
            } catch (e: Exception) {
                _notFoundAddress.call()
            }
        }) {
            _notFoundAddress.call()
        }
    }

    fun getReceiveAddress() = this.receiveAddress

    /****************************************
     * WalletSendCommission
     ****************************************/
    private var commission: Double = 0.0

    private val _ethGasPrice = MutableLiveData<List<EthGasPrice>>()

    val ethGasPrice: LiveData<List<EthGasPrice>> get() = _ethGasPrice

    fun getCommissionList() {
        runDisposable(walletEthAPI.getGasPrice(), {
            _ethGasPrice.postValue(it)
        }) {
            //TODO 수수료 조회 실패
        }
    }

    fun setCommission(commission: Double) {
        this.commission = commission
    }

    fun getCommissionStr() : String {
        return when(type) {
            WalletTokenType.ETH -> "%f GWEI".format(commission)
            WalletTokenType.SOL -> "%f SOL".format(commission)
        }
    }
    /****************************************
     * WalletSendPassword
     ****************************************/
    private var isSending = false   // 보내기 진행중인지 상태

    fun successInputPassword() {
        if (!isSending) {
            isSending = true
            _selectToken.value?.let { token ->
                when(token.tokenType) {
                    WalletTokenType.ETH -> {
                        when(token.tokenTypeDetail) {
                            WalletTokenDetailType.MAIN -> sendEthereum()
                            WalletTokenDetailType.SUB -> sendAngolaEth()
                        }
                    }
                    WalletTokenType.SOL -> {
                        when(token.tokenTypeDetail) {
                            WalletTokenDetailType.MAIN -> sendSolana()
                            WalletTokenDetailType.SUB -> sendAngolaSol()
                        }
                    }
                }
            }
        }
    }

    private fun sendEthereum() {
        runDisposable(walletEthAPI.sendEthereum(
            accountUtil.getEthereumKeystore()!!,
            accountUtil.getPassword()!!,
            accountUtil.getWalletEthereumAddress()!!,
            this.commission,
            this.receiveAddress!!,
            this.sendAmount
        ), {
            isSending = false
            if (it.status == 200) {
                nextPage()
            } else if (it.status == 500) {
                //TODO 비밀번호 오류
            }
        }){
            isSending = false
            //TODO 전송 실패
        }
    }

    private fun sendAngolaEth() {
        runDisposable(walletEthAPI.sendEthereumToken(
            accountUtil.getEthereumKeystore()!!,
            accountUtil.getPassword()!!,
            accountUtil.getWalletEthereumAddress()!!,
            this.commission,
            this.receiveAddress!!,
            this.sendAmount,
            accountUtil.getEthAngolaContract()
        ), {
            isSending = false
            if (it.status == 200) {
                nextPage()
            } else if (it.status == 500) {
                //TODO 비밀번호 오류
            }
        }){
            isSending = false
            //TODO 전송 실패
        }
    }

    private fun sendSolana() {
        runDisposable(walletAPI.sendSolana(
            accountUtil.getSeed()!!,
            this.receiveAddress!!,
            this.sendAmount
        ), {
            isSending = false
            if (it.status == 200) {
                nextPage()
            } else if (it.status == 500) {
                //TODO 비밀번호 오류
            }
        }){
            isSending = false
            //TODO 전송 실패
        }
    }



    private fun sendAngolaSol() {
        runDisposable(walletAPI.sendSolanaToken(
            accountUtil.getSeed()!!,
            accountUtil.getSolAngolaContract(),
            this.receiveAddress!!,
            this.sendAmount
        ), {
            isSending = false
            if (it.status == 200) {
                nextPage()
            } else if (it.status == 500) {
                //TODO 비밀번호 오류
            }
        }){
            isSending = false
            //TODO 전송 실패
        }
    }


    /****************************************
     * WalletSendFinish
     ****************************************/
}