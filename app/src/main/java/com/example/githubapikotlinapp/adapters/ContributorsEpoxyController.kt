package com.example.githubapikotlinapp.adapters

import android.content.Context
import android.view.View
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

//        for (contributor in listData) {
//            contributors {
//                /** R.layout.epoxy_cell_contributorsのビューモデル **/
//
//                onBind { model, view, position ->
//                    // glideでURL画像の読み込み
////                    Glide.with(context).load(contributor.avatarURL).circleCrop()
////                        .into(view.dataBinding.root.avatarImg)
//                }
//                val avatarID = "avatarImgTransitionName$modelCountBuiltSoFar"
//                val nameID = "contributorTransitionName$modelCountBuiltSoFar"
//                val layoutID = "contributorLayoutTransitionName$modelCountBuiltSoFar"
//
//                id(modelCountBuiltSoFar)
//                avatarImgTransitionName(avatarID)
//                contributorTransitionName(nameID)
//                contributorLayoutTransitionName(layoutID)
//                contributorName(contributor.userName)
//
//                clickListener { model, parentView, clickedView, position ->
//                    listener.setContributorClickListener(
//                        position,
//                        clickedView,
//                        avatarID,
//                        nameID,
//                        layoutID
//                    )
//                }
//            }
//        }

        for (contributor in 0..45) {
            contributors {
                /** R.layout.epoxy_cell_contributorsのビューモデル **/
                // viewの transitionNameにセットする一意のIDを生成
                val uniqueTransName = "cardViewTransName$modelCountBuiltSoFar"

                onBind { model, view, position ->
                    // glideでURL画像の読み込み
//                    Glide.with(context).load(contributor.avatarURL).circleCrop()
//                        .into(view.dataBinding.root.avatarImg)
                    Glide.with(context).load("https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcTRPqzYzWvyplA5Bf-ZZgCWyUeQw36uxO0JOQ&usqp=CAU").circleCrop()
                        .into(view.dataBinding.root.avatarImg)
                }

                id(modelCountBuiltSoFar)
                // 一意なtransitionName属性を設定
                cardViewTransName(uniqueTransName)

                contributorName("USER NAME")

                clickListener { model, parentView, clickedView, position ->
                    listener.setContributorClickListener(
                        position,
                        clickedView
                    )
                }
            }
        }
    }


}