package com.example.githubapikotlinapp.adapters

import com.airbnb.epoxy.TypedEpoxyController
import com.example.githubapikotlinapp.detailContent
import com.example.githubapikotlinapp.detailHeader

class DetailEpoxyController() : TypedEpoxyController<String>() {

    override fun isStickyHeader(position: Int): Boolean {
        return position == 0
    }

    override fun buildModels(data: String?) {

        for (ii in 0..2) {

            detailHeader {
                /** R.layout.epoxy_cell_detail_headerのビューモデル **/
                id(modelCountBuiltSoFar)
                title("TITLE_TEST")
            }

            for (i in 0..20) {
                detailContent {
                    /** R.layout.epoxy_cell_detail_contentのビューモデル **/
                    id(modelCountBuiltSoFar)
                    content("TEST_CONTENT$i")
                }
            }

        }


    }

}