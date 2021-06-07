package dev.dizzy1021.core.domain

import androidx.paging.PagingData
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.domain.repository.IPlaceRepository
import dev.dizzy1021.core.domain.usecase.PlaceUseCase
import dev.dizzy1021.core.utils.ResourceWrapper
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import javax.inject.Inject

class ImplPlaceRepository @Inject constructor(
    private val repository: IPlaceRepository
) : PlaceUseCase {

    override fun fetchHome(user: String): Flow<PagingData<Place>> = repository.fetchHome(user)

    override fun getWishlist(user: String): Flow<PagingData<Place>> = repository.getWishlist(user)

    override fun searchPlaces(
        user: String,
        q: String?,
        image: InputStream?
    ): Flow<PagingData<Place>> = repository.searchPlaces(user, q, image)

    override fun fetchPlace(id: String, user: String): Flow<ResourceWrapper<Place>> = repository.fetchPlace(id, user)

    override fun addWishlist(id: Int, user: String, place: Place) = repository.addWishlist(id, user, place)
}