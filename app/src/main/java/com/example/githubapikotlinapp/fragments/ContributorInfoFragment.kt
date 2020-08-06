package com.example.githubapikotlinapp.fragments

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import androidx.navigation.fragment.navArgs
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.airbnb.epoxy.stickyheader.StickyHeaderLinearLayoutManager
import com.bumptech.glide.Glide
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.adapters.DetailEpoxyController
import com.example.githubapikotlinapp.api.GetDetailInfoAPI
import com.google.android.material.transition.platform.MaterialContainerTransform
import kotlinx.android.synthetic.main.fragment_contributor_info.*
import org.json.JSONArray
import org.json.JSONObject

/**
 * 選択したContributorの情報を表示するフラグメント
 * HomeFragmentから遷移してくる
 * Safe argsでクリックしたcontributorのinfoを受け取る
 *
 * @author: Yamashita 2020/7/26
 * **/

data class DetailListData(
    val title: String,
    val contentList: List<String>
)

data class DetailList2Data(
    val title: String,
    val content2List: List<FollowerData>
)

data class FollowerData(
    val name: String,
    val avatarURL: String,
    val url: String
)

class ContributorInfoFragment() : Fragment() {

    // safe argsでデータを受け取る
    private val contributorDataArgs by navArgs<ContributorInfoFragmentArgs>()

    private var controller: DetailEpoxyController? = null

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


        // epoxyRecyclerViewの生成
        controller = DetailEpoxyController(requireContext())
        contributorInfoRecyclerView.adapter = controller!!.adapter
        contributorInfoRecyclerView.layoutManager =
            StickyHeaderLinearLayoutManager(requireContext())

        controller!!.setEpoxyListener(object : DetailEpoxyController.OnEpoxyClickListener {

            override fun setOnEpoxyClickListener(url: String) {
                //epoxyクリックで GitHubのブラウザを開く
                showCustomTabs(url)
            }
        })


        /** APIリクエストを送り、結果を取得 **/
        val getDetailInfoAPI = GetDetailInfoAPI()
        // JSONArrayのキーからデータを抽出
        contributorDataArgs.contributorInfo.run {
            // 各APIのエンドポイントをsafe argsから受け取る
            getDetailInfoAPI.getDetailInfo(
                detailUserInfoAPI_URL,
                organizationAPI_URL,
                followersAPI_URL
            )
        }

        // APIを取得し終わったらデータが渡ってくる
        getDetailInfoAPI.setOnListener(object : GetDetailInfoAPI.OnGetAPIListener {
            override fun onGetAPIListener(
                contributorJSONObject: JSONObject?,
                organizationJSONObject: JSONObject?,
                followersJSONArray: JSONArray?
            ) {
                // epoxy にデータを反映
                renderEpoxy(
                    contributorJSONObject,
                    organizationJSONObject,
                    followersJSONArray
                )
            }
        })
        /** APIリクエストを送り、結果を取得  ------- END -----------     **/

        // viewをセット
        Glide.with(requireContext()).load(contributorDataArgs.contributorInfo.avatarURL)
            .into(contributorLgImg)
        toolbar.title = contributorDataArgs.contributorInfo.userLoginName

    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                // toolbarの戻るボタンクリック時
                activity?.onBackPressed()
            }
        }
        return super.onOptionsItemSelected(item)
    }


    /** APIで取得したデータを使ってepoxyRecyclerViewを更新する **/
    private fun renderEpoxy(
        contributorJSONObject: JSONObject?,
        organizationJSONObject: JSONObject?,
        followersJSONArray: JSONArray?
    ) {
        // APIで取得した各データを変数に格納

        // safe argsで受け取ったデータ
        val loginName =
            contributorDataArgs.contributorInfo.userLoginName   // contributorのアカウント名
        val contributions =
            contributorDataArgs.contributorInfo.contributions  // contribution数
        val gitHubURL = contributorDataArgs.contributorInfo.gitHubURL // gitHubのURL

        // contributorJSONObjectから取得したcontributorの詳細データ
        // (前の画面の一覧(HomeFragment)で取得したkey: urlのAPIのエンドポイントを使用して取得したデータ)
        val userName = contributorJSONObject?.getString("name") ?: "No Data"   // contributorsの名前
        val followersNum = contributorJSONObject?.getInt("followers") ?: 0   // follower数
        val followingNum = contributorJSONObject?.getInt("following") ?: 0   // following数
        val pubRepoNum =
            contributorJSONObject?.getInt("public_repos") ?: 0   // public_repos数
        val pubGistNum =
            contributorJSONObject?.getInt("public_gists") ?: 0   // public_gists数
        val company = contributorJSONObject?.getString("company") ?: "No Data"  // 所属company
        val location = contributorJSONObject?.getString("location") ?: "No Data"  // Location

        // organizationJSONObjectから取得したorganizationの詳細データ
        // (前の画面の一覧(HomeFragment)で取得したkey: organizations_urlのAPIのエンドポイントを使用して取得したデータ)
        val orgLoginName =
            organizationJSONObject?.getString("login") ?: "No Data" // organizationのアカウント名
        val orgAvatarImg =
            organizationJSONObject?.getString("avatar_url") ?: "No Data"  // organizationのアバター画像
        val description = organizationJSONObject?.getString("description")
            ?: "No Data" // organizationのdescription

        // followersJSONArrayから取得したcontributorのfollowersListデータ
        // (前の画面の一覧(HomeFragment)で取得したkey: followers_urlのAPIのエンドポイントを使用して取得したデータ)
        val followersList =
            mutableListOf<FollowerData>()      // JSONArrayから loginNameとアバター画像
        for (i in 0 until (followersJSONArray?.length() ?: 0)) {
            if (followersJSONArray != null) {
                val jsonObject = JSONObject(followersJSONArray[i].toString())
                followersList.add(
                    FollowerData(
                        jsonObject.getString("login"),
                        jsonObject.getString("avatar_url"),
                        jsonObject.getString("html_url")
                    )
                )
            } else {
                continue
            }
        }


        // epoxyに渡すリストデータ(文字だけのもの)
        val detailList = listOf<DetailListData>(
            DetailListData("user name", listOf(userName)),
            DetailListData("account name", listOf(loginName)),
            DetailListData("location", listOf(location)),
            DetailListData("company", listOf(company)),
            DetailListData("contributions", listOf("$contributions")),
            DetailListData("GitHub", listOf(gitHubURL)),
            DetailListData("フォロワー", listOf("$followersNum 人")),
            DetailListData("フォロー", listOf("$followingNum 人")),
            DetailListData("public リポジトリ数", listOf("$pubRepoNum")),
            DetailListData("public gist数", listOf("$pubGistNum")),
            DetailListData("organization", listOf(orgLoginName, description))
        )

        // epoxy に渡すリストデータ(画像と文字)
        val detailList2 = listOf(
            DetailList2Data(
                "organization",
                listOf(FollowerData(orgLoginName, orgAvatarImg, ""))
            ),
            DetailList2Data(
                "Followers",
                followersList
            )
        )

        progressBarDetail?.visibility = View.INVISIBLE
        // epoxyに反映
        controller!!.setData(detailList, detailList2)
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