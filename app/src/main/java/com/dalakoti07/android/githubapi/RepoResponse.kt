package com.dalakoti07.android.githubapi

import com.dalakoti07.android.modelclass.RepoModel
import com.google.gson.annotations.SerializedName

data class RepoResponse(
        @SerializedName("total_count") val total: Int = 0,
        @SerializedName("items") val items: List<RepoModel> = emptyList(),
        val nextPage: Int? = null
)