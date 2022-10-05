package com.joeware.android.gpulumera.challenge.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.joeware.android.gpulumera.challenge.model.Join
import com.joeware.android.gpulumera.challenge.model.User
import com.joeware.android.gpulumera.databinding.ItemVoteBinding

class VotePagerAdapter(private val medalShow: Boolean = false) : PagerAdapter() {
    private var voteList = listOf<Join>()

    fun setItems(items: List<Join>) {
        voteList = items
        notifyDataSetChanged()
    }

    fun getItems(): List<Join> {
        return voteList
    }

    fun getVote(pos: Int): Join? {
        return if (voteList.size > pos) voteList[pos] else null
    }

    fun toggleVote(pos: Int) {
        if (pos >= voteList.size) {
            return
        }
        voteList[pos].voted = !voteList[pos].voted
    }

    override fun getCount(): Int {
        return voteList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view === `object`
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val context = container.context
        val binding = ItemVoteBinding.inflate(LayoutInflater.from(context))
        val join = voteList[position]

        binding.apply {
            item = join
            ivMedal.visibility = if (position == 0 && medalShow) View.VISIBLE else View.GONE

            ivProfile.setOnClickListener {
                clickListener?.onClickVisitProfile(join.user)
            }
            tvNickname.setOnClickListener {
                clickListener?.onClickVisitProfile(join.user)
            }
            btnVisitProfile.setOnClickListener {
                clickListener?.onClickVisitProfile(join.user)
            }
        }

        container.addView(binding.root)
        return binding.root
    }

    /************************************************************************************
     * 클릭 리스너
     ***********************************************************************************/
    interface OnClickListener {
        fun onClickVisitProfile(user: User)
    }

    private var clickListener: OnClickListener? = null

    fun setOnClickListener(listener: OnClickListener) = run { this.clickListener = listener }
}