package dev.dizzy1021.wander.ui.place.feedback

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dizzy1021.core.domain.usecase.ReviewUseCase
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor(
    private val useCase: ReviewUseCase
): ViewModel() {

    val reviews = { id: String, user: String ->
        useCase.fetchReviewPlace(id, user).cachedIn(viewModelScope)
    }

    val addReview = {
            id: String,
            images: List<InputStream?>,
            user: String,
            desc: String,
            rating: Int  ->

        useCase.addReview(
            id = id,
            images = images,
            user = user,
            desc = desc,
            rating = rating
        ).asLiveData()
    }
}