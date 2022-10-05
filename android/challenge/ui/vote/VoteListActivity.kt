package com.joeware.android.gpulumera.challenge.ui.vote

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
import com.joeware.android.gpulumera.challenge.adapter.VoteListAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ActivityVoteListBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoteListActivity : BaseActivity() {
    private lateinit var binding: ActivityVoteListBinding
    private val viewModel: VoteListViewModel by viewModel()
    private val voteListDataUtil: VoteListDataUtil by inject()

    private val hDecoration = HorizontalSpaceItemDecoration(1)
    private val vDecoration = VerticalSpaceItemDecoration(1)

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote_list)
        binding.apply {
            lifecycleOwner = this@VoteListActivity
            activity = this@VoteListActivity
            vm = viewModel

            adapter = VoteListAdapter().apply {
//                setHasStableIds(true)
                setOnClickListener(object : VoteListAdapter.OnClickListener {
                    override fun onClickJoin(pos: Int) {
                        viewModel.items.value?.toCollection(ArrayList())?.let {
                            val intent = Intent(this@VoteListActivity, VoteDetailActivity::class.java)
                            voteListDataUtil.setChallenge(true, pos, viewModel.challenge, it)
                            startActivityForResult(intent, REQUEST_VOTE_DETAIL)
                        }

                    }
                })
            }

            rcvList.apply {
                removeItemDecoration(hDecoration)
                addItemDecoration(hDecoration)
                removeItemDecoration(vDecoration)
                addItemDecoration(vDecoration)

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
                            totalItemCount > 0 &&
                            (lastVisibleItemPos + 5) >= totalItemCount
                        ) {
                            viewModel.getJoinList()
                        }
                    }
                })
            }

            swipeContainer.setOnRefreshListener {
                viewModel.initJoinList()
            }
        }
    }

    override fun setObserveData() {
        viewModel.items.observe(this, Observer { items -> binding.adapter?.setItems(items) })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
        viewModel.refreshEnd.observe(this, Observer { binding.swipeContainer.isRefreshing = false })
    }

    override fun init() {
        val challenge = intent.getParcelableExtra<Challenge>("challenge") as Challenge
        viewModel.challenge = challenge
        binding.challenge = challenge

        viewModel.initJoinList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_VOTE_DETAIL) {
            voteListDataUtil.requestVoteDetailActivity?.let {
                viewModel.setItems(it)
                voteListDataUtil.clearRequestVoteDetailActivity()
            }
        }
    }

    companion object {
        private const val REQUEST_VOTE_DETAIL = 0x1010
    }
}
