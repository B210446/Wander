package dev.dizzy1021.core.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Review(
    val username: String,
    val rating: Double,
    val desc: String,
    val date: String,
    val placeId: String,
    val placeName: String?,
): Parcelable