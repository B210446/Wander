package dev.dizzy1021.core.utils

import androidx.paging.LoadStateAdapter
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ConcatAdapter
import androidx.recyclerview.widget.RecyclerView
import dev.dizzy1021.core.data.source.remote.response.Poster
import dev.dizzy1021.core.data.source.remote.response.ResponseHome
import dev.dizzy1021.core.data.source.remote.response.ResponsePlace
import dev.dizzy1021.core.data.source.remote.response.ResponseReviews
import dev.dizzy1021.core.domain.model.ImagePath
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.domain.model.Review

fun List<ResponseHome>.toPlace(): List<Place> =
    this.map {
        Place(
            id = it.id,
            name = it.name,
            desc = null,
            rating = it.rating,
            location = it.location,
            longitude = it.lng.toString(),
            latitude = it.lat.toString(),
            poster = it.poster?.url,
            gallery = emptyList(),
            topReviews = emptyList(),
            isFavorite = it.isFavorite,
            website = it.openLink
        )
    }

fun List<ResponseReviews>.toReViews(): List<Review> =
    this.map {
        Review(
            username = it.username,
            rating = it.rating,
            desc = it.desc,
            date = it.date,
            placeId = it.placeId,
        )
    }

fun List<Poster>.toImagePath(): List<ImagePath> =
    this.map {
        ImagePath(
            url = it.url,
            desc = it.contentDescription
        )
    }

fun ResponsePlace.toPlace(): Place =
    Place(
        id = this.id,
        name = this.name,
        desc = this.description,
        rating = this.rating,
        location = this.location,
        longitude = this.lat.toString(),
        latitude = this.lng.toString(),
        poster = this.imagePath[0].url,
        gallery = this.imagePath.toImagePath(),
        topReviews = this.topReviews?.toReViews(),
        isFavorite = this.isFavorite,
        website = this.openLink
    )

fun <T : Any, V : RecyclerView.ViewHolder> PagingDataAdapter<T, V>.withLoadStateAdapters(
    header: LoadStateAdapter<*>,
    footer: LoadStateAdapter<*>
): ConcatAdapter {
    addLoadStateListener { loadStates ->
        header.loadState = loadStates.refresh
        footer.loadState = loadStates.append
    }

    return ConcatAdapter(header, this, footer)
}
