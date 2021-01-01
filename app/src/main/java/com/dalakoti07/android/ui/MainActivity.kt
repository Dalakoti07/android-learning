package com.dalakoti07.android.ui

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.RecyclerView
import com.dalakoti07.android.R
import com.dalakoti07.android.datasource.Repository
import com.dalakoti07.android.dbcache.LocalCache
import com.dalakoti07.android.dbcache.RepoDao
import com.dalakoti07.android.dbcache.RepoDb
import com.dalakoti07.android.githubapi.GithubService
import com.dalakoti07.android.modelclass.RepoModel
import com.dalakoti07.android.ui.viewmodel.ViewModelFactory
import com.dalakoti07.android.ui.viewmodel.ViewModelSearch
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: ViewModelSearch
    private val adapter = AdapterRepo()
    private lateinit var list:RecyclerView
    private lateinit var search_repo: EditText
    private lateinit var emptyList: TextView
    private lateinit var viewModelFactory: ViewModelFactory
    private lateinit var githubService: GithubService
    private lateinit var localCache: LocalCache
    private lateinit var repoDao: RepoDao
    private lateinit var repository: Repository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        repoDao=RepoDb.getInstance(this).reposDao()
        githubService= GithubService.create()
        localCache=LocalCache(repoDao,Executors.newCachedThreadPool())
        repository= Repository(githubService,localCache)
        viewModelFactory= ViewModelFactory(repository)
        // get the view model
        viewModel = ViewModelProviders.of(this, viewModelFactory)
                .get(ViewModelSearch::class.java)

        // add dividers between RecyclerView's row items
        val decoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL)
        list=findViewById<RecyclerView>(R.id.list)
        emptyList=findViewById<TextView>(R.id.emptyList)
        search_repo=findViewById(R.id.search_repo)
        list.addItemDecoration(decoration)

        initAdapter()
        val query = savedInstanceState?.getString(LAST_SEARCH_QUERY) ?: DEFAULT_QUERY
        viewModel.searchRepo(query)
        initSearch(query)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(LAST_SEARCH_QUERY, viewModel.lastQueryValue())
    }

    private fun initAdapter() {
        list.adapter = adapter
        viewModel.repos.observe(this, Observer<PagedList<RepoModel>> {
            Log.d("Activity", "list: ${it?.size}")
            showEmptyList(it?.size == 0)
            adapter.submitList(it)
        })
        viewModel.networkErrors.observe(this, Observer<String> {
            Toast.makeText(this, "\uD83D\uDE28 Wooops $it", Toast.LENGTH_LONG).show()
        })
    }

    private fun initSearch(query: String) {
        search_repo.setText(query)

        search_repo.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
        search_repo.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateRepoListFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updateRepoListFromInput() {
        search_repo.text.trim().let {
            if (it.isNotEmpty()) {
                Log.d(Companion.TAG,"searching "+it.toString())
                list.scrollToPosition(0)
                viewModel.searchRepo(it.toString())
                adapter.submitList(null)
            }
        }
    }

    private fun showEmptyList(show: Boolean) {
        if (show) {
            emptyList.visibility = View.VISIBLE
            list.visibility = View.GONE
        } else {
            emptyList.visibility = View.GONE
            list.visibility = View.VISIBLE
        }
    }

    companion object {
        private const val LAST_SEARCH_QUERY: String = "last_search_query"
        private const val DEFAULT_QUERY = "Android"
        private const val TAG = "MainActivity"
    }
}