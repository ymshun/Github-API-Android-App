package com.example.githubapikotlinapp.view.adapters

import android.view.View
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.bumptech.glide.Glide

object BindingAdapter {

    // ビューの表示、非表示を切り替えるbinding adapter
    @BindingAdapter("visibleGone")
    @JvmStatic
    fun showHide(view: View, isShow: Boolean) {
        view.visibility = if (isShow) View.VISIBLE else View.GONE
    }


    // GlideでURLから画像をImageViewにロードさせる binding adapter
    @BindingAdapter("imgURL")
    @JvmStatic
    fun setURLImg(imageView: ImageView, url: String?) {
        if (url != null) {
            // loading placeholderの生成
            val circularProgressDrawable = CircularProgressDrawable(imageView.context)
            circularProgressDrawable.strokeWidth = 5f
            circularProgressDrawable.centerRadius = 30f
            circularProgressDrawable.start()

            // glideで画像読み込み
            Glide.with(imageView).load(url)
                .circleCrop()
                .placeholder(circularProgressDrawable)
                .into(imageView)
        }
    }
}