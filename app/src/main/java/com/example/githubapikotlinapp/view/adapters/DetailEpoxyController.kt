package com.example.githubapikotlinapp.view.adapters

import com.airbnb.epoxy.TypedEpoxyController
import com.example.githubapikotlinapp.DetailHeaderBindingModel_
import com.example.githubapikotlinapp.detailContent
import com.example.githubapikotlinapp.detailHeader
import com.example.githubapikotlinapp.service.model.DetailInfo
import com.example.githubapikotlinapp.view.callback.ContributorDetailClickCallback

class DetailEpoxyController(private val callback: ContributorDetailClickCallback) :
    TypedEpoxyController<List<DetailInfo>>() {


    // stickyヘッダーを実装
    override fun isStickyHeader(position: Int): Boolean {
        val viewModel = this.adapter.getModelAtPosition(position)
        return viewModel is DetailHeaderBindingModel_
    }


    override fun buildModels(
        detailList: List<DetailInfo>?
    ) {
        // 文字だけのviewHolder
        detailList?.forEach { detailInfo ->
            detailHeader {
                // sticky Headerにする各データのヘッダー
                id(modelCountBuiltSoFar)
                title(detailInfo.title)
            }

            detailInfo.contentList.forEach { epoxyContent ->
                detailContent {
                    id(modelCountBuiltSoFar)

                    content(epoxyContent.content)   // 文字コンテンツ

                    if (epoxyContent.avatarURL != null) {
                        // 画像があるときは画像を表示
                        isShowImg(true)     // 画像を表示
                        avatarURL(epoxyContent.avatarURL)   // glideで読み込む
                    }

                    clickable(epoxyContent.clickable)   // クリック可能の時に、矢印表示

                    if (epoxyContent.clickable) {
                        // クリック可能なときに、クリックしてchrome custom tabsでページを表示
                        clickListener { model, parentView, clickedView, position ->
                            if (epoxyContent.htmlURL != null) {
                                callback.onItemClick(epoxyContent.htmlURL)
                            }
                        }
                    }
                }
            }
        }

    }

}