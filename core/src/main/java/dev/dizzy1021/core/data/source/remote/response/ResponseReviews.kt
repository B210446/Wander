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

    @field:SerializedName("link")
    val link: String,

    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("user")
    val user: String,

    @field:SerializedName("place_id")
    val placeId: String,

    @field:SerializedName("username")
    val username: String,

    @field:SerializedName("desc")
    val desc: String

): Parcelable
