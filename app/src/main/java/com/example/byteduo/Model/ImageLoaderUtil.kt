package com.example.byteduo.Model

import android.content.Context
import android.widget.ImageView
import com.bumptech.glide.Glide

//this is so we can use a single function for image loading on the home and cart pages
object ImageLoaderUtil {
    fun loadRoundedImage(itemImageUrl: String, itemImageView: ImageView, context: Context) {
        Glide.with(context)
            .load(itemImageUrl)
            .circleCrop()
            .into(itemImageView)
    }
}