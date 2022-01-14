package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletCreateAuthBinding
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletCreateAuthActivity : BaseActivity() {


    private lateinit var binding: ActivityWalletCreateAuthBinding
    private val viewModel: WalletCreateAuthViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_create_auth)
        binding.lifecycleOwner = this
    }

    override fun setObserveData() {
        viewModel.prevPage.observe(this, Observer { onBackPressed() })
        viewModel.showWalletCreateExplainFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().replace(binding.flMain.id, WalletCreateExplainFragment()).commit()
        })
        viewModel.showWalletCreatePinFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletCreatePinFragment().apply {
                arguments = Bundle().apply { putBoolean("isConfirm", false) }
            }).addToBackStack(null).commit()
        })
        viewModel.showWalletCreatePinConfirmFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletCreatePinFragment().apply {
                arguments = Bundle().apply { putBoolean("isConfirm", true) }
            }).addToBackStack(null).commit()
        })

        viewModel.checkBio.observe(this, Observer { viewModel.checkBio(this) })
        viewModel.showWalletCreateBioDialog.observe(this, Observer { WalletCreateBioDialog.showDialog(supportFragmentManager) })

        viewModel.showWalletCreatePasswordFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletCreatePasswordFragment().apply {
                arguments = Bundle().apply { putBoolean("isConfirm", false) }
            }).addToBackStack(null).commit()
        })
        viewModel.showWalletCreatePasswordConfirmFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletCreatePasswordFragment().apply {
                arguments = Bundle().apply { putBoolean("isConfirm", true) }
            }).addToBackStack(null).commit()
        })
        viewModel.showWalletCreateSeedFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletCreateSeedFragment().apply {
            }).addToBackStack(null).commit()
        })
        viewModel.successCreateAuthWallet.observe(this, Observer {
            JPLog.i("호일", "종료 시작")
            setResult(RESULT_OK)
            finish()
        })
    }

    override fun init() {
        viewModel.setRestoreMnemonic(intent.getStringExtra("mnemonic"))
        viewModel.initData()
    }
}