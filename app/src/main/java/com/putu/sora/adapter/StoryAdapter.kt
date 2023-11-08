package com.putu.sora.adapter

import android.content.Intent
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.putu.sora.R
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.databinding.StoryListBinding
import com.putu.sora.extra.setDateFormat
import com.putu.sora.ui.activity.DetailStoryActivity
import com.putu.sora.ui.activity.DetailStoryActivity.Companion.EXTRA_DATA

class StoryAdapter: PagingDataAdapter<StoryEntity, StoryAdapter.ViewHolder>(DIFF_CALLBACK) {

    inner class ViewHolder(private val storyListBinding: StoryListBinding): RecyclerView.ViewHolder(storyListBinding.root) {
        fun bind(story: StoryEntity) {
            storyListBinding.apply {
                Glide.with(itemView)
                    .load(story.photoUrl)
                    .listener(glideListener(storyListBinding))
                    .apply(RequestOptions.overrideOf(1500, 1350).placeholder(R.drawable.bg_image_loading).error(R.drawable.ic_baseline_broken_image))
                    .into(ivPhoto)
                storyListBinding.tvInitialName.text = story.name.substring(0, 1)
                storyListBinding.tvName.text = story.name
                storyListBinding.tvDate.setDateFormat(story.createdAt)
                storyListBinding.tvDescription.text = story.description

                itemView.setOnClickListener {
                    val intent = Intent(itemView.context, DetailStoryActivity::class.java)
                    intent.putExtra(EXTRA_DATA, story)
                    itemView.context.startActivity(intent)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val storyListBinding = StoryListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(storyListBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = getItem(position)
        if (data != null) {
            holder.bind(data)
        }
    }

    private fun glideListener(binding: StoryListBinding): RequestListener<Drawable> {
        return object : RequestListener<Drawable>{
            override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                binding.progressBar.visibility = View.GONE
                return false
            }

            override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                binding.progressBar.visibility = View.GONE
                return false
            }
        }
    }

    companion object {
        val DIFF_CALLBACK = object : DiffUtil.ItemCallback<StoryEntity>() {
            override fun areItemsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: StoryEntity, newItem: StoryEntity): Boolean {
                return oldItem.id == newItem.id
            }
        }
    }
}