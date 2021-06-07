package dev.dizzy1021.core.data.source.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import dev.dizzy1021.core.data.source.remote.response.ResponsePlace
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
        object : PagingSource<String, Place>() {

            override fun getRefreshKey(state: PagingState<String, Place>): String? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestItemToPosition(anchorPosition)?.id
                }

            }

            override suspend fun load(params: LoadParams<String>): LoadResult<String, Place> {
                try {
                    val response = services.callHome(
                        token = params.key,
                        key = user
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = response.body()?.links?.self,
                        nextKey = response.body()?.links?.next
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

    suspend fun fetchReview(user: String): Flow<ResourceWrapper<ResponseWrapper<ResponseReviews>>> =
        flow {
            services.callReview(
                page = 1,
                key = user
            ).let {
                if (it.isSuccessful) {
                    emit(ResourceWrapper.success(it.body()))
                } else {
                    emit(ResourceWrapper.failure("Failure when calling data", null))
                }
            }
        }.flowOn(Dispatchers.IO)

     fun findPlaces(user: String, q: String?, image: MultipartBody.Part?) =
        object : PagingSource<String, Place>() {

            override fun getRefreshKey(state: PagingState<String, Place>): String? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestItemToPosition(anchorPosition)?.id
                }
            }

            override suspend fun load(params: LoadParams<String>): LoadResult<String, Place> {
                try {
                    val response = services.searchPlaces(
                        q = q,
                        token = params.key,
                        image = image,
                        key = user
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)

                    return LoadResult.Page(
                        data = responseData,
                        prevKey = response.body()?.links?.prev,
                        nextKey = response.body()?.links?.next
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

     fun findPlaces(user: String, q: String?) =
        object : PagingSource<String, Place>() {

            override fun getRefreshKey(state: PagingState<String, Place>): String? {
                return state.anchorPosition?.let { anchorPosition ->
                    state.closestItemToPosition(anchorPosition)?.id
                }
            }

            override suspend fun load(params: LoadParams<String>): LoadResult<String, Place> {
                try {
                    val response = services.searchPlaces(
                        q = q,
                        token = params.key,
                        key = user
                    )

                    val responseData = mutableListOf<Place>()
                    val data = response.body()?.data.let {
                        it?.toPlace()
                    } ?: emptyList()

                    responseData.addAll(data)


                    return LoadResult.Page(
                        data = responseData,
                        prevKey = response.body()?.links?.prev,
                        nextKey = response.body()?.links?.next
                    )

                } catch (e: Exception) {
                    return LoadResult.Error(e)
                }
            }
        }

     fun findPlaceByID(user: String, id: String): Flow<ResourceWrapper<ResponseWrapper<ResponsePlace>>> =
         flow {
            services.callPlaceById(
                id = id,
                key = user
            ).let {
                if (it.isSuccessful) {
                    emit(ResourceWrapper.success(it.body()))
                } else {
                    emit(ResourceWrapper.failure("Failure when calling data", null))
                }
            }
        }.flowOn(Dispatchers.IO)

    fun fetchReviewPlace(user: String, id: String): PagingSource<Int, Review> =
        object : PagingSource<Int, Review>() {

            override fun getRefreshKey(state: PagingState<Int, Review>): Int? {
                return state.anchorPosition
            }

            override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Review> {
                try {
                    val currentLoadingPageKey = params.key ?: 1
                    val response = services.callReviewPlace(
                        page = currentLoadingPageKey,
                        id = id,
                        key = user
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
        user: String,
        images: List<MultipartBody.Part?>,
        desc: RequestBody,
        rating: RequestBody,
    ) = flow {
        services.createReview(
            id = id,
            images = images,
            desc = desc,
            key = user,
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
