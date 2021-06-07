package dev.dizzy1021.core.data.source

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import dev.dizzy1021.core.data.source.local.LocalDataSource
import dev.dizzy1021.core.data.source.remote.RemoteDataSource
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.domain.repository.IReviewRepository
import dev.dizzy1021.core.utils.ResourceState
import dev.dizzy1021.core.utils.ResourceWrapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReviewRepository @Inject constructor(
    private val remoteDataSource: RemoteDataSource,
    private val localDataSource: LocalDataSource
) : IReviewRepository {

    override fun getReviews(user: String): Flow<ResourceWrapper<List<Review>>> {
        TODO("Not yet implemented")
    }

    override fun fetchReviewPlace(id: String, user: String): Flow<PagingData<Review>> =
        Pager(
            PagingConfig(pageSize = 14, enablePlaceholders = false)
        ) {
            remoteDataSource.fetchReviewPlace(id, user)

        }.flow.flowOn(Dispatchers.IO)

    override fun addReview(
        id: String,
        images: List<InputStream?>,
        desc: String,
        rating: Int,
        user: String
    ) =
        flow {
            emit(ResourceWrapper.pending(null))

            val reqDesc = desc.toRequestBody()
            val reqRating = rating.toString().toRequestBody()
            val reqImages = ArrayList<MultipartBody.Part?>()

            val stringBuilder = StringBuilder()
            val timeStamp = (System.currentTimeMillis() / 1000).toString()

            for (image in images) {
                val partImage = image?.readBytes()?.let {
                    MultipartBody.Part.createFormData(
                        "images",
                        stringBuilder.append("IMG_").append(timeStamp).toString(),
                        it
                            .toRequestBody(
                                "image/*".toMediaTypeOrNull()
                            )
                    )
                }
                reqImages.add(partImage)
            }

            val response = remoteDataSource.addReview(
                id = id,
                desc = reqDesc,
                rating = reqRating,
                images = reqImages.toList(),
                user = user
            ).first()

            when (response.state) {
                ResourceState.SUCCESS -> {
                    val result = response.data?.data
                    emit(ResourceWrapper.success(result))
                }
                ResourceState.FAILURE -> {
                    emit(ResourceWrapper.failure(response.message.toString(), null))
                }
                ResourceState.PENDING -> {}
            }
        }.flowOn(Dispatchers.IO)

}