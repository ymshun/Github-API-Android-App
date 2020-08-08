package com.example.githubapikotlinapp.view.adapters

import com.airbnb.epoxy.TypedEpoxyController
import com.example.githubapikotlinapp.contributors
import com.example.githubapikotlinapp.service.model.Contributor
import com.example.githubapikotlinapp.view.callback.ContributorClickCallback


/**
 * ホームのContributorリスト(EpoxyRecyclerView)のコントローラー
 *
 * **/

class ContributorsEpoxyController(
    private val callback: ContributorClickCallback
) : TypedEpoxyController<List<Contributor>>() {


    override fun buildModels(listData: List<Contributor>?) {
        listData ?: return

        for (contributor in listData) {

            contributors {
                /** R.layout.epoxy_cell_contributorsのビューモデル **/
                // viewの transitionNameにセットする一意のIDを生成
                val uniqueTransName = "cardViewTransName$modelCountBuiltSoFar"

                id(modelCountBuiltSoFar)    // id

                cardViewTransName(uniqueTransName)  // 一意なtransitionName属性を設定
                contributorName(contributor.login)  // ユーザー名
                avatarURL(contributor.avatar_url)   // アバター画像

                clickListener { model, parentView, clickedView, position ->
                    // クリックして詳細画面へ
                    // 実装はHomeFragmentにて
                    callback.onItemClick(position, clickedView)
                }
            }
        }
    }
}