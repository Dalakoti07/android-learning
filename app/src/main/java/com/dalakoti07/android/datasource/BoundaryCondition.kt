package com.dalakoti07.android.datasource

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.PagedList
import com.dalakoti07.android.dbcache.LocalCache
import com.dalakoti07.android.githubapi.GithubService
import com.dalakoti07.android.githubapi.searchRepos
import com.dalakoti07.android.modelclass.RepoModel

class BoundaryCondition(
        private val query: String,
        private val service: GithubService,
        private val cache: LocalCache
) : PagedList.BoundaryCallback<RepoModel>() {
    init{
        Log.d(TAG, "created boundary instance ")
    }
    private var lastRequestedPage = 1

    private val _networkErrors = MutableLiveData<String>()

    // LiveData of network errors.
    val networkErrors: LiveData<String>
        get() = _networkErrors

    // avoid triggering multiple requests in the same time
    private var isRequestInProgress = false

    override fun onZeroItemsLoaded() {
        Log.d(TAG, "zero item loaded")
        requestAndSaveData(query)
    }

    override fun onItemAtEndLoaded(itemAtEnd: RepoModel) {
        Log.d(TAG, "last item loaded")
        requestAndSaveData(query)
    }

    companion object {
        private const val NETWORK_PAGE_SIZE = 50
        private const val TAG = "BoundaryCondition"
    }

    private fun requestAndSaveData(query: String) {
        Log.d(TAG, "requesting data and saving it ")
        if (isRequestInProgress) return

        isRequestInProgress = true
        searchRepos(service, query, lastRequestedPage, NETWORK_PAGE_SIZE, { repos ->
            cache.insert(repos) {
                lastRequestedPage++
                isRequestInProgress = false
            }
        }, { error ->
            isRequestInProgress = false
        })
    }
}