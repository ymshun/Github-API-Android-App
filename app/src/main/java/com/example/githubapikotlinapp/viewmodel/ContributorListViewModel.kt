package com.example.githubapikotlinapp.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.example.githubapikotlinapp.service.model.Contributor
import com.example.githubapikotlinapp.service.repository.ContributorRepository
import kotlinx.coroutines.launch

class ContributorListViewModel(
    application: Application,
    private val projectName: String,
    private val repositoryName: String
) : AndroidViewModel(application) {

    //    ContributorRepositoryのインスタンス
    private val repository = ContributorRepository.instance

    // live data
    val contributorListLiveData: MutableLiveData<List<Contributor>> = MutableLiveData()


    //ViewModel初期化時にリクエスト
    init {
        loadContributorList()
    }


    private fun loadContributorList() {
        viewModelScope.launch {
            try {
                val request = repository.getContributorList(projectName, repositoryName)
                if (request.isSuccessful) {
                    // live data に通知
                    contributorListLiveData.postValue(request.body())
                }

            } catch (e: Exception) {

            }
        }
    }


    // 各変数の(DI)依存性注入ファクトリ
    class Factory(
        private val application: Application,
        private val projectName: String,
        private val userName: String
    ) :
        ViewModelProvider.NewInstanceFactory() {

        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return ContributorListViewModel(application, projectName, userName) as T
        }
    }
}