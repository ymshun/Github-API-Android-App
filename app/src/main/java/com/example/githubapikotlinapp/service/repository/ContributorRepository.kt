package com.example.githubapikotlinapp.service.repository

import com.example.githubapikotlinapp.service.model.Contributor
import com.example.githubapikotlinapp.service.model.ContributorDetail
import com.example.githubapikotlinapp.service.model.Follower
import com.example.githubapikotlinapp.service.model.Organization
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


/**
 * viewModelのデータプロバイダ
 *
 * viewModelに依存し、retrofitでAPIの呼び出し
 *
 * **/

class ContributorRepository {

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://api.github.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private var gitHubService = retrofit.create(GitHubService::class.java)


    // APIにリクエストを送って、コルーチンでContributorの一覧のレスポンスを受け取る
    suspend fun getContributorList(
        projectName: String,
        repositoryName: String
    ): Response<List<Contributor>> =
        gitHubService.getContributorsList(projectName, repositoryName)


    // APIにリクエストを送って、コルーチンでContributorの詳細データのレスポンスを受け取る
    suspend fun getContributorDetails(user: String): Response<ContributorDetail> =
        gitHubService.getContributorDetails(user)


    // APIにリクエストを送って、コルーチンでContributorのFollower一覧のレスポンスを受け取る
    suspend fun getFollowersList(user: String): Response<List<Follower>> =
        gitHubService.getFollowersList(user)


    suspend fun getOrganization(user: String): Response<List<Organization>> =
        gitHubService.getOrganization(user)


    // シングルトンでContributroRepositoryのインスタンスを取得できるように
    companion object Factory {
        val instance: ContributorRepository
            @Synchronized get() {
                return ContributorRepository()
            }
    }
}