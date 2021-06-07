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

    override fun getWishlist(user: String): Flow<ResourceWrapper<List<Place>>> {
        TODO("Not yet implemented")
    }

    override fun searchPlaces(
        q: String?,
        image: InputStream?,
        user: String
    ): Flow<PagingData<Place>> = repository.searchPlaces(q, image, user)

    override fun fetchPlace(id: String, user: String): Flow<ResourceWrapper<Place>> = repository.fetchPlace(id, user)

    override fun addWishlist(id: Int, place: Place, user: String) = repository.addWishlist(id, place, user)
}