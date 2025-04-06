package com.example.destour

import com.crocodic.core.api.ApiResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("exec")
    suspend fun register(@Body request: RegisterRequest): RegisterResponse

    @POST("exec")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("exec")
    suspend fun getWisata(
        @Query("endpoint") endpoint: String = "listwisata",
        @Query("token") token: String
    ): Response<WisataResponse>

    @GET("exec")
    suspend fun searchWisata(
        @Query("endpoint") endpoint: String = "searchWisata",
        @Query("token") token: String,
        @Query("keyword") keyword: String
    ): Response<WisataResponse>

    @GET("exec")
    suspend fun getProfile(
        @Query("endpoint") endpoint: String = "profile",
        @Query("token") token: String
    ): Response<ProfileResponse>

    @POST("exec")
    suspend fun likeWisata(
        @Query("endpoint") endpoint: String = "likeWisata",
        @Query("token") token: String,
        @Query("id_wisata") idWisata: Int
    ): Response<LikeResponse>

    @POST("exec")
    suspend fun unlikeWisata(
        @Query("endpoint") endpoint: String = "unlikeWisata",
        @Query("token") token: String,
        @Query("id_wisata") idWisata: Int
    ): Response<LikeResponse>

    @POST("exec")
    suspend fun addBookmark(
        @Query("endpoint") endpoint: String = "addBookmarks",
        @Query("token") token: String,
        @Query("id_wisata") idWisata: Int
    ): Response<ResponseBody> // API tidak mengembalikan body

    @POST("exec")
    suspend fun removeBookmark(
        @Query("endpoint") endpoint: String = "removeBookmarks",
        @Query("token") token: String,
        @Query("id_wisata") idWisata: Int
    ): Response<ResponseBody>

    @POST("exec")
    suspend fun logout(
        @Query("endpoint") endpoint: String = "logout",
        @Query("token") token: String
    ): Response<ApiResponse>

    @GET("exec")
    suspend fun getBookmarks(
        @Query("endpoint") endpoint: String = "getBookmarks",
        @Query("token") token: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 10
    ): Response<GetBookmarksResponse>

}
