package dev.dizzy1021.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Place(
    val id: String,
    val name: String,
    val desc: String?,
    val rating: Double,
    val location: String?,
    val longitude: String,
    val latitude: String,
    val poster: String?,
    val website: String,
    val gallery: List<ImagePath>,
    val topReviews: List<Review>?,
    var isFavorite: Boolean = false,
    var isReviewed: Boolean = false,
): Parcelable

@Parcelize
data class ImagePath(
    val url: String?,
    val desc: String?
): Parcelable