package dev.dizzy1021.core.domain

import androidx.paging.PagingData
import dev.dizzy1021.core.domain.model.Review
import dev.dizzy1021.core.domain.repository.IReviewRepository
import dev.dizzy1021.core.domain.usecase.ReviewUseCase
import kotlinx.coroutines.flow.Flow
import java.io.InputStream
import javax.inject.Inject

class ImplReviewRepository @Inject constructor(
    private val repository: IReviewRepository
) : ReviewUseCase{
    override fun getReviews(user: String): Flow<PagingData<Review>> = repository.getReviews(user)

    override fun fetchReviewPlace(id: String, user: String): Flow<PagingData<Review>> = repository.fetchReviewPlace(id, user)

    override fun addReview(id: String, user: String, images: List<InputStream?>, desc: String, rating: Int) =
        repository.addReview(
        id,
        images,
        desc,
        rating,
        user
    )
}