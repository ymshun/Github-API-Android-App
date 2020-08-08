package com.example.githubapikotlinapp.service.model

data class DetailInfo(
    val title: String,     // sticky headerに表示するタイトル
    val contentList: List<EpoxyContent>      //リスト表示するデータ
)

data class EpoxyContent(
    val content: String,     //文字列の情報
    val avatarURL: String?,  // 画像があるときは画像を表示
    val clickable: Boolean,   // タップして遷移先を作るか
    val htmlURL: String?    // chrome custom tabsで表示ささせるページのurl
)