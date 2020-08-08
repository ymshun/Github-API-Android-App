package com.example.githubapikotlinapp.service.model

data class Organization(
    val login: String,          // organization の名前
    val avatar_url: String,     // organizationのアバター画像
    val description: String     // organizationのdescription
)