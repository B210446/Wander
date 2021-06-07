package dev.dizzy1021.wander.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dizzy1021.core.domain.usecase.ReviewUseCase
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val useCase: ReviewUseCase
) : ViewModel() {
    val review = { id: String ->
        useCase.fetchReviewPlace(id).cachedIn(viewModelScope)
    }
}