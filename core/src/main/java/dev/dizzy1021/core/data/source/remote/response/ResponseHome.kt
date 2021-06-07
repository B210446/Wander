package dev.dizzy1021.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponseHome(

	@field:SerializedName("is_favorite")
	val isFavorite: Boolean,

	@field:SerializedName("lng")
	val lng: Double,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("rating")
	val rating: Double,

	@field:SerializedName("open_link")
	val openLink: String,

	@field:SerializedName("location")
	val location: String? = null,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("poster")
	val poster: Poster? = null,

	@field:SerializedName("lat")
	val lat: Double
) : Parcelable

@Parcelize
data class Poster(

	@field:SerializedName("url")
	val url: String? = null,

	@field:SerializedName("content_description")
	val contentDescription: String? = null
) : Parcelable
