package dev.dizzy1021.core.data.source.remote.service

import dev.dizzy1021.core.BuildConfig
import dev.dizzy1021.core.data.source.remote.response.ResponseHome
import dev.dizzy1021.core.data.source.remote.response.ResponseReviews
import dev.dizzy1021.core.data.source.remote.response.ResponseWishlist
import dev.dizzy1021.core.data.source.remote.response.ResponseWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Services {

    @GET("home")
    suspend fun callHome(
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("page") page: Int,
        @Query("user") user: String,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("wishlist")
    suspend fun callWishlist(
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("page") page: Int,
        @Query("user") user: String,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("review")
    suspend fun callReview(
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("page") page: Int,
        @Query("user") user: String,
    ): Response<ResponseWrapper<ResponseReviews>>

    @Multipart
    @POST("search")
    suspend fun searchPlaces(
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("q") q: String?,
        @Query("page") page: Int,
        @Query("user") user: String,
        @Part image: MultipartBody.Part?,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @POST("search")
    suspend fun searchPlaces(
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("q") q: String?,
        @Query("page") page: Int,
        @Query("user") user: String,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("place/{id}")
    suspend fun callPlaceById(
        @Path("id") id: String,
        @Query("user") user: String,
        @Query("key") key: String = BuildConfig.API_KEY_SERVER
    ): Response<ResponseWrapper<ResponseHome>>

    @GET("place/{id}/review")
    suspend fun callReviewPlace(
        @Path("id") id: String,
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Query("page") page: Int,
    ): Response<ResponseWrapper<List<ResponseReviews>>>

    @Multipart
    @POST("place/{id}/review")
    suspend fun createReview(
        @Path("id") id: String,
        @Query("key") key: String = BuildConfig.API_KEY_SERVER,
        @Part images: List<MultipartBody.Part?>,
        @Part("user") user: RequestBody,
        @Part("desc") desc: RequestBody,
        @Part("rating") rating: RequestBody
    ): Response<ResponseWrapper<String?>>

}