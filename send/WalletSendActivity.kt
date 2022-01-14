package com.joeware.android.gpulumera.reward.ui.wallet.send

import android.content.Intent
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import com.google.zxing.integration.android.IntentIntegrator
import com.google.zxing.integration.android.IntentResult
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.databinding.ActivityWalletSendBinding
import com.joeware.android.gpulumera.reward.ui.wallet.password.input.WalletInputPasswordDialog
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletSendActivity : BaseActivity() {

    private lateinit var binding: ActivityWalletSendBinding
    private val viewModel: WalletSendViewModel by viewModel()

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_wallet_send)
        binding.lifecycleOwner = this
        binding.vm = viewModel
    }

    override fun setObserveData() {
        viewModel.prevPage.observe(this, Observer { onBackPressed() })
        viewModel.showToastMessage.observe(this, Observer { Toast.makeText(this, it, Toast.LENGTH_SHORT).show() })

        viewModel.showWalletSendChooseTokenFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().replace(binding.flMain.id, WalletSendChooseTokenFragment()).commit()
        })
        viewModel.showWalletSendAmountFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletSendAmountFragment()).addToBackStack(null).commit()
        })
        viewModel.showWalletSendAddressFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletSendAddressFragment()).addToBackStack(null).commit()
        })
        viewModel.showWalletSendConfirmFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletSendConfirmFragment()).addToBackStack(null).commit()
        })
        viewModel.showWalletSendSuccessFragment.observe(this, Observer {
            supportFragmentManager.beginTransaction().add(binding.flMain.id, WalletSendSuccessFragment()).addToBackStack(null).commit()
        })
        viewModel.showWalletInputPasswordDialog.observe(this, Observer {
            WalletInputPasswordDialog.showDialog(supportFragmentManager) { viewModel.showWalletSendConfirmFragment() }
        })
        viewModel.showQRActivity.observe(this, Observer { scanQRCode() })
        viewModel.finishActivity.observe(this, Observer { setResult(RESULT_OK);finish() })
    }

    override fun init() {
        viewModel.showWalletSendChooseTokenFragment()
    }

    private fun scanQRCode(){
        val integrator = IntentIntegrator(this)
        integrator.setBeepEnabled(false)
        integrator.setOrientationLocked(true)
        integrator.setPrompt("QR코드를 찍어주세요.")
        integrator.initiateScan()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result: IntentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if(result != null) {
            if (result.contents == null) {
                JPLog.e("호일", "잘못된 QR코드입니다.")
            } else {
                viewModel.setSendAddress(result.contents.toString())
            }
        }
        else{
            super.onActivityResult(requestCode, resultCode, data)
        }
    }
}