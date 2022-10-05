package com.joeware.android.gpulumera.account.wallet.main

import android.content.Intent
import android.net.Uri
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.setting.SettingActivity
import com.joeware.android.gpulumera.account.wallet.history.WalletHistoryActivity
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.account.wallet.transaction.receive.WalletReceiveDialog
import com.joeware.android.gpulumera.account.wallet.transaction.send.WalletSendActivity
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletMainBinding
import com.joeware.android.gpulumera.manager.RemoteConfigManager
import com.joeware.android.gpulumera.reward.ui.history.RewardHistoryDialog
import com.joeware.android.gpulumera.util.AccountUtil
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.java.standalone.KoinJavaComponent.inject
import java.lang.Exception
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class WalletMainActivity : BaseActivity() {

    private val accountUtil: AccountUtil by inject(AccountUtil::class.java)
    private lateinit var binding: ActivityWalletMainBinding
    private val viewModel: WalletMainViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_main)
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    override fun setObserveData() {

    }

    override fun init() {
        setOnClickListener()
        viewModel.initWalletMain()
        showAdBanner()
    }

    private fun setOnClickListener() {
        binding.btnBack.setOnClickListener { finish() }
        binding.btnSetting.setOnClickListener { startActivity(Intent(this, SettingActivity::class.java)) }
        binding.lyPoint.setOnClickListener { RewardHistoryDialog.showDialog(supportFragmentManager) }
        binding.btnEthReceive.setOnClickListener { WalletReceiveDialog.showDialog(supportFragmentManager, WalletTokenType.ETH) }
        binding.btnSolReceive.setOnClickListener { WalletReceiveDialog.showDialog(supportFragmentManager, WalletTokenType.SOL) }
        binding.btnEthSend.setOnClickListener { walletSendStartForResult.launch(WalletSendActivity.startActivityIntent(this, WalletTokenType.ETH)) }
        binding.btnSolSend.setOnClickListener { walletSendStartForResult.launch(WalletSendActivity.startActivityIntent(this, WalletTokenType.SOL)) }
        binding.btnEthHistory.setOnClickListener {
            val ethscanUrl = "https://etherscan.io/address/${accountUtil.getWalletEthereumAddress()}"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(ethscanUrl))
            startActivity(browserIntent)
            //startActivity(WalletHistoryActivity.startActivityIntent(this, WalletTokenType.ETH))
        }
        binding.btnSolHistory.setOnClickListener {
            val solscanUrl = "https://solscan.io/account/${accountUtil.getWalletSolanaAddress()}"
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(solscanUrl))
            startActivity(browserIntent)
            //startActivity(WalletHistoryActivity.startActivityIntent(this, WalletTokenType.SOL))
        }
        binding.btnRefresh.setOnClickListener { viewModel.refreshCall() }
        binding.btnExchange.setOnClickListener {

        }
        binding.btnSwap.setOnClickListener {

        }
    }

    private fun showAdBanner() {
        try {
            val bybitJson = RemoteConfigManager.getInstance().firebaseRemoteConfig.getString("bybit")
            if (bybitJson.isNotEmpty()) {
                val gson = Gson()
                val bybit: Map<String, Any?>? = gson.fromJson(bybitJson, object : TypeToken<Map<String, Any?>?>() {}.type)
                if (bybit?.containsKey("isShowDialog") == true) {
                    if (!(bybit["isShowDialog"] as Boolean)) {
                        binding.lyAdBanner.visibility = View.GONE
                    } else {
                        binding.lyAdBanner.visibility = View.VISIBLE
                        setListDate(bybit["listDate"]?.toString())
                        setBybitUrl(bybit["bybitBannerUrl"]?.toString())
                    }
                } else { binding.lyAdBanner.visibility = View.GONE }
            } else {
                binding.lyAdBanner.visibility = View.VISIBLE
                setListDate(null)
                setBybitUrl(null)
            }
        } catch (e: Exception) {
            binding.lyAdBanner.visibility = View.GONE
        }
    }

    private fun setListDate(date: String?) {
        val df = SimpleDateFormat("yyyy-MM-dd")
        val listDate = df.parse(date ?: "2022-09-28")
        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time.time
        val dDay = ((listDate?.time ?: 0) - today) / (24 * 60 * 60 * 1000)
        if (dDay < 0) {
            binding.tvBybitDate.visibility = View.GONE
        } else {
            binding.tvBybitDate.visibility = View.VISIBLE
            binding.tvBybitDate.text = "D-${dDay} / ${DateFormat.getDateInstance(DateFormat.LONG).format(listDate?.time)}"
        }
    }

    private fun setBybitUrl(url: String?) {
        val bybitUrl = url ?: "https://www.bybit.com/en-US/register?affiliate_id=41422&group_id=0&group_type=1"
        binding.lyAdBanner.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(bybitUrl))
            startActivity(browserIntent)
        }
    }





    private val walletSendStartForResult = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if(result.resultCode == RESULT_OK) {
            viewModel.refreshCall()
        } else if(result.resultCode == RESULT_CANCELED) {

        }
    }
}