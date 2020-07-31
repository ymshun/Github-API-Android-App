package com.example.githubapikotlinapp.fragments

import android.app.ActionBar
import android.os.Bundle
import android.transition.TransitionInflater
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat.getColor
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.navigation.fragment.navArgs
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.bumptech.glide.Glide
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.adapters.DetailEpoxyController
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_contributor_info.*

/**
 * 選択したContributorの情報を表示するフラグメント
 * HomeFragmentから遷移してくる
 *
 * @param:
 *
 * @author: Yamashita 2020/7/26
 * **/

data class DetailListData(
    val githubURL:String,
    val contribution : Int,
    val organization : OrganizationInfoData,
    val followersList:MutableList<ContributorData>
)

data class OrganizationInfoData(
    val avatarURL :String,
    val descriptions:String,
    val organizationAPI_URL:String,
    val organizationGitHubURL: String?

)


class ContributorInfoFragment() : Fragment() {

    // safe argsでデータを受け取る
    private val contributorDataArgs by navArgs<ContributorInfoFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contributor_info, container, false)
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

        // 共有要素に関して一意な遷移前の画面とセットなtransitionName属性をセットする
        contributorInfoLayout.transitionName = contributorDataArgs.transitionNames

        // Container Transformの設定
        val transform = MaterialContainerTransform().apply {
            duration = 475
//            duration = 2000  // デバッグ
//            drawingViewId = R.id.nav_host_fragment
//            isDrawDebugEnabled =true
        }
        sharedElementEnterTransition = transform

        // viewをセット
        Glide.with(requireContext()).load(contributorDataArgs.contributorInfo.avatarURL)
            .into(contributorLgImg)
        toolbar.title = contributorDataArgs.contributorInfo.userName


        // epoxyRecyclerViewの生成
        val controller = DetailEpoxyController()
        contributorInfoRecyclerView.adapter = controller.adapter
        contributorInfoRecyclerView.layoutManager = StickyHeaderLinearLayoutManager(requireContext())
        controller.setData("")

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home->{
                // toolbarの戻るボタンクリック時
                activity?.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }

}