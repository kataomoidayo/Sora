package com.putu.sora.ui.activity

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.bumptech.glide.Glide
import com.putu.sora.R
import com.putu.sora.data.database.StoryEntity
import com.putu.sora.databinding.ActivityDetailStoryBinding
import com.putu.sora.extra.setDateFormat
import java.io.IOException
import java.util.*

@Suppress("DEPRECATION")
class DetailStoryActivity : AppCompatActivity() {

    private var _detailBind : ActivityDetailStoryBinding? = null
    private val detailBind get() = _detailBind


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _detailBind = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(detailBind?.root)

        val detailStory = intent.getParcelableExtra<StoryEntity>(EXTRA_DATA) as StoryEntity
        detailBind?.apply {
            Glide.with(this@DetailStoryActivity)
                .load(detailStory.photoUrl)
                .error(R.drawable.ic_baseline_broken_image_medium)
                .into(ivPhoto)

            tvName.text = detailStory.name
            tvDescription.text = detailStory.description
            tvDate.setDateFormat(detailStory.createdAt)

            try {
                if (detailStory.lat != null && detailStory.lon != null && detailStory.lat >= -90 && detailStory.lat <= 90 && detailStory.lon >= -180 && detailStory.lon <= 180) {
                    val geocoder = Geocoder(this@DetailStoryActivity, Locale.getDefault())
                    val list = geocoder.getFromLocation(detailStory.lat, detailStory.lon, 1)

                    if ((list != null) && (list.size != 0)) {
                        tvLocation.text = list[0].getAddressLine(0)

                        ivLocation.visibility = View.VISIBLE
                        tvLocation.visibility = View.VISIBLE
                    }
                }
            } catch (exception: IOException) {
                exception.printStackTrace()
            }
        }

        supportActionBar?.title = detailStory.name
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }

    companion object {
        const val EXTRA_DATA = "Extra_Data"
    }
}