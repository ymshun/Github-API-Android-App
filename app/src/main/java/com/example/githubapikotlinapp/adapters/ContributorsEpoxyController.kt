package com.example.githubapikotlinapp.adapters

import android.content.Context
import android.view.View
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.Glide
import com.example.githubapikotlinapp.contributors
import com.example.githubapikotlinapp.fragments.ContributorData
import kotlinx.android.synthetic.main.epoxy_cell_contributors.view.*


/**
 * ホームのContributorリスト(EpoxyRecyclerView)のコントローラー
 *
 * **/

class ContributorsEpoxyController(private val context: Context) :
    TypedEpoxyController<MutableList<ContributorData>>() {

    lateinit var listener: OnClickListener

    interface OnClickListener {
        fun setContributorClickListener(
            position: Int,
            clickedView: View
        )
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.listener = listener
    }


    override fun buildModels(listData: MutableList<ContributorData>?) {
        listData ?: return

        // loading placeholderの生成
        val circularProgressDrawable = CircularProgressDrawable(context)
        circularProgressDrawable.strokeWidth = 5f
        circularProgressDrawable.centerRadius = 30f
        circularProgressDrawable.start()

        for (contributor in listData) {
            contributors {
                /** R.layout.epoxy_cell_contributorsのビューモデル **/
                // viewの transitionNameにセットする一意のIDを生成
                val uniqueTransName = "cardViewTransName$modelCountBuiltSoFar"

                onBind { model, view, position ->
                    // glideでURL画像の読み込み
                    Glide.with(context).load(contributor.avatarURL).circleCrop().placeholder(circularProgressDrawable)
                        .into(view.dataBinding.root.avatarImg)
                }

                id(modelCountBuiltSoFar)
                // 一意なtransitionName属性を設定
                cardViewTransName(uniqueTransName)

                contributorName(contributor.userLoginName)

                clickListener { model, parentView, clickedView, position ->
                    // クリックして詳細画面へ
                    listener.setContributorClickListener(
                        position,
                        clickedView
                    )
                }
            }
        }
    }


}