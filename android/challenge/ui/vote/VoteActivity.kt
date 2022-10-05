package com.joeware.android.gpulumera.challenge.ui.vote

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.VoteAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ActivityVoteBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class VoteActivity : BaseActivity() {
    private lateinit var binding: ActivityVoteBinding
    private val viewModel: VoteViewModel by viewModel()

    private val vDecoration = VerticalSpaceItemDecoration(24, includeLast = true)

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_vote)
        binding.apply {
            lifecycleOwner = this@VoteActivity
            activity = this@VoteActivity
            vm = viewModel

            adapter = VoteAdapter().apply {
//                setHasStableIds(true)
                setOnClickListener(object : VoteAdapter.OnClickListener {
                    override fun onClickBtn(challenge: Challenge) {
                        val intent = Intent(this@VoteActivity, VoteListActivity::class.java)
                        intent.putExtra("challenge", challenge)
                        startActivity(intent)
                    }
                })
            }

            rcvList.apply {
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
                            (lastVisibleItemPos + 2) >= totalItemCount
                        ) {
                            viewModel.getVoteList()
                        }
                    }
                })
            }

            swipeContainer.setOnRefreshListener {
                viewModel.initVoteList()
            }
        }
    }

    override fun setObserveData() {
        viewModel.voteItems.observe(
            this,
            Observer { items -> binding.adapter?.setItems(items) })
        viewModel.refreshEnd.observe(this, Observer { binding.swipeContainer.isRefreshing = false })
    }

    override fun init() {
        viewModel.initVoteList()
    }
}