package com.example.destour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.crocodic.core.base.activity.NoViewModelActivity
import com.example.destour.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : NoViewModelActivity<ActivityMainBinding>(R.layout.activity_main) {

    @Inject
    lateinit var apiService: ApiService

    private lateinit var wisataAdapter: WisataAdapter
    private var token: String = ""
    private var searchJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("MainActivity", "onCreate dipanggil")

        setupRecyclerView()
        loadToken()
        setupSearchListener()
        setupFabClickListener()

        if (token.isNotEmpty()) {
            getWisataList()
            getBookmarks()
        } else {
            showToast("Token tidak tersedia, silakan login ulang.")
        }
    }

    val detailLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val wisataId = data?.getIntExtra("EXTRA_ID", -1) ?: -1
            val isLiked = data?.getBooleanExtra("EXTRA_LIKED", false) ?: false
            val isBookmarked = data?.getBooleanExtra("EXTRA_BOOKMARKED", false) ?: false

            if (wisataId != -1) {
                wisataAdapter.updateLikeStatus(wisataId, isLiked)
                wisataAdapter.updateBookmarkStatus(wisataId, isBookmarked)
            }
        }
    }

    private fun getBookmarks() {
        lifecycleScope.launch {
            try {
                val response = apiService.getBookmarks(token = token, page = 1, limit = 10)
                if (response.isSuccessful) {
                    response.body()?.data?.bookmarks?.let { bookmarks ->
                        bookmarks.forEach { bookmark ->
                            Log.d("MainActivity", "Bookmark ditemukan: ${bookmark.title}, ID: ${bookmark.id}")

                            // Simpan di SharedPreferences
                            getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit()
                                .putBoolean("BOOKMARK_${bookmark.id}", true)
                                .apply()

                            // Update di adapter
                            wisataAdapter.updateBookmarkStatus(bookmark.id, true)

                            updateBookmarkIcons(bookmarks)

                        }
                        wisataAdapter.notifyDataSetChanged() // Tambahkan ini agar perubahan terlihat
                    } ?: showToast("Data bookmark kosong.")
                } else {
                    showToast("Gagal mengambil bookmark: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan: ${e.message}")
            }
        }
    }




    private fun updateBookmarkIcons(bookmarks: List<BookmarkItem>) {
        bookmarks.forEach { bookmark ->
            wisataAdapter.updateBookmarkStatus(bookmark.id, bookmark.bookmarked)
        }
    }

    private fun performLogout() {
        lifecycleScope.launch {
            try {
                val response = apiService.logout(token = token)
                if (response.isSuccessful) {
                    // Hapus SEMUA data SharedPreferences
                    getSharedPreferences("app_prefs", Context.MODE_PRIVATE).edit().apply {
                        clear() // Ini menghapus semua data termasuk token dan bookmark
                        apply()
                    }
                    navigateToLogin()
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }


    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun setupRecyclerView() {
        wisataAdapter = WisataAdapter(this)

        binding.rvWisata.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = wisataAdapter
        }
    }

    private fun loadToken() {
        token = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
            .getString("token", "").orEmpty()
    }

    private fun setupSearchListener() {
        binding.etSearch.addTextChangedListener { editable ->
            val keyword = editable.toString().trim()

            searchJob?.cancel() // Batalkan pencarian sebelumnya
            searchJob = lifecycleScope.launch {
                delay(300) // debounce selama 300ms
                if (keyword.isNotEmpty()) {
                    searchWisata(keyword)
                } else {
                    getWisataList()
                }
            }
        }
    }

    private fun setupFabClickListener() {
        binding.fabProfile.setOnClickListener {
            showProfileDialog()
        }
    }

    private fun showProfileDialog() {
        lifecycleScope.launch {
            try {
                val response = apiService.getProfile(token = token)
                if (response.isSuccessful) {
                    response.body()?.data?.let { profileData ->
                        val dialog = ProfileDialog(this@MainActivity, profileData)
                        dialog.setCancelable(true)

                        dialog.onLogoutListener = {
                            performLogout()
                        }

                        dialog.show()
                    } ?: showToast("Data profil tidak tersedia.")
                } else {
                    showToast("Gagal mengambil profil: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.etSearch.windowToken, 0)
    }

    private fun getWisataList() {
        lifecycleScope.launch {
            try {
                val response = apiService.getWisata(token = token)
                if (response.isSuccessful) {
                    response.body()?.data?.let { wisataData ->
                        wisataData.wisataList.forEach {
                            Log.d("MainActivity", "Wisata: ${it.title}, ID: ${it.id}")
                        }
                        wisataAdapter.initItem(ArrayList(wisataData.wisataList))
                        wisataAdapter.notifyDataSetChanged()
                        getBookmarks()
                    } ?: showToast("Data wisata kosong.")
                } else {
                    showToast("Gagal mengambil data: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan: ${e.message}")
            }
        }
    }



    private fun searchWisata(keyword: String) {
        lifecycleScope.launch {
            try {
                val response = apiService.searchWisata(token = token, keyword = keyword)
                if (response.isSuccessful) {
                    response.body()?.data?.let {
                        wisataAdapter.initItem(ArrayList(it.wisataList))
                        wisataAdapter.notifyDataSetChanged()
                    } ?: showToast("Wisata tidak ditemukan.")
                } else {
                    showToast("Gagal mencari data: ${response.message()}")
                }
            } catch (e: Exception) {
                showToast("Terjadi kesalahan: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
