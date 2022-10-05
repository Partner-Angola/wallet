package com.joeware.android.gpulumera.challenge.ui.challenge

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.PrizeListAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ActivityPrizeListBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class PrizeListActivity : BaseActivity() {
    private lateinit var binding: ActivityPrizeListBinding
    private val viewModel: PrizeListViewModel by viewModel()

    private val hDecoration = HorizontalSpaceItemDecoration(1, hasHeader = true)
    private val vDecoration = VerticalSpaceItemDecoration(1, hasHeader = true)

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_prize_list)
        binding.apply {
            lifecycleOwner = this@PrizeListActivity
            activity = this@PrizeListActivity
            vm = viewModel

            adapter = PrizeListAdapter().apply {
//                setHasStableIds(true)
                setOnClickListener(object : PrizeListAdapter.OnClickListener {
                    override fun onClickItem(pos: Int) {
                        val intent = Intent(activity!!, PrizeDetailActivity::class.java)
                        intent.putExtra("pos", pos)
                        intent.putExtra("challenge", challenge)
                        viewModel.voteItems.value?.toCollection(ArrayList())?.let { intent.putExtra("joins", it) }
                        startActivity(intent)
                    }
                })
            }

            rcvList.apply {
                removeItemDecoration(hDecoration)
                addItemDecoration(hDecoration)
                removeItemDecoration(vDecoration)
                addItemDecoration(vDecoration)

                (layoutManager as GridLayoutManager).spanSizeLookup =
                    object : GridLayoutManager.SpanSizeLookup() {
                        override fun getSpanSize(position: Int): Int {
                            return when (position) {
                                0 -> 3
                                else -> 1
                            }
                        }
                    }

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(recyclerView, dx, dy)

                        val layoutManager = recyclerView.layoutManager
                        val totalItemCount = layoutManager!!.itemCount
                        val lastVisibleItemPos = when (layoutManager) {
                            is GridLayoutManager -> layoutManager.findLastVisibleItemPosition()
                            is LinearLayoutManager -> layoutManager.findLastVisibleItemPosition()
                            is StaggeredGridLayoutManager -> layoutManager.findLastVisibleItemPositions(
                                null
                            )[0]
                            else -> 0
                        }

                        if (
                            totalItemCount > 1 &&
                            (lastVisibleItemPos + 5) >= totalItemCount
                        ) {
                            viewModel.getJoinList()
                        }
                    }
                })
            }

        }
    }

    override fun setObserveData() {
        viewModel.voteItems.observe(this, Observer { binding.adapter?.setItems(it) })
        viewModel.itemCount.observe(this, Observer { binding.adapter?.itemCount = it })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
    }

    override fun init() {
        val challenge = intent.getParcelableExtra<Challenge>("challenge")
        viewModel.challenge = challenge as Challenge
        binding.adapter?.setChallengeInfo(challenge)

        viewModel.initJoinList()
    }

    fun onShare() {
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(
                Intent.EXTRA_TEXT,
                (viewModel.challenge.titleCountry ?: viewModel.challenge.title) + "\n<Google>\nhttps://play.google.com/store/apps/details?id=com.joeware.android.gpulumera\n<Apple>\nhttps://apps.apple.com/app/id881267423"
            )
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
    }
}