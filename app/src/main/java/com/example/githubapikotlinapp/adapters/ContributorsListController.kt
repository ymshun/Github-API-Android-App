package com.example.githubapikotlinapp.adapters

import android.content.Context
import android.view.View
import com.airbnb.epoxy.TypedEpoxyController
import com.bumptech.glide.Glide
import com.example.githubapikotlinapp.ContributorsBindingModel_
import com.example.githubapikotlinapp.contributors
import com.example.githubapikotlinapp.fragments.ContributorData
import kotlinx.android.synthetic.main.epoxy_cell_contributors.view.*


/**
 * ホームのContributorリスト(EpoxyRecyclerView)のコントローラー
 *
 * **/

class ContributorsListController(private val context: Context) :
    TypedEpoxyController<MutableList<ContributorData>>() {

    lateinit var listener: OnClickListener

    interface OnClickListener {
        fun setContributorClickListener(position: Int)
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.listener = listener
    }


    override fun buildModels(listData: MutableList<ContributorData>?) {
        listData ?: return

        for (contributor in listData) {
            contributors {

                onBind { model, view, position ->
                    // glideでURL画像の読み込み
                    Glide.with(context).load(contributor.avatarURL).circleCrop()
                        .into(view.dataBinding.root.avatarImg)
                }

                /** R.layout.epoxy_cell_contributorsのビューモデル **/
                id(modelCountBuiltSoFar)
                contributorName(contributor.userName)

                contributorAvatarID(contributor.avatarURL)
                clickListener { model, parentView, clickedView, position ->
                    listener.setContributorClickListener(position)
                }
            }
        }
    }


}