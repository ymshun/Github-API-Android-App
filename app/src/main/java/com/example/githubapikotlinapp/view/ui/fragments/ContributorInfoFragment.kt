package com.example.githubapikotlinapp.view.ui.fragments

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.core.view.doOnPreDraw
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.databinding.FragmentContributorInfoBinding
import com.example.githubapikotlinapp.view.adapters.DetailEpoxyController
import com.example.githubapikotlinapp.view.callback.ContributorDetailClickCallback
import com.example.githubapikotlinapp.viewmodel.ContributorDetailViewModel
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_contributor_info.*

/**
 * 選択したContributorの情報を表示するフラグメント
 * HomeFragmentから遷移してくる
 * Safe argsでクリックしたcontributorのinfoを受け取る
 *
 * @author: Yamashita 2020/7/26
 * **/

class ContributorInfoFragment : Fragment() {


    // safe argsでデータを受け取る
    private val safeArgs by navArgs<ContributorInfoFragmentArgs>()


    // viewModelの初期化
    private val viewModel by lazy {
        ViewModelProvider(
            this,
            ContributorDetailViewModel.Factory(
                requireActivity().application,
                requireContext(),
                safeArgs.contributorInfo.login
            )
        ).get(ContributorDetailViewModel::class.java)
    }

    // データバインディング
    private var binding: FragmentContributorInfoBinding? = null


    // epoxyRecyclerViewのコントローラー
    private var epoxyAdapter: DetailEpoxyController? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        // binding初期化
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_contributor_info, container, false)

        // epoxy controller のインスタンス
        epoxyAdapter = DetailEpoxyController(object : ContributorDetailClickCallback {
            override fun onItemClick(url: String) {
                // クリックでchrome custom tabsの表示
                showCustomTabs(url)
            }

        })

        // ビューのセット
        binding?.apply {
            isShow = true       // progress bar
            contributorInfoRecyclerView.adapter = epoxyAdapter?.adapter     // epoxyのアダプタをセット
            contributorInfoRecyclerView.layoutManager =
                StickyHeaderLinearLayoutManager(requireContext())   // sticky header
            avatarURL = safeArgs.contributorInfo.avatar_url      // アバター画像の読み込み
            title = safeArgs.contributorInfo.login  //toolbarのタイトル

        }
        return binding?.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // toolbarの設定、戻るボタンの表示
        setHasOptionsMenu(true)
        val activity = activity as AppCompatActivity
        activity.setSupportActionBar(toolbar)
        activity.supportActionBar!!.apply {
            setDisplayHomeAsUpEnabled(true)
            setHomeButtonEnabled(true)
        }

        // 画面のレンダリングを待ってから共有要素遷移(遷移先のフラグメントから戻ってきたときに有効)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        // 共有要素に関して一意な遷移前の画面とセットなtransitionName属性をセットする
        binding!!.transitionName = safeArgs.transitionNames

        // Container Transformの設定
        val transform = MaterialContainerTransform().apply {
            duration = 475
//            duration = 2000  // デバッグ
//            drawingViewId = R.id.nav_host_fragment
//            isDrawDebugEnabled = true
        }
        sharedElementEnterTransition = transform
    }

    // 戻る矢印クリック
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // toolbarの戻るボタンクリック時
                activity?.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        //view modelの監視
        observeViewModel(viewModel)
    }

    //view modelの監視
    private fun observeViewModel(viewModel: ContributorDetailViewModel) {
        viewModel.detailInfoListLiveData.observe(viewLifecycleOwner, Observer { detailInfoList ->
            if (detailInfoList != null) {
                binding?.isShow = false     // progress bar
                epoxyAdapter?.setData(detailInfoList)    // epoxyの更新
            }
        })
    }


    /** chrome Custom Tabsの起動 **/
    private fun showCustomTabs(url: String) {
        val intent = CustomTabsIntent.Builder()
            .setShowTitle(true)
            .setToolbarColor(resources.getColor(R.color.colorPrimary))
            .build()

        intent.intent.setPackage("com.android.chrome")
        intent.launchUrl(requireContext(), Uri.parse(url))
    }
}