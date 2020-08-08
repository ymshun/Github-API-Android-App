package com.example.githubapikotlinapp.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.example.githubapikotlinapp.service.model.*
import com.example.githubapikotlinapp.service.repository.ContributorRepository
import kotlinx.coroutines.launch

class ContributorDetailViewModel(
    application: Application,
    private val context: Context,
    private val user: String
) : AndroidViewModel(application) {

    // retrofit API管理のインスタンス取得
    private val repository = ContributorRepository.instance

    // すべてを含めたContributorの詳細情報のLive Data
    val detailInfoListLiveData: MutableLiveData<List<DetailInfo>> = MutableLiveData()
    private val detailInfoList: MutableList<DetailInfo> = mutableListOf()


    // 初期化と同時にリクエスト送信、live data更新
    init {
        loadContributor()   // contributor詳細情報
        loadOrganization()  // organization詳細情報
        loadFollowerList()  //follower リスト
    }


    /**
     * APIにリクエストを送り、contributorの詳細情報を取得
     * **/
    private fun loadContributor() {
        viewModelScope.launch {
            try {
                val request = repository.getContributorDetails(user)
                if (request.isSuccessful) {
                    // APIの結果を取得しlive data に通知
//                    contributorDetailLiveData.postValue(request.body())
                    // epoxyRecyclerViewにリストデータとして入れるために整形
                    request.body()?.let { response ->
                        addDetailInfo(response)
                    }
                    // Live Data に通知
                    detailInfoListLiveData.postValue(detailInfoList)

                } else {
                    Log.d("test00", request.message())
                    Toast.makeText(context, "ERROR: ${request.message()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.d("test00", e.message)
                Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    /**
     * API にリクエストを送り、contributorのFollowerリストを取得
     * **/
    private fun loadFollowerList() {
        viewModelScope.launch {
            try {
                val request = repository.getFollowersList(user)
                if (request.isSuccessful) {
                    // APIの結果を取得しlive data に通知
//                    followersListLiveData.postValue(request.body())

                    // epoxyRecyclerViewにリストデータとして入れるために整形
                    request.body()?.let { response ->
                        addFollowerList(response)
                    }
                    //Live Data に通知
                    detailInfoListLiveData.postValue(detailInfoList)
                    Log.d("test01", request.message())
                } else {
                    Toast.makeText(context, "ERROR: ${request.message()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.d("test01", e.message)
                Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    /**
     * API にリクエストを送り、contributorのorganizationの詳細情報を取得
     * **/
    private fun loadOrganization() {
        viewModelScope.launch {
            try {
                val request = repository.getOrganization(user)
                if (request.isSuccessful) {
                    // APIの結果を取得しlive data に通知
                    // epoxyRecyclerViewにリストデータとして入れるために整形
                    request.body()?.let { response ->
                        // organizationはリストなので一つ取り出す
                        if (response.isNotEmpty()) addOrganization(response[0])
                    }
                    //Live Data に通知
                    detailInfoListLiveData.postValue(detailInfoList)
                    Log.d("test02", request.message())
                } else {
                    Toast.makeText(context, "ERROR: ${request.message()}", Toast.LENGTH_LONG).show()
                }

            } catch (e: Exception) {
                Log.d("test02", e.message)
                Toast.makeText(context, "ERROR: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }


    // Epoxy でリストのデータとして使えるように整形 (contributorの詳細データ、画像無し)
    private fun addDetailInfo(response: ContributorDetail) {
        detailInfoList.add(
            DetailInfo(
                "Account Name",
                listOf(
                    EpoxyContent(
                        response.login,
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "Name",
                listOf(
                    EpoxyContent(
                        response.name,
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "Location",
                listOf(
                    EpoxyContent(
                        response.location,
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "Company",
                listOf(
                    EpoxyContent(
                        response.company,
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "GitHub",
                listOf(
                    EpoxyContent(
                        response.html_url,
                        null,
                        true,
                        response.html_url
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "フォロワー数",
                listOf(
                    EpoxyContent(
                        "${response.followers}人",
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "フォロワー中",
                listOf(
                    EpoxyContent(
                        "${response.following}人",
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "Public Repository数",
                listOf(
                    EpoxyContent(
                        "${response.public_repos}",
                        null,
                        false,
                        null
                    )
                )
            )
        )
        detailInfoList.add(
            DetailInfo(
                "Public Gist数",
                listOf(
                    EpoxyContent(
                        "${response.public_gists}",
                        null,
                        false,
                        null
                    )
                )
            )
        )
    }


    // Epoxy でリストのデータとして使えるように整形 (フォロワーのリスト、画像あり)
    private fun addFollowerList(response: List<Follower>) {
        val contentListNoImg: MutableList<EpoxyContent> = mutableListOf()
        response.forEach { follower ->
            contentListNoImg.add(
                EpoxyContent(
                    follower.login,
                    follower.avatar_url,
                    true,
                    follower.html_url
                )
            )
        }

        detailInfoList.add(
            DetailInfo(
                "フォロワー",
                contentListNoImg as List<EpoxyContent>
            )
        )
    }


    // Epoxy でリストのデータとして使えるように整形 (フォロワーのリスト、画像あり)
    private fun addOrganization(response: Organization) {
        detailInfoList.add(
            DetailInfo(
                "Organization",
                listOf(
                    EpoxyContent(
                        response.login,
                        response.avatar_url,
                        false,
                        null
                    ),
                    EpoxyContent(
                        response.description,
                        null,
                        false,
                        null
                    )
                )
            )
        )
    }


    // 各変数の(DI)依存性注入ファクトリ
    class Factory(
        private val application: Application,
        private val context: Context,
        private val userName: String
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ContributorDetailViewModel(application, context, userName) as T
        }
    }
}