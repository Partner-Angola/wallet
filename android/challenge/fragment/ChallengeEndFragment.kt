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
import com.joeware.android.gpulumera.challenge.adapter.EndChallengeAdapter
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.challenge.ui.challenge.ChallengeViewModel
import com.joeware.android.gpulumera.challenge.ui.challenge.PrizeDetailActivity
import com.joeware.android.gpulumera.challenge.ui.challenge.PrizeListActivity
import com.joeware.android.gpulumera.challenge.ui.setting.ProfileActivity
import com.joeware.android.gpulumera.databinding.FragmentChallengeEndBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

class ChallengeEndFragment : BaseFragment() {

    private lateinit var binding: FragmentChallengeEndBinding
    private val viewModel: ChallengeViewModel by viewModel()

    override fun setBinding(inflater: LayoutInflater, container: ViewGroup?): View {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_challenge_end,
            container,
            false
        )
        binding.apply {
            view = this@ChallengeEndFragment
            vm = viewModel

            adapter = EndChallengeAdapter().apply {
//                setHasStableIds(true)
                setOnClickListener(object : EndChallengeAdapter.OnClickListener {
                    override fun onClickCell(challenge: Challenge, pos: Int) {
                        val intent = Intent(activity!!, PrizeDetailActivity::class.java)
                        intent.putExtra("pos", pos)
                        intent.putExtra("challenge", challenge)
                        startActivity(intent)
                    }

                    override fun onClickUser(user: User) {
                        val intent = Intent(activity!!, ProfileActivity::class.java)
                        intent.putExtra("user", user)
                        startActivity(intent)
                    }

                    override fun onClickViewAll(challenge: Challenge) {
                        val intent = Intent(activity!!, PrizeListActivity::class.java)
                        intent.putExtra("challenge", challenge)
                        startActivity(intent)
                    }
                })
            }

            rvList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        viewModel.getEndChallengeList()
                    }
                }
            })

            swipeContainer.setOnRefreshListener {
                viewModel.initEndChallengeList()
            }
        }

        return binding.root
    }

    override fun setObserveData() {
        viewModel.endItems.observe(
            this,
            Observer { items -> binding.adapter?.setItems(items) })
        viewModel.showToastMessage.observe(this, Observer { ToastUtils.showShort(it) })
        viewModel.refreshEnd.observe(this, Observer { binding.swipeContainer.isRefreshing = false })
    }

    override fun init() {
        viewModel.initEndChallengeList()
    }

    companion object {
        @JvmStatic
        fun newInstance() = ChallengeEndFragment()
    }
}