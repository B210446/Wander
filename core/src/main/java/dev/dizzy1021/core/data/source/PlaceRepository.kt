package dev.dizzy1021.core.data.source

import dev.dizzy1021.core.data.source.local.LocalDataSource
import dev.dizzy1021.core.data.source.remote.RemoteDataSource
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.domain.repository.IPlaceRepository
import dev.dizzy1021.core.utils.IdlingResourceUtil
import dev.dizzy1021.core.utils.ResourceState
import dev.dizzy1021.core.utils.ResourceWrapper
import dev.dizzy1021.core.utils.toPlace
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaceRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IPlaceRepository {

    override fun fetchHome(page: Int, user: String): Flow<ResourceWrapper<List<Place>>> =
        flow{
            IdlingResourceUtil.increment()
            emit(ResourceWrapper.pending(null))

            val response = remoteDataSource.fetchHome(page = page, user = user)
                .first()

            when(response.state) {
                ResourceState.SUCCESS -> {
                    val result = response.data?.data.let {
                        it?.toPlace()
                    }
                    emit(ResourceWrapper.success(result))
                }
                ResourceState.FAILURE -> {
                    emit(ResourceWrapper.failure(response.message.toString(), null))
                }
            }
            IdlingResourceUtil.decrement()
        }.flowOn(Dispatchers.IO)

    override fun getWishlist(page: Int, user: String): Flow<ResourceWrapper<List<Place>>> {
        TODO("Not yet implemented")
    }

    override fun searchPlaces(
        page: Int,
        user: String,
        q: String?,
        image: Any?
    ): Flow<ResourceWrapper<List<Place>>> {
        TODO("Not yet implemented")
    }

    override fun fetchPlace(id: Int, user: String): Flow<ResourceWrapper<Place>> =
        flow{
            IdlingResourceUtil.increment()
            emit(ResourceWrapper.pending(null))

            val response = remoteDataSource.findPlaceByID(id = id, user = user)
                .first()

            when(response.state) {
                ResourceState.SUCCESS -> {
                    val result = response.data?.data.let {
                        it?.toPlace()
                    }
                    emit(ResourceWrapper.success(result))
                }
                ResourceState.FAILURE -> {
                    emit(ResourceWrapper.failure(response.message.toString(), null))
                }
            }
            IdlingResourceUtil.decrement()
        }.flowOn(Dispatchers.IO)

    override fun addWishlist(id: Int, user: String, place: Place) {
        TODO("Not yet implemented")
    }
}