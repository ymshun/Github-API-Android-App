package com.example.githubapikotlinapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import com.example.githubapikotlinapp.R
import com.example.githubapikotlinapp.adapters.ContributorsListController
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.fragment_home.*


/**
 * ホームフラグメント(MainActivity起動と同時に遷移)
 * Contributorのリストを表示するフラグメント
 *
 * @param
 *
 * @author Yamashita 2020/7/26
 * **/

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

        /** EpoxyRecyclerViewの生成  **/
        val controller = ContributorsListController()
        contributorsRecyclerView.adapter = controller.adapter
        controller.setData("test")

        //リストのアイテムクリック時
        controller.setOnItemClickListener(object : ContributorsListController.OnClickListener {
            override fun setContributorClickListener(position: Int) {
                Toast.makeText(context!!, "クリック : $position",Toast.LENGTH_SHORT).show()

                //詳細画面へ遷移
                val fragment = ContributorInfoFragment()
                val fm = fragmentManager
                fm?.apply {
                    beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.container, fragment)
                        .commit()
                }
            }

        })

        /** EpoxyRecyclerViewの生成   ---------  END ----------- **/
    }
}