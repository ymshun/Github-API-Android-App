package com.example.githubapikotlinapp.service.model

import java.io.Serializable

/**
 * HomeFragmentのリストで表示するContributorデータ
 *
 * **/

data class Contributor(
    val login: String,  // contributorのログインネーム
    val avatar_url: String,      // アバター画像URL
    val url: String,    // ユーザーの詳細情報取得API
    val html_url: String,          // ユーザーのgithubページのURL
    val followers_url: String,       // フォロワー情報取得API
    val organizations_url: String,   //organizationの情報取得API
    val contributions: Int          // contributions
):Serializable