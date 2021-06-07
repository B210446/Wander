package dev.dizzy1021.core.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.dizzy1021.core.data.source.remote.response.ResponseHome
import dev.dizzy1021.core.data.source.remote.response.ResponseReviews
import dev.dizzy1021.core.data.source.remote.response.ResponseWishlist
import dev.dizzy1021.core.data.source.remote.response.ResponseWrapper
import dev.dizzy1021.core.data.source.remote.service.Services
import dev.dizzy1021.core.domain.model.Place
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.utils.ResourceWrapper
import dev.dizzy1021.core.utils.toPlace
import dev.dizzy1021.core.utils.toReViews
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MultipartBody
import okhttp3.RequestBody
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteDataSource @Inject constructor(private val services: Services) {

    fun fetchHome(user: String) =
        object : PagingSource<Int, Place>() {

            override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.callHome(
                        page = currentLoadingPageKey,
                        user = user
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = prevKey,
                        nextKey = currentLoadingPageKey.plus(1)
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

    fun fetchWishlist(user: String) =
        object : PagingSource<Int, Place>() {

            override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.callWishlist(
                        page = currentLoadingPageKey,
                        user = user
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = prevKey,
                        nextKey = currentLoadingPageKey.plus(1)
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

    suspend fun fetchReview(page: Int, user: String): Flow<ResourceWrapper<ResponseWrapper<ResponseReviews>>> =
        flow {
            services.callReview(
                page = page,
                user = user
            ).let {
                if (it.isSuccessful) {
                    emit(ResourceWrapper.success(it.body()))
                } else {
                    emit(ResourceWrapper.failure("Failure when calling data", null))
                }
            }
        }.flowOn(Dispatchers.IO)

     fun findPlaces(user: String, q: String?, image: MultipartBody.Part?) =
        object : PagingSource<Int, Place>() {

            override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.searchPlaces(
                        page = currentLoadingPageKey,
                        user = user,
                        q = q,
                        image = image,
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = prevKey,
                        nextKey = currentLoadingPageKey.plus(1)
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

     fun findPlaces(user: String, q: String?) =
        object : PagingSource<Int, Place>() {

            override fun getRefreshKey(state: PagingState<Int, Place>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Place> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.searchPlaces(
                        page = currentLoadingPageKey,
                        user = user,
                        q = q,
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = prevKey,
                        nextKey = currentLoadingPageKey.plus(1)
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

     fun findPlaceByID(id: String, user: String): Flow<ResourceWrapper<ResponseWrapper<ResponseHome>>> =
         flow {
            services.callPlaceById(
                id = id,
                user = user,
            ).let {
                if (it.isSuccessful) {
                    emit(ResourceWrapper.success(it.body()))
                } else {
                    emit(ResourceWrapper.failure("Failure when calling data", null))
                }
            }
        }.flowOn(Dispatchers.IO)

    fun fetchReviewPlace(id: String): PagingSource<Int, Review> =
        object : PagingSource<Int, Review>() {

            override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.callReviewPlace(
                        page = currentLoadingPageKey,
                        id = id
                    )

                    val responseData = mutableListOf<Review>()
                    val data = response.body()?.data.let {
                        it?.toReViews()
                    } ?: emptyList()

                    responseData.addAll(data)

                    val prevKey = if (currentLoadingPageKey == 1) null else currentLoadingPageKey - 1

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = prevKey,
                        nextKey = currentLoadingPageKey.plus(1)
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

    suspend fun addReview(
        id: String,
        images: List<MultipartBody.Part?>,
        user: RequestBody,
        desc: RequestBody,
        rating: RequestBody,
    ) = flow {
        services.createReview(
            id = id,
            images = images,
            user = user,
            desc = desc,
            rating = rating
        ).let {
            if (it.isSuccessful) {
                emit(ResourceWrapper.success(it.body()))
            } else {
                emit(ResourceWrapper.failure("Failure when calling data", null))
            }
        }
    }.flowOn(Dispatchers.IO)

}
