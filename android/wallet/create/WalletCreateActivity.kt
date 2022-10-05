package com.joeware.android.gpulumera.account.wallet.create

import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.account.wallet.model.WalletCreateMode
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.camera.ai.AiCameraResultDetailFragment
import com.joeware.android.gpulumera.databinding.ActivityWalletCreateBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletCreateActivity : BaseActivity() {

    companion object {
        fun startActivityIntent(context: Context, mode: WalletCreateMode) : Intent{
            return Intent(context, WalletCreateActivity::class.java).apply {
                putExtra("mode", mode.name)
            }
        }
    }
    private val viewModel: WalletCreateViewModel by viewModel()
    private lateinit var binding: ActivityWalletCreateBinding
    private var createMode: WalletCreateMode? = null

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_create)
        binding.lifecycleOwner = this
        createMode = when(intent.getStringExtra("mode")) {
            WalletCreateMode.CREATE_MODE.name -> WalletCreateMode.CREATE_MODE
            WalletCreateMode.RESTORE_MODE.name -> WalletCreateMode.RESTORE_MODE
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
        createMode?.let { mode ->
            viewModel.initWalletCreate(mode)
        } ?: run {
            setResult(RESULT_CANCELED)
            finish()
        }
    }

    override fun onBackPressed() {
        viewModel.prevPage()
    }
}