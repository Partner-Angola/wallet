package com.joeware.android.gpulumera.challenge.fragment

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseFragment
import com.joeware.android.gpulumera.challenge.*
import com.joeware.android.gpulumera.challenge.adapter.ProgressChallengeAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeActivity
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeDetailActivity
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeManualActivity
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeViewModel
import com.joeware.android.gpulumera.challenge.ui.setting.NicknameInputActivity
import com.joeware.android.gpulumera.challenge.ui.vote.VoteActivity
import com.joeware.android.gpulumera.challenge.ui.vote.VoteListActivity
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.common.C
import com.joeware.android.gpulumera.databinding.FragmentChallengeProgressBinding
import com.jpbrothers.base.util.log.JPLog
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChallengeProgressFragment : BaseFragment() {

    private lateinit var binding: FragmentChallengeProgressBinding
    private val viewModel: ChallengeViewModel by viewModel()

    private val vDecoration = VerticalSpaceItemDecoration(24, includeLast = true, hasHeader = true)

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_challenge_progress,
            container,
            false
        )
        binding.apply {
            view = this@ChallengeProgressFragment
            vm = viewModel
            adapter = ProgressChallengeAdapter(viewModel).apply {
//                setHasStableIds(true)
                setOnClickListener(object : ProgressChallengeAdapter.OnClickListener {
                    override fun onClickCell(challenge: Challenge) {
                        // click vote cell
                        val intent = Intent(requireActivity(), VoteListActivity::class.java)
                        intent.putExtra("challenge", challenge)
                        requireActivity().startActivity(intent)
                    }

                    override fun onClickCellBtn(challenge: Challenge) {
                        // click participate button
                        if (C.me.isLogin) {
                            if (C.me.nickname.isEmpty()) {
                                val intent = Intent(activity!!, NicknameInputActivity::class.java)
                                startActivity(intent)
                            } else {
                                val intent = Intent(activity!!, ChallengeDetailActivity::class.java)
                                intent.putExtra("challenge", challenge)
                                startActivity(intent)
                            }
                        } else {
                            (activity as ChallengeActivity).requestLogin()
                        }
                    }

                    override fun onClickViewAll() {
                        // click vote view all button
                        startActivity(Intent(requireActivity(), VoteActivity::class.java))
                    }

                    override fun onClickNotice() {

                    }

                    override fun onClickManual() {
                        startActivity(Intent(requireActivity(), ChallengeManualActivity::class.java))
                    }
                })
            }

            rvList.apply {
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
                            totalItemCount > 1 &&
                            (lastVisibleItemPos + 2) >= totalItemCount
                        ) {
                            viewModel.getProgressChallengeList()
                        }
                    }
                })
            }

            swipeContainer.setOnRefreshListener {
                viewModel.initVoteChallengeList()
                viewModel.initProgressChallengeList()
            }
        }

        return binding.root
    }

    override fun setObserveData() {
        viewModel.voteItems.observe(
            this,
            Observer { items -> binding.adapter?.setHeaderItems(items) })
        viewModel.participateItems.observe(
            this,
            Observer { items -> binding.adapter?.setItems(items) })
        viewModel.showToastMessage.observe(
            this,
            Observer { msg -> ToastUtils.showShort(msg) }
        )
        viewModel.refreshEnd.observe(this, Observer {
            binding.swipeContainer.isRefreshing = false
        })
    }

    override fun init() {
        viewModel.initVoteChallengeList()
        viewModel.initProgressChallengeList()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChallengeProgressFragment()
    }
}