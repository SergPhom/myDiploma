package ru.netology.nmedia.view

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import ru.netology.nmedia.BuildConfig

fun ImageView.load(
    url: String,
    vararg transforms: BitmapTransformation = emptyArray()
) = Glide.with(this)
    .load(url)
    .timeout(10_000)
    .transform(*transforms)
    .into(this)


fun ImageView.loadAvatar(
    avatarUrl: String,
    vararg transforms: BitmapTransformation = emptyArray()
) = load(avatarUrl,
    CircleCrop(), *transforms)

fun ImageView.loadImage(
    imgUrl: String,
    vararg transforms: BitmapTransformation = emptyArray()
) = load(imgUrl,
    *transforms )
