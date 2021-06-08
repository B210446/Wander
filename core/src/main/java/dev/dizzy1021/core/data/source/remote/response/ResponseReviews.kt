package dev.dizzy1021.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseReviews(

    @field:SerializedName("date")
    val date: String,

    @field:SerializedName("rating")
    val rating: Double,

    @field:SerializedName("place_id")
    val placeId: String,

    @field:SerializedName("place_name")
    val placeName: String?,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("desc")
    val desc: String

): Parcelable
