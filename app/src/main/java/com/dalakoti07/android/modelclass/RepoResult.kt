package com.dalakoti07.android.modelclass

import androidx.lifecycle.LiveData
import androidx.paging.PagedList

data class RepoResult(
        val data: LiveData<PagedList<RepoModel>>,
        val networkErrors: LiveData<String>
)