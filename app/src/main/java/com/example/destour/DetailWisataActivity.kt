package com.example.destour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import coil.load
import com.bumptech.glide.Glide
import com.crocodic.core.helper.ImagePreviewHelper
import com.example.destour.databinding.ActivityDetailWisataBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailWisataActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailWisataBinding

    @Inject
    lateinit var apiService: ApiService

    private var isLiked = false
    private var isBookmarked = false
    private var wisataId = -1
    private var token: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailWisataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Glide.with(this)
            .load(intent.getStringExtra("EXTRA_IMAGE_URL"))
            .into(binding.imgWisata)

        // Tambahkan ini setelah load image
        val imageUrl = intent.getStringExtra("EXTRA_IMAGE_URL")
        val imagePreviewHelper = ImagePreviewHelper(this)

        binding.imgWisata.setOnClickListener {
            imagePreviewHelper.show(binding.imgWisata, imageUrl)
        }


        wisataId = intent.getIntExtra("EXTRA_ID", -1)
        isLiked = intent.getBooleanExtra("EXTRA_LIKED", false)
        isBookmarked = intent.getBooleanExtra("EXTRA_BOOKMARKED", false)

        // Load data dari Intent
        Glide.with(this)
            .load(intent.getStringExtra("EXTRA_IMAGE_URL"))
            .into(binding.imgWisata)

        binding.tvTitle.text = intent.getStringExtra("EXTRA_TITLE")
        binding.tvLokasi.text = intent.getStringExtra("EXTRA_LOKASI")
        binding.tvDeskripsi.text = intent.getStringExtra("EXTRA_DESKRIPSI")

        loadToken()

        updateLikeButton()
        updateBookmarkButton()
        loadInitialBookmarkStatus()

        binding.btnLike.setOnClickListener {
            toggleLikeStatus()
        }

        binding.btnBookmark.setOnClickListener {
            toggleBookmarkStatus()
        }
    }

    private fun updateLikeButton() {
        binding.btnLike.setImageResource(if (isLiked) R.drawable.ic_liked else R.drawable.ic_like)
    }

    private fun updateBookmarkButton() {
        binding.btnBookmark.setImageResource(if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark)
    }

    private fun loadToken() {
        token = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("token", "").orEmpty()
    }

    private fun loadInitialBookmarkStatus() {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isBookmarked = sharedPrefs.getBoolean("BOOKMARK_$wisataId", false)
        updateBookmarkButton()
    }

    private fun loadInitialLikeStatus() {
        val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        isLiked = sharedPrefs.getBoolean("LIKE_$wisataId", false)
        updateLikeButton()
    }

    private fun toggleLikeStatus() {
        lifecycleScope.launch {
            try {
                val response = if (isLiked) {
                    apiService.unlikeWisata(token = token, idWisata = wisataId)
                } else {
                    apiService.likeWisata(token = token, idWisata = wisataId)
                }

                if (response.isSuccessful && response.body()?.code == 200) {
                    isLiked = !isLiked
                    updateLikeButton()

                    // Simpan status "like" ke SharedPreferences
                    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putBoolean("LIKE_$wisataId", isLiked)
                    editor.apply()

                    // Kirim hasil kembali ke MainActivity
                    val resultIntent = Intent().apply {
                        putExtra("EXTRA_ID", wisataId)
                        putExtra("EXTRA_LIKED", isLiked)
                    }
                    setResult(RESULT_OK, resultIntent)

                    Toast.makeText(
                        this@DetailWisataActivity,
                        "Berhasil ${if (isLiked) "menyukai" else "membatalkan like"} wisata",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@DetailWisataActivity, "Gagal memperbarui like", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailWisataActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun toggleBookmarkStatus() {
        lifecycleScope.launch {
            try {
                val response = if (isBookmarked) {
                    apiService.removeBookmark(token = token, idWisata = wisataId)
                } else {
                    apiService.addBookmark(token = token, idWisata = wisataId)
                }

                if (response.isSuccessful) {
                    isBookmarked = !isBookmarked
                    updateBookmarkButton()

                    // Simpan status bookmark ke SharedPreferences
                    val sharedPrefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                    val editor = sharedPrefs.edit()
                    editor.putBoolean("BOOKMARK_$wisataId", isBookmarked)
                    editor.apply()

                    // Kirim hasil kembali ke MainActivity
                    val resultIntent = Intent().apply {
                        putExtra("EXTRA_ID", wisataId)
                        putExtra("EXTRA_BOOKMARKED", isBookmarked)
                    }
                    setResult(RESULT_OK, resultIntent)

                    Toast.makeText(
                        this@DetailWisataActivity,
                        "Berhasil ${if (isBookmarked) "menandai" else "menghapus"} bookmark",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@DetailWisataActivity, "Gagal memperbarui bookmark", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(this@DetailWisataActivity, "Terjadi kesalahan: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }


}
