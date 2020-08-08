package com.example.githubapikotlinapp.service.model


data class ContributorDetail(
    val login: String,  // contributorのログインネーム
    val name: String,    // ユーザーの詳細情報取得API
    val html_url: String,      // アバター画像URL
    val company: String,          // ユーザーのgithubページのURL
    val location: String,       // フォロワー情報取得API
    val public_repos: Int,   //organizationの情報取得API
    val public_gists: Int,          // contributions
    val followers: Int,
    val following: Int
)