package com.example.destour.api

import com.google.gson.annotations.SerializedName

data class RegisterRequest(
    val endpoint: String = "register",
    val nama_lengkap: String,
    val email: String,
    val nomor_hp: String,
    val password: String,
    val confirm_password: String
)

data class RegisterResponse(
    val code: Int,
    val status: String,
    val message: String,
    val data: DataRegis
) {
    data class DataRegis(
        val token: String,
        val user: User
    )

    data class User(
        val id: Int,
        val nama_lengkap: String,
        val email: String,
        val nomor_hp: String
    )
}

data class LoginRequest(
    val endpoint: String = "login",
    val email: String,
    val password: String
)

data class LoginResponse(
    val code: Int,
    val status: String,
    val message: String?,
    val data: LoginData?
)

data class LoginData(
    val token: String?,
    val user: UserInfo?
)

data class UserInfo(
    val id: Int,
    val namaLengkap: String,
    val email: String,
    val nomorHp: String
)

data class WisataResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: WisataData?
)


data class WisataData(
    @SerializedName("wisataList") val wisataList: List<WisataItem>
)

data class WisataItem(
    val id: Int,
    val image_url: String,
    val title: String,
    val lokasi: String, // Tambahkan lokasi
    val deskripsi: String, // Tambahkan deskripsi jika diperlukan
    var liked: Boolean, // Tambahkan liked jika ingin menampilkan status like
    var bookmarked: Boolean // Tambahkan ini
)

data class ProfileResponse(
    val status: String,
    val code: Int,
    val message: String,
    val data: ProfileData?
)

data class ProfileData(
    val id: Int,
    val nama_lengkap: String,
    val email: String,
    val nomor_hp: String
)

data class LikeResponse(
    val code: Int,
    val status: String,
    val message: String
)

data class GetBookmarksResponse(
    val status: Boolean,
    val message: String?,
    val data: BookmarkData?
)

data class BookmarkData(
    val total: Int,
    val bookmarks: List<BookmarkItem>
)

data class BookmarkItem(
    @SerializedName("id_wisata") val id: Int,
    val title: String,
    val image_url: String,
    val lokasi: String,
    val deskripsi: String,
    val liked: Boolean,
    val bookmarked: Boolean
)
