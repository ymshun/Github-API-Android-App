package com.example.githubapikotlinapp.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.githubapikotlinapp.R

/**
 * 選択したContributorの情報を表示するフラグメント
 * HomeFragmentから遷移してくる
 *
 * @param:
 *
 * @author: Yamashita 2020/7/26
 * **/


class ContributorInfoFragment(private val contributorInfo:ContributorData) : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_contributor_info, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

}