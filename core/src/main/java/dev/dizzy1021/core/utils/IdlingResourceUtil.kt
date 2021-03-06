package dev.dizzy1021.core.utils

import androidx.test.espresso.IdlingResource
import androidx.test.espresso.idling.CountingIdlingResource

object IdlingResourceUtil {
    private const val RESOURCE: String = "GLOBAL"
    private val espressoTestIdlingResource = CountingIdlingResource(RESOURCE)
    fun increment() {
        espressoTestIdlingResource.increment()
    }
    fun decrement() {
        espressoTestIdlingResource.decrement()
    }
    fun getEspressoIdlingResource(): IdlingResource {
        return espressoTestIdlingResource
    }
}