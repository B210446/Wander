package dev.dizzy1021.wander.ui.search

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.dizzy1021.core.domain.usecase.PlaceUseCase
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import java.io.InputStream
import javax.inject.Inject

@HiltViewModel
@ExperimentalCoroutinesApi
@FlowPreview
class SearchViewModel @Inject constructor(
    private val useCase: PlaceUseCase
): ViewModel() {

    val queryChannel = BroadcastChannel<String>(Channel.CONFLATED)

    val places = { user: String, image: InputStream? ->

        queryChannel
            .asFlow()
            .debounce(1000)
            .distinctUntilChanged()
            .filter {
                return@filter it.trim().length >= 3
            }
            .flatMapLatest {
                useCase.searchPlaces(it, image, user)
            }
    }

}