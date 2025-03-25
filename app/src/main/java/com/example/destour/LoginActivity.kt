package com.example.destour

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.activity.NoViewModelActivity
import com.example.destour.databinding.ActivityLoginBinding
import com.google.android.ads.mediationtestsuite.activities.HomeActivity
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
                showToast("Email tidak boleh kosong")
                false
            }
            password.isEmpty() -> {
                showToast("Password tidak boleh kosong")
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
                response.message?.let { showToast(it) }

                if (response.status == "success") {
                    // Simpan token ke SharedPreferences
                    response.data?.token?.let { saveToken(it) }

                    // Arahkan ke MainActivity
                    navigateToMain()
                }
            } catch (e: Exception) {
                hideLoadingDialog()
                showToast("Error: ${e.message}")
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

    private fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun showLoadingDialog() = loadingDialog.show()
    private fun hideLoadingDialog() = loadingDialog.dismiss()
}
