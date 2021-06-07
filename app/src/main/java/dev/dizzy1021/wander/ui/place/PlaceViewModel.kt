package dev.dizzy1021.wander.ui.place

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dizzy1021.core.domain.usecase.PlaceUseCase
import javax.inject.Inject

@HiltViewModel
class PlaceViewModel @Inject constructor(
    private val useCase: PlaceUseCase
): ViewModel() {

    val places = { id: String, user: String ->
        useCase.fetchPlace(id, user).asLiveData()
    }

    val addFavorite = { id: String, user: String ->
        useCase.addWishlist(id, user).asLiveData()
    }

}