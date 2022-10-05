package com.joeware.android.gpulumera.account.wallet.transaction.send

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletTokenType
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletSendBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletSendActivity : BaseActivity() {
    companion object {
        fun startActivityIntent(context: Context, type: WalletTokenType) : Intent {
            val intent = Intent(context, WalletSendActivity::class.java)
            intent.putExtra("type", type.name)
            return intent
        }
    }

    private val viewModel: WalletSendViewModel by viewModel()
    private lateinit var binding: ActivityWalletSendBinding
    private var tokenType: WalletTokenType? = null


    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_send)
        binding.lifecycleOwner = this
        tokenType = when(intent.getStringExtra("type")) {
            WalletTokenType.ETH.name -> WalletTokenType.ETH
            WalletTokenType.SOL.name -> WalletTokenType.SOL
            else -> null
        }
    }

    override fun setObserveData() {
        viewModel.cancelFinishActivity.observe(this) {
            setResult(RESULT_CANCELED)
            finish()
        }
        viewModel.successFinishActivity.observe(this) {
            setResult(RESULT_OK)
            finish()
        }
        viewModel.showFragment.observe(this) { fragment ->
            supportFragmentManager.beginTransaction().replace(binding.mainFrame.id, fragment).commitAllowingStateLoss()
        }
    }

    override fun init() {
        tokenType?.let { type ->
            viewModel.initWalletSend(type)
        } ?: run {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        viewModel.prevPage()
    }


}