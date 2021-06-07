package dev.dizzy1021.wander.ui.wishlist

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dizzy1021.core.domain.usecase.PlaceUseCase
import javax.inject.Inject

@HiltViewModel
class WishlistViewModel @Inject constructor(
    private val useCase: PlaceUseCase
) : ViewModel() {
    val wishlist = {user: String ->
        useCase.getWishlist(user).cachedIn(viewModelScope)
    }
}