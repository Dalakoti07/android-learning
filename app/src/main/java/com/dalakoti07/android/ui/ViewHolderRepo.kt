package com.dalakoti07.android.ui

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.dalakoti07.android.R
import com.dalakoti07.android.modelclass.RepoModel

class ViewHolderRepo(view: View) : RecyclerView.ViewHolder(view) {
    private val name: TextView = view.findViewById(R.id.repo_name)
    private val description: TextView = view.findViewById(R.id.repo_description)
    private val stars: TextView = view.findViewById(R.id.repo_stars)
    private val language: TextView = view.findViewById(R.id.repo_language)
    private val forks: TextView = view.findViewById(R.id.repo_forks)

    private var repo: RepoModel? = null

    init {
        view.setOnClickListener {
            repo?.url?.let { url ->
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                view.context.startActivity(intent)
            }
        }
    }

    fun bind(repo: RepoModel?) {
        if (repo == null) {
            val resources = itemView.resources
            name.text = "loading"
            description.visibility = View.GONE
            language.visibility = View.GONE
            stars.text = "stars"
            forks.text = "fork"
        } else {
            showRepoData(repo)
        }
    }

    private fun showRepoData(repo: RepoModel) {
        this.repo = repo
        name.text = repo.fullName

        // if the description is missing, hide the TextView
        var descriptionVisibility = View.GONE
        if (repo.description != null) {
            description.text = repo.description
            descriptionVisibility = View.VISIBLE
        }
        description.visibility = descriptionVisibility

        stars.text = repo.stars.toString()
        forks.text = repo.forks.toString()

        // if the language is missing, hide the label and the value
        var languageVisibility = View.GONE
        if (!repo.language.isNullOrEmpty()) {
            val resources = this.itemView.context.resources
            language.text = "language"
            languageVisibility = View.VISIBLE
        }
        language.visibility = languageVisibility
    }

    companion object {
        fun create(parent: ViewGroup): ViewHolderRepo {
            val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.recycler_item, parent, false)
            return ViewHolderRepo(view)
        }
    }
}