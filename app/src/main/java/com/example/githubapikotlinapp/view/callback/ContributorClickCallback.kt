package com.example.githubapikotlinapp.view.callback

import android.view.View

interface ContributorClickCallback {
    fun onItemClick(position: Int, view: View)
}