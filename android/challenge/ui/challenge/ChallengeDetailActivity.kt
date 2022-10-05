package com.joeware.android.gpulumera.challenge.ui.challenge

import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.blankj.utilcode.util.StringUtils
import com.blankj.utilcode.util.ToastUtils
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.base.BaseActivity
import com.joeware.android.gpulumera.challenge.adapter.ChallengeEntryAdapter
import com.joeware.android.gpulumera.challenge.dialog.PhotoDetailDialog
import com.joeware.android.gpulumera.challenge.dialog.PhotoDetailDialogInterface
import com.joeware.android.gpulumera.challenge.model.Challenge
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.ui.vote.VoteDetailActivity
import com.joeware.android.gpulumera.challenge.ui.vote.VoteListDataUtil
import com.joeware.android.gpulumera.challenge.widget.HorizontalSpaceItemDecoration
import com.joeware.android.gpulumera.challenge.widget.VerticalSpaceItemDecoration
import com.joeware.android.gpulumera.databinding.ActivityChallengeDetailBinding
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class ChallengeDetailActivity : BaseActivity() {

    private lateinit var binding: ActivityChallengeDetailBinding
    private val viewModel: ChallengeDetailViewModel by viewModel()
    private val voteListDataUtil: VoteListDataUtil by inject()

    private val hDecoration = HorizontalSpaceItemDecoration(1, hasHeader = true)
    private val vDecoration = VerticalSpaceItemDecoration(1, hasHeader = true)

    override fun setBinding() {
        binding = DataBindingUtil.setContentView(this, R.layout.activity_challenge_detail)
        binding.apply {
            lifecycleOwner = this@ChallengeDetailActivity
            activity = this@ChallengeDetailActivity
            vm = viewModel

            adapter = ChallengeEntryAdapter().apply {
                setOnClickListener(object : ChallengeEntryAdapter.OnClickListener {
                    override fun onClickItem(pos: Int) {
                        viewModel.items.value?.toCollection(ArrayList())?.let {
                            val intent = Intent(this@ChallengeDetailActivity, VoteDetailActivity::class.java)
                            voteListDataUtil.setChallenge(false, pos - 1, viewModel.challenge, it)
                            startActivity(intent)
                        }
                    }

                    override fun onClickMyEntry(join: Join) {
                        PhotoDetailDialog.showDialog(
                            supportFragmentManager,
                            join,
                            showMedal = false,
                            showInfo = false
                        )
                    }
                })
            }

            rcvList.apply {
                removeItemDecoration(vDecoration)
                addItemDecoration(vDecoration)
                removeItemDecoration(hDecoration)
                addItemDecoration(hDecoration)

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
        viewModel.finishActivity.observe(this, Observer { finish() })
        viewModel.shareChallenge.observe(this, Observer { onShareChallenge() })
        viewModel.items.observe(this, Observer { binding.adapter?.setItems(it) })
        viewModel.myItems.observe(this, Observer {
            binding.leftCount = 3 - it.size
            binding.btnParticipate.text = if (binding.leftCount <= 0)
                StringUtils.getString(R.string.participation_completed)
            else
                String.format(getString(R.string.participate_left), binding.leftCount)

            binding.adapter?.setMyItems(it)
        })
        viewModel.itemCount.observe(this, Observer { binding.adapter?.setAllCount(it) })
    }

    override fun init() {
        val challenge = intent.getParcelableExtra<Challenge>("challenge") as Challenge
        viewModel.challenge = challenge
        binding.adapter?.setChallengeInfo(challenge)

        viewModel.getMyJoinList()
        viewModel.initJoinList()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_PHOTO && resultCode == RESULT_OK) {
            val filePath = data?.getStringExtra("filePath")
            val intent = Intent(
                this@ChallengeDetailActivity,
                ChallengeParticipateActivity::class.java
            )
            intent.putExtra("filePath", filePath)
            intent.putExtra("challenge", viewModel.challenge)
            startActivityForResult(intent, REQUEST_PARTICIPATE)
        }
        if (requestCode == REQUEST_PARTICIPATE && resultCode == RESULT_OK) {
            // 참가성공
            ToastUtils.showShort(R.string.participate_success)
            viewModel.getMyJoinList()
            viewModel.initJoinList()
        }
    }

    private fun onShareChallenge() {
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

    fun onParticipate() {
        val intent = Intent(this, PhotoSelectActivity::class.java)
        startActivityForResult(intent, REQUEST_PHOTO)
    }

    companion object {
        private const val REQUEST_PHOTO = 0x1110
        private const val REQUEST_PARTICIPATE = 0x1111
    }
}