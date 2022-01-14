package com.joeware.android.gpulumera.reward.ui.wallet.password.create

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.joeware.android.gpulumera.R
import com.joeware.android.gpulumera.databinding.ItemWalletCreateExplainBinding

class WalletCreateExplainAdapter : RecyclerView.Adapter<WalletCreateExplainAdapter.WalletCreateExplainViewHolder>() {

    data class WalletExplain(
        val title: String,
        val contents: String,
        val imagesRes: Int,
        val contentsColor: Int
    )

    private val items = listOf(
        WalletExplain("Hello,\n앙골라 지갑", "시작할 때 꼭 알아야 할\n앙골라 지갑 사용 방법을 안내합니다.", R.drawable.point_img_intro_1, Color.parseColor("#99454545")),
        WalletExplain("지갑\n만들기", "비밀번호를 생성하고, Seed를 만들면\n앙골라 지갑이 만들어져요.", R.drawable.point_img_intro_2, Color.parseColor("#99454545")),
        WalletExplain("지갑\n가져오기", "Seed만 가지고 있으면\n어디서든 지갑을 가져올 수 있어요.", R.drawable.point_img_intro_3, Color.parseColor("#99454545")),
        WalletExplain("Seed를\n꼭 백업해 주세요.", "Seed를 복사하여 안전하게 보관해 주세요.\nSeed 분실 시, 재발급이 불가해요.", R.drawable.point_img_intro_4, Color.parseColor("#FF752F"))
    )

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WalletCreateExplainAdapter.WalletCreateExplainViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WalletCreateExplainViewHolder(ItemWalletCreateExplainBinding.inflate(inflater, parent, false))
    }

    override fun onBindViewHolder(holder: WalletCreateExplainAdapter.WalletCreateExplainViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class WalletCreateExplainViewHolder(val binding: ItemWalletCreateExplainBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WalletExplain) {
            val context = binding.root.context
            binding.tvTitle.text = item.title
            binding.tvContents.text = item.contents
            binding.tvContents.setTextColor(item.contentsColor)
            Glide.with(context).load(item.imagesRes).into(binding.ivIcon)
        }
    }
}