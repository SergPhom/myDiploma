package ru.netology.nmedia.dto

import android.os.Parcelable
import kotlinx.parcelize.Parcelize


@Parcelize
data class UserJob(
    val id: Long,
    val name: String,
    val position: String,
    val start: Long,
    val finish: Long?,
    val link: String?,
) : Parcelable