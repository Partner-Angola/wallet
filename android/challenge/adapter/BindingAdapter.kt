package com.joeware.android.gpulumera.challenge.adapter

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.BindingConversion
import com.blankj.utilcode.util.ConvertUtils
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestOptions
import com.joeware.android.gpulumera.R

@BindingConversion
fun boolean2Visibility(visible: Boolean): Int {
    return if (visible) {
        View.VISIBLE
    } else {
        View.GONE
    }
}

@BindingAdapter(value = ["profile"], requireAll = true)
fun bindUserProfile(
    view: ImageView,
    profile: String?
) {
    val resource =
        if (profile.isNullOrEmpty())
            R.drawable.ic_default_profile
        else
            profile

    Glide.with(view.context)
        .load(resource)
        .apply(RequestOptions().transform(CenterCrop(), CircleCrop()))
        .placeholder(R.drawable.ic_default_profile)
        .into(view)
}

@BindingAdapter(value = ["image", "radius"], requireAll = false)
fun bindImage(
    view: ImageView,
    image: String?,
    radius: Float?
) {
    val resource = if (image.isNullOrEmpty())
        R.drawable.ic_placeholder
    else
        image
    val requestOptions = if (radius != null && radius > 0)
        RequestOptions().transform(
            CenterCrop(),
            RoundedCorners(ConvertUtils.dp2px(radius))
        )
    else
        RequestOptions().transform(
            CenterCrop()
        )

    Glide.with(view.context)
        .load(resource)
        .placeholder(R.drawable.transparent)
        .apply(requestOptions)
        .transition(DrawableTransitionOptions().transition(R.anim.fade_in))
        .into(view)
}