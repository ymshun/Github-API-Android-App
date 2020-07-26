package com.example.githubapikotlinapp.adapters

import com.airbnb.epoxy.TypedEpoxyController
import com.example.githubapikotlinapp.contributors


/**
 * ホームのContributorリスト(EpoxyRecyclerView)のコントローラー
 *
 * **/

class ContributorsListController() : TypedEpoxyController<String>() {

    lateinit var listener: OnClickListener

    interface OnClickListener {
        fun setContributorClickListener(position: Int)
    }

    fun setOnItemClickListener(listener: OnClickListener) {
        this.listener = listener
    }


    override fun buildModels(data: String?) {
        for (i in 0..20) {
            contributors {
                /** R.layout.epoxy_cell_contributorsのビューモデル **/
                id(modelCountBuiltSoFar)
                contributorName("USER NAME: $i")
                clickListener { model, parentView, clickedView, position ->
                    listener.setContributorClickListener(position)
                }
            }
        }
    }

}