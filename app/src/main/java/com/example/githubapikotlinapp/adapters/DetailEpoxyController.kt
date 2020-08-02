package com.example.githubapikotlinapp.adapters

import android.content.Context
import android.view.View
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.airbnb.epoxy.Typed2EpoxyController
import com.bumptech.glide.Glide
import com.example.githubapikotlinapp.DetailHeaderBindingModel_
import com.example.githubapikotlinapp.detailContent1
import com.example.githubapikotlinapp.detailContent2
import com.example.githubapikotlinapp.detailHeader
import com.example.githubapikotlinapp.fragments.DetailList2Data
import com.example.githubapikotlinapp.fragments.DetailListData
import kotlinx.android.synthetic.main.epoxy_cell_detail_content1.view.*
import kotlinx.android.synthetic.main.epoxy_cell_detail_content2.view.*

class DetailEpoxyController(private val context: Context) :
    Typed2EpoxyController<List<DetailListData>, List<DetailList2Data>>() {

    // リスナ(初期化は実装側で)
    lateinit var listener: OnEpoxyClickListener

    interface OnEpoxyClickListener {
        fun setOnEpoxyClickListener(url: String)
    }


    override fun isStickyHeader(position: Int): Boolean {
        val viewModel = this.adapter.getModelAtPosition(position)
        return viewModel is DetailHeaderBindingModel_
    }


    override fun buildModels(
        content1List: List<DetailListData>?,
        content2List: List<DetailList2Data>?
    ) {
        // 文字だけのviewHolder
        content1List?.forEach { item ->
            detailHeader {
                // sticky Headerにする各データのヘッダー
                id(modelCountBuiltSoFar)
                title(item.title)
            }

            item.contentList.forEach { content ->
                detailContent1 {
                    id(modelCountBuiltSoFar)
                    content(content)
                    onBind { model, view, position ->
                        view.dataBinding.root.arrowRightImg.visibility = View.GONE
                        if (item.title == "GitHub") {
                            view.dataBinding.root.arrowRightImg.visibility = View.VISIBLE
                        }
                    }

                    clickListener { view ->
                        if (item.title == "GitHub") {
                            // クリックできるように
                            // chrome Custom Tabsでページを開く
                            listener.setOnEpoxyClickListener(content)
                        }
                    }
                }
            }
        }

        // 文字と画像のviewHolder
        content2List?.forEach { item ->
            detailHeader {
                // sticky Headerにする各データのヘッダー
                id(modelCountBuiltSoFar)
                title(item.title)
            }

            item.content2List.forEach { content2 ->
                detailContent2 {
                    id(modelCountBuiltSoFar)
                    content(content2.name)
                    onBind { model, view, position ->
                        // loading placeholderの生成
                        val circularProgressDrawable = CircularProgressDrawable(context)
                        circularProgressDrawable.strokeWidth = 5f
                        circularProgressDrawable.centerRadius = 30f
                        circularProgressDrawable.start()

                        //画像の読み込み
                        Glide.with(context).load(content2.avatarURL)
                            .placeholder(circularProgressDrawable)
                            .into(view.dataBinding.root.content2Img)
                        view.dataBinding.root.arrowRightImg2.visibility = View.GONE
                        if (item.title == "Followers") {
                            // クリックできるように
                            view.dataBinding.root.arrowRightImg2.visibility = View.VISIBLE

                        }
                    }
                    clickListener { view ->
                        if (item.title == "Followers") {
                            // chrome Custom Tabsでページを開く
                            listener.setOnEpoxyClickListener(content2.url)
                        }
                    }
                }
            }
        }
    }


    // リスナ通知
    fun setEpoxyListener(listener: OnEpoxyClickListener) {
        this.listener = listener
    }
}