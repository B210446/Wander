package dev.dizzy1021.core.domain.usecase

import androidx.paging.PagingData
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.utils.ResourceWrapper
import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface ReviewUseCase {

    fun getReviews(user: String): Flow<PagingData<Review>>

    fun fetchReviewPlace(id: String, user: String): Flow<PagingData<Review>>

    fun addReview(id: String, user: String, images: List<InputStream?>, desc: String, rating: Int): Flow<ResourceWrapper<String?>>

}