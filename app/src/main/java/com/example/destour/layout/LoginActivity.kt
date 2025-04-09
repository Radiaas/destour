package com.example.destour.layout

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.activity.NoViewModelActivity
import com.crocodic.core.extension.tos
import com.example.destour.R
import com.example.destour.api.ApiService
import com.example.destour.api.LoginRequest
import com.example.destour.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : NoViewModelActivity<ActivityLoginBinding>(R.layout.activity_login) {

    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

                // Langsung periksa token saat aplikasi dibuka
                checkTokenAndNavigate()

        binding.buttonLogin.setOnClickListener {
            if (validateInputs()) {
                showLoadingDialog()
                processLogin()
            }
        }

        binding.textViewRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }

    }

    /**
     * Cek apakah token tersimpan di SharedPreferences, jika iya langsung ke MainActivity
     */
    private fun checkTokenAndNavigate() {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", null)

        if (!token.isNullOrEmpty()) {
            // Jika token ada, langsung buka MainActivity
            navigateToMain()
        }
    }

    private fun navigateToMain() {
        val intent = Intent(this, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun validateInputs(): Boolean {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        return when {
            email.isEmpty() -> {
                tos("Email tidak boleh kosong")
                false
            }
            password.isEmpty() -> {
                tos("Password tidak boleh kosong")
                false
            }
            else -> true
        }
    }

    private fun processLogin() {
        val email = binding.editTextEmail.text.toString().trim()
        val password = binding.editTextPassword.text.toString().trim()

        lifecycleScope.launch {
            try {
                val request = LoginRequest(email = email, password = password)
                val response = apiService.login(request)
                hideLoadingDialog()

                if (response.isSuccessful) {
                    val token = response.body()?.data?.token

                    if (!token.isNullOrEmpty()) {
                        // Hapus semua data SharedPreferences sebelum menyimpan token baru
                        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        prefs.edit().clear().apply()

                        // Simpan token
                        prefs.edit().putString("token", token).apply()

                        navigateToMain()
                    } else {
                        tos("Token tidak tersedia.")
                    }
                } else {
                    tos("Login gagal: ${response.message()}")
                }
            } catch (e: Exception) {
                hideLoadingDialog()
                tos("Terjadi kesalahan: ${e.message}")
            }
        }
    }


    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        with(sharedPreferences.edit()) {
            putString("token", token)
            apply()
        }
    }




    private fun showLoadingDialog() = loadingDialog.show()
    private fun hideLoadingDialog() = loadingDialog.dismiss()
}
