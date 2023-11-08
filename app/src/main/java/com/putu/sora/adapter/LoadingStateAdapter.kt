package com.putu.sora.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.putu.sora.databinding.ItemLoadingBinding

class LoadingStateAdapter(private val retry: () -> Unit): LoadStateAdapter<LoadingStateAdapter.ViewHolder>() {

    inner class ViewHolder(private val itemLoadingBinding: ItemLoadingBinding, retry: () -> Unit): RecyclerView.ViewHolder(itemLoadingBinding.root) {
        init {
            itemLoadingBinding.retryBtn.setOnClickListener {
                retry.invoke()
            }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                itemLoadingBinding.errorMessage.text = loadState.error.localizedMessage
            }

            itemLoadingBinding.apply {
                errorMessage.isVisible = loadState is LoadState.Error
                progressBar.isVisible = loadState is LoadState.Loading
                retryBtn.isVisible = loadState is LoadState.Error
            }
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): ViewHolder {
        val itemLoadingBinding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(itemLoadingBinding, retry)
    }
}