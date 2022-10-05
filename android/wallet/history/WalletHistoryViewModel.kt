package com.joeware.android.gpulumera.account.wallet.history

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.joeware.android.gpulumera.account.wallet.model.EthHistory
import com.joeware.android.gpulumera.account.wallet.model.EthTokenHistory
import com.joeware.android.gpulumera.account.wallet.model.SolHistory
import com.joeware.android.gpulumera.account.wallet.model.WalletHistoryType
import com.joeware.android.gpulumera.api.WalletAPI
import com.joeware.android.gpulumera.api.WalletEthAPI
import com.joeware.android.gpulumera.base.CandyViewModel
import com.joeware.android.gpulumera.util.AccountUtil
import org.koin.java.standalone.KoinJavaComponent.inject

class WalletHistoryViewModel : CandyViewModel() {

    private val walletAPI: WalletAPI by inject(WalletAPI::class.java)
    private val walletEthAPI: WalletEthAPI by inject(WalletEthAPI::class.java)
    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)

    private val _tokenAmount = MutableLiveData<String>()
    private val _ethTokenItems = MutableLiveData<List<EthTokenHistory>>()
    private val _ethItems = MutableLiveData<List<EthHistory>>()
    private val _solItems = MutableLiveData<List<SolHistory>>()
    private val _isEmpty = MutableLiveData<Boolean>()

    val tokenAmount: LiveData<String> get() = _tokenAmount
    val ethTokenItems: LiveData<List<EthTokenHistory>> get() = _ethTokenItems
    val ethItems: LiveData<List<EthHistory>> get() = _ethItems
    val solItems: LiveData<List<SolHistory>> get() = _solItems
    val isEmpty: LiveData<Boolean> get() = _isEmpty

    fun getWalletHistory(type: WalletHistoryType?) {
        when(type) {
            WalletHistoryType.ETH -> getEthHistory()
            WalletHistoryType.ETH_ANG -> getEthAngHistory()
            WalletHistoryType.SOL -> getSolHistory()
            WalletHistoryType.SOL_ANG -> getSolAngHistory()
            else -> return
        }
        getTokenBalance(type)
    }

    private fun getTokenBalance(type: WalletHistoryType) {
        val ethAddress = accountUtil.getWalletEthereumAddress() ?: ""
        val ethContract = accountUtil.getEthAngolaContract()
        val solAddress = accountUtil.getWalletSolanaAddress() ?: ""
        val solContract = accountUtil.getSolAngolaContract()
        runDisposable(walletAPI.getWalletBalanceInfo(ethAddress, ethContract, solAddress, solContract), { info ->
            _tokenAmount.postValue(
                when(type) {
                    WalletHistoryType.ETH -> info.asset.ethBalance.toString()
                    WalletHistoryType.ETH_ANG -> info.asset.ethTokenBalance.toString()
                    WalletHistoryType.SOL -> info.asset.solBalance.toString()
                    WalletHistoryType.SOL_ANG -> info.asset.solTokenBalance.toString()
                }
            )
        }) {}
    }

    private fun getEthHistory() {
        accountUtil.getWalletEthereumAddress()?.let { address ->
            onProgress(true)
            _isEmpty.postValue(false)
            runDisposable(walletEthAPI.getEthHistory(address), {
                onProgress(false)
                if (it.result.isNotEmpty()) _ethItems.postValue(it.result) else _isEmpty.postValue(true)
            }) { onProgress(false);_isEmpty.postValue(false);onError(it) }
        }
    }

    private fun getEthAngHistory() {
        //0xf59f324ef94b2324846d730034a5e5fdb1a3e8e8
        val items = listOf(
            EthTokenHistory(
                "15438644",
                1661838627,
                "0xc4480f20aa80b89a937cc1f7c0cb4bfbaeaf5c2b16157ee95911f3dd22d50ca4",
                "137",
                "0xcd03439152fb6188569a4a072cb7e52ebd334966d213d4dab61e60c12d1b0768",
                "0x83034ac8fb51501c9519a751069688c7164c3b68",
                "0xa00a4d5786a6e955e9539d01d78bf68f3271c050",
                "0xf59f324ef94b2324846d730034a5e5fdb1a3e8e8",
                "200000000000000000000",
                "Quiztok Token",
                "QTCON",
                "18",
                "264",
                "65000",
                "5000000000",
                "31590",
                "26855372",
                "deprecated",
                "98873"
            ),
            EthTokenHistory(
                "15438644",
                1661838627,
                "0xc4480f20aa80b89a937cc1f7c0cb4bfbaeaf5c2b16157ee95911f3dd22d50ca4",
                "137",
                "0xcd03439152fb6188569a4a072cb7e52ebd334966d213d4dab61e60c12d1b0768",
                "0xf59f324ef94b2324846d730034a5e5fdb1a3e8e8",
                "0xa00a4d5786a6e955e9539d01d78bf68f3271c050",
                "0xf59f324ef94b2324846d730034a5e5fdb1a3e8e1",
                "200000000000000000000",
                "Quiztok Token",
                "QTCON",
                "18",
                "264",
                "65000",
                "5000000000",
                "31590",
                "26855372",
                "deprecated",
                "98873"
            ),
            EthTokenHistory(
                "15438644",
                1661838627,
                "0xc4480f20aa80b89a937cc1f7c0cb4bfbaeaf5c2b16157ee95911f3dd22d50ca4",
                "137",
                "0xcd03439152fb6188569a4a072cb7e52ebd334966d213d4dab61e60c12d1b0768",
                "0xf59f324ef94b2324846d730034a5e5fdb1a3e8e8",
                "0xa00a4d5786a6e955e9539d01d78bf68f3271c050",
                "0xf59f324ef94b2324846d730034a5e5fdb1a3e8e1",
                "200000000000000000000",
                "Quiztok Token",
                "QTCON",
                "18",
                "264",
                "65000",
                "5000000000",
                "31590",
                "26855372",
                "deprecated",
                "98873"
            )
        )
        _ethTokenItems.postValue(items)
    }

    private fun getSolHistory() {
        accountUtil.getWalletSolanaAddress()?.let { address ->
            onProgress(true)
            _isEmpty.postValue(false)
            runDisposable(walletAPI.getSolanaHistory(address), {
                onProgress(false)
                if (it.result.isNotEmpty()) _solItems.postValue(it.result) else _isEmpty.postValue(true)
            }) { onProgress(false);_isEmpty.postValue(false);onError(it) }
        }
    }

    private fun getSolAngHistory() {
        accountUtil.getWalletSolanaAddress()?.let { address ->
            onProgress(true)
            _isEmpty.postValue(false)
            runDisposable(walletAPI.getSolanaTokenHistory(address, accountUtil.getSolAngolaContract()), {
                onProgress(false)
                if (it.result.isNotEmpty()) _solItems.postValue(it.result) else _isEmpty.postValue(true)
            }) { onProgress(false);_isEmpty.postValue(false);onError(it) }
        }
    }
}