package com.example.githubapikotlinapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.doOnPreDraw
import androidx.navigation.findNavController
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.api.GetContributorListAPI
import com.example.githubapikotlinapp.adapters.ContributorsEpoxyController
import com.google.android.material.transition.platform.MaterialElevationScale
import kotlinx.android.synthetic.main.epoxy_cell_contributors.view.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.Serializable


/**
 * ホームフラグメント(MainActivity起動と同時に遷移)
 * Contributorのリストを表示するフラグメント
 *
 * @author Yamashita 2020/7/26
 * **/

data class ContributorData(
    val userLoginName: String,  // contributorのログインネーム
    val avatarURL: String,      // アバター画像URL
    val detailUserInfoAPI_URL:String,    // ユーザーの詳細情報取得API
    val gitHubURL: String,          // ユーザーのgithubページのURL
    val followersAPI_URL :String,       // フォロワー情報取得API
    val organizationAPI_URL: String,   //organizationの情報取得API
    val contributions: Int          // contributions
) : Serializable


class HomeFragment : Fragment() {
    private var contributorsList: MutableList<ContributorData> = mutableListOf()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // 画面のレンダリングを待ってから共有要素遷移(遷移先のフラグメントから戻ってきたときに有効)
        postponeEnterTransition()
        view.doOnPreDraw { startPostponedEnterTransition() }

        if (contributorsList == mutableListOf<ContributorData>()) {
//        if (false) {
            // 起動時のみにAPIリクエスト送信
            /** APIにリクエストを送り、取得結果をviewに反映 **/
            val requestGitHubAPI =
                GetContributorListAPI("https://api.github.com/repos/googlesamples/android-architecture-components/contributors")
            requestGitHubAPI.setRequestAPIListener(object : GetContributorListAPI.OnRequestAPI {
                override fun onPreRequestAPI() {
                    // API リクエスト送る前
                    apiProgressBar.visibility = View.VISIBLE
                }

                override fun onSucceedRequestAPI(jsonArray: JSONArray) {
                    // APIリクエスト成功時
                    for (i in 0 until jsonArray.length()) {
                        val jsonObject = JSONObject(jsonArray[i].toString())
                        // JSONArrayのキーからデータを抽出
                        contributorsList.add(
                            jsonObject.run {
                                ContributorData(
                                    getString("login"),
                                    getString("avatar_url"),
                                    getString("url"),
                                    getString("html_url"),
                                    getString("followers_url"),
                                    getString("organizations_url"),
                                    getInt("contributions")
                                )
                            }
                        )
                    }

                    apiProgressBar.visibility = View.GONE  //プログレス非表示
                    /** APIの取得が完了したらEpoxyRecyclerViewを生成する **/
                    renderEpoxyRecyclerView(contributorsList)
                }

                override fun onFailedRequestAPI(errorCode: Int, errorMsg: String) {
                    // APIリクエスト失敗時
                    Toast.makeText(
                        context,
                        "ERROR_CODE: $errorCode ; \n ERROR: $errorMsg",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
            requestGitHubAPI.execute()
        } else {
            // バックスタックからの復元時
            apiProgressBar.visibility = View.GONE  //プログレス非表示
            /** APIの取得が完了したらEpoxyRecyclerViewを生成する **/
            renderEpoxyRecyclerView(contributorsList)
        }

        /** APIにリクエストを送り、取得結果をviewに反映   ------------- END ----------  **/
    }


    /** EpoxyRecyclerViewの生成
     * @param listData : EpoxyRecyclerViewで表示するリストデータ
     * **/
    private fun renderEpoxyRecyclerView(listData: MutableList<ContributorData>) {

        val controller = ContributorsEpoxyController(requireContext())
        contributorsRecyclerView.adapter = controller.adapter
        controller.setData(listData)

        //リストのアイテムクリック時
        controller.setOnItemClickListener(object : ContributorsEpoxyController.OnClickListener {

            // クリックでNavigation使って画面遷移
            override fun setContributorClickListener(
                position: Int,
                clickedView: View
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
                        listData[position],
                        clickedView.cardView.transitionName
                    )
                // 共有要素
                val extras = FragmentNavigatorExtras(
                    clickedView.cardView to clickedView.cardView.transitionName
                )
                // navControllerで共有要素での画面遷移
                navController.navigate(action, extras)
            }
        })
    }

}