package dev.dizzy1021.core.data.source.remote.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ResponsePlace(

	@field:SerializedName("is_favorite")
	val isFavorite: Boolean,

	@field:SerializedName("is_reviewed")
	val isReviewed: Boolean,

	@field:SerializedName("image_path")
	val imagePath: List<Poster>,

	@field:SerializedName("lat")
	val lat: Double,

	@field:SerializedName("name")
	val name: String,

	@field:SerializedName("open_link")
	val openLink: String,

	@field:SerializedName("rating")
	val rating: Double,

	@field:SerializedName("top_reviews")
	val topReviews: List<ResponseReviews>? = null,

	@field:SerializedName("description")
	val description: String? = null,

	@field:SerializedName("location")
	val location: String,

	@field:SerializedName("id")
	val id: String,

	@field:SerializedName("lng")
	val lng: Double
) : Parcelable

