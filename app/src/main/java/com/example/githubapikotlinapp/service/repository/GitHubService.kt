package com.example.githubapikotlinapp.service.repository

import com.example.githubapikotlinapp.service.model.Contributor
import com.example.githubapikotlinapp.service.model.ContributorDetail
import com.example.githubapikotlinapp.service.model.Follower
import com.example.githubapikotlinapp.service.model.Organization
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * APIを取り扱うインターフェース
 * **/


interface GitHubService {

    // contributors一覧取得
    @GET("repos/{projectName}/{repositoryName}/contributors")
    suspend fun getContributorsList(
        @Path("projectName") company: String,
        @Path("repositoryName") repositoryName: String
    ): Response<List<Contributor>>


    // contributorの詳細情報取得
    @GET("users/{user}")
    suspend fun getContributorDetails(@Path("user") user: String): Response<ContributorDetail>


    // contributorのFollower一覧(最初の一ページのみ)取得
    @GET("users/{user}/followers")
    suspend fun getFollowersList(@Path("user") user: String):Response<List<Follower>>


    // contributorが所属するorganizationの情報取得
    @GET("users/{user}/orgs")
    suspend fun getOrganization(@Path("user") user : String):Response<List<Organization>>
}