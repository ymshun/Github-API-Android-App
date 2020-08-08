package com.example.githubapikotlinapp.view.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.databinding.FragmentHomeBinding
import com.example.githubapikotlinapp.service.model.Contributor
import com.example.githubapikotlinapp.view.adapters.ContributorsEpoxyController
import com.example.githubapikotlinapp.view.callback.ContributorClickCallback
import com.example.githubapikotlinapp.viewmodel.ContributorListViewModel
import com.google.android.material.transition.platform.MaterialElevationScale
import kotlinx.android.synthetic.main.epoxy_cell_contributors.view.*


/**
 * ホームフラグメント(MainActivity起動と同時に遷移)
 * Contributorのリストを表示するフラグメント
 *
 * @author Yamashita 2020/7/26
 * **/


class HomeFragment : Fragment() {

    // viewModelの初期化
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ContributorListViewModel.Factory(
                requireActivity().application,
                "googlesamples",
                "android-architecture-components"
            )
        ).get(ContributorListViewModel::class.java)
    }

    // データバインディング
    private var binding: FragmentHomeBinding? = null

    // epoxyRecyclerViewのコントローラー
    private var epoxyAdapter: ContributorsEpoxyController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // バインディング
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false)

        // epoxy controllerのインスタンス
        epoxyAdapter =
            ContributorsEpoxyController(object : ContributorClickCallback {
                // リストのアイテムをクリックしたとき
                override fun onItemClick(position: Int, view: View) {
                    navigateToContributorDetailFragment(
                        view,
                        viewModel.contributorListLiveData.value!![position]
                    )
                }
            })

        // viewの変更
        binding?.apply {
            isShow = true   // loading中ならprogress bar
            contributorsRecyclerView.adapter = epoxyAdapter?.adapter
        }

        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 画面のレンダリングを待ってから共有要素遷移(遷移先のフラグメントから戻ってきたときに有効)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        // viewModelの監視
        observeViewModel(viewModel)
    }


    private fun observeViewModel(viewModel: ContributorListViewModel) {
        viewModel.contributorListLiveData.observe(viewLifecycleOwner, Observer { contributorList ->
            if (contributorList != null) {
                binding?.isShow = false
                epoxyAdapter?.setData(contributorList)    // epoxyの更新
            }
        })
    }


    /**
     * Navigation使って詳細画面に遷移する
     * Motion 使って共有要素アニメーション
     * safe argsでデータの受け渡し
     *
     * **/
    private fun navigateToContributorDetailFragment(
        clickedView: View,
        clickedContributor: Contributor
    ) {
        // アニメーション時に対称コンテナ以外を残したままにする
        exitTransition = MaterialElevationScale(false).apply {
            duration = resources.getInteger(R.integer.duration).toLong()
        }
        reenterTransition = MaterialElevationScale(true).apply {
            duration = resources.getInteger(R.integer.duration).toLong()
        }

        // Navigation使って詳細画面へ遷移、パラメータをsafe argsで送る
        val navController = clickedView.findNavController()

        // safe args クリックしたcontributorの情報とtransitionName属性名
        val action = HomeFragmentDirections
            .actionHomeFragmentToContributorInfoFragment(
                clickedContributor,
                clickedView.cardView.transitionName
            )
        // 共有要素の設定 cardView -> coordinatorLayout
        val extras = FragmentNavigatorExtras(
            clickedView.cardView to clickedView.cardView.transitionName
        )
        // navControllerで共有要素での画面遷移
        navController.navigate(action, extras)
    }
}