package com.example.githubapikotlinapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.RequestGitHubAPI
import com.example.githubapikotlinapp.adapters.ContributorsListController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*
import org.json.JSONArray
import org.json.JSONObject


/**
 * ホームフラグメント(MainActivity起動と同時に遷移)
 * Contributorのリストを表示するフラグメント
 *
 * @param
 *
 * @author Yamashita 2020/7/26
 * **/

data class ContributorData(
    val userName: String,
    val avatarURL: String,
    val gitHubURL: String,
    val organizationAPI_URL: String?,
    val contributions :Int
)

class HomeFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        /** APIにリクエストを送り、取得結果をviewに反映 **/
        val requestGitHubAPI = RequestGitHubAPI()
        val contributorsList: MutableList<ContributorData> = mutableListOf()
        requestGitHubAPI.setRequestAPIListener(object : RequestGitHubAPI.OnRequestAPI {
            override fun onPreRequestAPI() {
                // API リクエスト送る前
                apiProgressBar.visibility = View.VISIBLE
            }

            override fun onSucceedRequestAPI(jsonArray: JSONArray) {
                // APIリクエスト成功時
                for (i in 0 until jsonArray.length()) {
                    val jsonObject = JSONObject(jsonArray[i].toString())
                    contributorsList.add(
                        jsonObject.run {
                            ContributorData(
                                getString("login"),
                                getString("avatar_url"),
                                getString("url"),
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

        /** APIにリクエストを送り、取得結果をviewに反映   ------------- END ----------  **/
    }


    /** EpoxyRecyclerViewの生成
     * @param listData : EpoxyRecyclerViewで表示するリストデータ
     * **/
    private fun renderEpoxyRecyclerView(listData: MutableList<ContributorData>){
        val controller = ContributorsListController(context!!)
        contributorsRecyclerView.adapter = controller.adapter
        controller.setData(listData)

        //リストのアイテムクリック時
        controller.setOnItemClickListener(object : ContributorsListController.OnClickListener {
            override fun setContributorClickListener(position: Int) {
                Toast.makeText(context!!, "クリック : $position", Toast.LENGTH_SHORT).show()

                //詳細画面へ遷移
                val fragment = ContributorInfoFragment(listData[position])
                val fm = fragmentManager
                fm?.apply {
                    beginTransaction()
                        .addToBackStack(null)
                        .hide(this@HomeFragment)   // replaceすると戻ってきたときに再びAPIリクを送ってしまう
                        .add(R.id.container,fragment)
                        .commit()
                }
            }

        })
    }
}