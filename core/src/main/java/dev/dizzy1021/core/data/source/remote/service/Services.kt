package dev.dizzy1021.core.data.source.remote.service

import dev.dizzy1021.core.data.source.remote.response.ResponseHome
import dev.dizzy1021.core.data.source.remote.response.ResponsePlace
import dev.dizzy1021.core.data.source.remote.response.ResponseReviews
import dev.dizzy1021.core.data.source.remote.response.ResponseWrapper
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface Services {

    @GET("home")
    suspend fun callHome(
        @Query("key") key: String,
        @Query("token") token: String?,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("wishlist")
    suspend fun callWishlist(
        @Query("key") key: String,
        @Query("page") page: Int,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @POST("wishlist")
    suspend fun addWishlist(
        @Query("key") key: String,
        @Query("id") place_id: String,
    ): Response<ResponseWrapper<List<Any?>>>

    @GET("review")
    suspend fun callReview(
        @Query("key") key: String,
        @Query("page") page: Int,
    ): Response<ResponseWrapper<List<ResponseReviews>>>

    @Multipart
    @POST("search")
    suspend fun searchPlaces(
        @Query("key") key: String,
        @Query("q") q: String?,
        @Query("token") token: String?,
        @Part image: MultipartBody.Part?,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("search")
    suspend fun searchPlaces(
        @Query("key") key: String,
        @Query("q") q: String?,
        @Query("location") loc: String? = null,
        @Query("token") token: String?,
    ): Response<ResponseWrapper<List<ResponseHome>>>

    @GET("place")
    suspend fun callPlaceById(
        @Query("id") id: String,
        @Query("key") key: String
    ): Response<ResponseWrapper<ResponsePlace>>

    @GET("place/{id}/review")
    suspend fun callReviewPlace(
        @Path("id") id: String,
        @Query("key") key: String,
        @Query("page") page: Int,
    ): Response<ResponseWrapper<List<ResponseReviews>>>

    @Multipart
    @POST("place/{id}/review")
    suspend fun createReview(
        @Path("id") id: String,
        @Query("key") key: String,
        @Part images: List<MultipartBody.Part?>,
        @Part("desc") desc: RequestBody,
        @Part("rating") rating: RequestBody
    ): Response<ResponseWrapper<String?>>

}