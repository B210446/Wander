package dev.dizzy1021.core.domain.repository

import androidx.paging.PagingData
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.utils.ResourceWrapper
import kotlinx.coroutines.flow.Flow

interface IReviewRepository {

    fun getReviews(page: Int, user: String): Flow<ResourceWrapper<List<Review>>>

    fun fetchReviewPlace(id: Int): Flow<PagingData<Review>>

    fun addReview(id: Int, request: Review)
}