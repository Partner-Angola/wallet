package com.joeware.android.gpulumera.account.wallet.create

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager2.widget.ViewPager2
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.databinding.FragmentWalletGuideBinding
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class WalletGuideFragment : BaseFragment() {

    private lateinit var binding: FragmentWalletGuideBinding
    private val viewModel: WalletGuideViewModel by viewModel()
    private val parentViewModel: WalletCreateViewModel by sharedViewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = FragmentWalletGuideBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = this
        return binding.root
    }

    override fun setObserveData() {
        viewModel.pagerItems.observe(this) { items ->
            binding.indicator.createDotPanel(items.size, R.drawable.indicator_dot_off, R.drawable.indicator_dot_on, 0)
            binding.vpMain.apply {
                adapter = WalletGuideAdapter().apply { setItems(items) }
                registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.indicator.selectDot(position)
                        currentItem = position
                    }
                })
            }
        }
        viewModel.currentPagerIndex.observe(this) { position -> binding.vpMain.currentItem = position }
        viewModel.finishWalletDescription.observe(this) { parentViewModel.nextPage() }
        viewModel.finishParentActivity.observe(this) { parentViewModel.cancelFinish() }

    }

    override fun init() {
        viewModel.makeViewPagerList(requireContext())
        binding.btnNext.setOnClickListener { viewModel.nextPage(binding.vpMain.currentItem) }
        binding.btnBack.setOnClickListener { parentViewModel.cancelFinish() }
    }
}