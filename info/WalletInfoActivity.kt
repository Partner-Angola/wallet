package com.joeware.android.gpulumera.reward.ui.wallet.info

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletInfoBinding
import com.joeware.android.gpulumera.reward.ui.wallet.logout.WalletLogoutDialog
import com.joeware.android.gpulumera.reward.ui.wallet.password.create.WalletCreateAuthActivity
import com.joeware.android.gpulumera.reward.ui.wallet.password.input.WalletInputPasswordDialog
import com.joeware.android.gpulumera.reward.ui.wallet.password.input.WalletInputSeedDialog
import com.joeware.android.gpulumera.reward.ui.wallet.password.input.WalletSeedDialog
import com.joeware.android.gpulumera.reward.ui.wallet.receive.WalletReceiveDialog
import com.joeware.android.gpulumera.reward.ui.wallet.transaction.WalletTransactionDialog
import com.joeware.android.gpulumera.reward.ui.wallet.send.WalletSendActivity
import com.joeware.android.gpulumera.util.safeLet
//import com.joeware.android.gpulumera.reward.ui.wallet.receive.WalletReceiveDialog
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletInfoActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletInfoBinding
    private val viewModel : WalletInfoViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_info)
        binding.lifecycleOwner = this
        binding.adapter = WalletInfoAdapter().apply {
            setOnClickListener(object : WalletInfoClickListener {
                override fun onWalletInfoClick(index: Int) {
                    when(index){
                        0 -> viewModel.showWalletTransactionDialog()
                        1 -> WalletInputSeedDialog.showDialog(supportFragmentManager) { seed ->
                            val intent = Intent(this@WalletInfoActivity, WalletCreateAuthActivity::class.java).apply { putExtra("mnemonic", seed) }
                            startActivity(intent)}
                        2 -> WalletInputPasswordDialog.showDialog(supportFragmentManager) { WalletSeedDialog.showDialog(supportFragmentManager) }
                        3 -> {WalletLogoutDialog.showDialog(supportFragmentManager) {
                                viewModel.logoutWallet()
                                setResult(RESULT_OK, Intent().apply { putExtra("logout", true) })
                                finish()
                            }}
                    }
                }
            })
        }
        binding.vm = viewModel
    }

    override fun setObserveData() {
        viewModel.clickReceiveToken.observe(this, Observer { viewModel.showWalletReceiveDialog() })
        viewModel.clickSendToken.observe(this, Observer {
            val intent = Intent(this, WalletSendActivity::class.java)
            startActivityForResult(intent , 101)
        })
        viewModel.showWalletReceiveDialog.observe(this, Observer {
            safeLet(it["mnemonic"], it["pubKey"]) { mnemonic, pubKey ->
                WalletReceiveDialog.showDialog(supportFragmentManager, mnemonic, pubKey)
            }
        })
        viewModel.showWalletTransactionDialog.observe(this, Observer {
            it["pubKey"]?.let { pubKey ->
                WalletTransactionDialog.showDialog(supportFragmentManager, pubKey, viewModel.tokenAngolaAmount.value.toString(), viewModel.tokenSolanaAmount.value.toString())
            }
        })
        viewModel.clickBack.observe(this, Observer { finish() })
        viewModel.clickRefresh.observe(this, Observer { init() })
        viewModel.visibilityProgress.observe(this, Observer { binding.layoutProgress.visibility = it })
    }

    override fun init() {
        safeLet(intent.getStringExtra("mnemonic"), intent.getStringExtra("pubKey")) { mnemonic, pubKey  ->
            viewModel.initWalletInfo(mnemonic, pubKey)
        } ?: run { finish() }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 101 && resultCode == RESULT_OK) {
            viewModel.getTokenGetBalance()
        }
    }

    /****************************************
     * HistoryClickListener
     ****************************************/
    interface WalletInfoClickListener {
        fun onWalletInfoClick(index: Int)
    }
}