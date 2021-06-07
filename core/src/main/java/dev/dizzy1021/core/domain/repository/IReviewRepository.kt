package dev.dizzy1021.core.domain.repository

import androidx.paging.PagingData
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.utils.ResourceWrapper
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface IReviewRepository {

    fun getReviews(user: String): Flow<PagingData<Review>>

    fun fetchReviewPlace(id: String, user: String): Flow<PagingData<Review>>

    fun addReview(id: String, images: List<InputStream?>, desc: String, rating: Int, user: String): Flow<ResourceWrapper<String?>>
}