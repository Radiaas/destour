package com.example.destour

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.crocodic.core.base.activity.NoViewModelActivity
import com.example.destour.databinding.ActivityRegisterBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class RegisterActivity : NoViewModelActivity<ActivityRegisterBinding>(R.layout.activity_register) {

    @Inject
    lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding.buttonRegister.setOnClickListener {
            if (validateInputs()) {
                showLoadingDialog()
                processRegistration()
            }
        }

        binding.textViewLogin.setOnClickListener {
            // Navigasi ke LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    private fun validateInputs(): Boolean {
        val (nama, email, hp, password, confirmPassword) = listOf(
            binding.editTextNamaLengkap.text,
            binding.editTextEmail.text,
            binding.editTextNomorHp.text,
            binding.editTextPassword.text,
            binding.editTextConfirmPassword.text
        ).map { it.toString().trim() }

        return when {
            nama.isEmpty() -> showToast("Nama lengkap tidak boleh kosong").let { false }
            email.isEmpty() -> showToast("Email tidak boleh kosong").let { false }
            hp.isEmpty() -> showToast("Nomor HP tidak boleh kosong").let { false }
            password.isEmpty() -> showToast("Password tidak boleh kosong").let { false }
            confirmPassword.isEmpty() -> showToast("Konfirmasi password tidak boleh kosong").let { false }
            password != confirmPassword -> showToast("Password dan konfirmasi tidak sesuai").let { false }
            else -> true
        }
    }

    private fun processRegistration() {
        val request = RegisterRequest(
            nama_lengkap = binding.editTextNamaLengkap.text.toString().trim(),
            email = binding.editTextEmail.text.toString().trim(),
            nomor_hp = binding.editTextNomorHp.text.toString().trim(),
            password = binding.editTextPassword.text.toString().trim(),
            confirm_password = binding.editTextConfirmPassword.text.toString().trim()
        )

        lifecycleScope.launch {
            try {
                val response = apiService.register(request)
                hideLoadingDialog()
                showToast(response.message)
                if (response.code == 201) finish()
            } catch (e: Exception) {
                hideLoadingDialog()
                showToast("Error: ${e.message}")
            }
        }
    }

    private fun showToast(message: String) =
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()

    private fun showLoadingDialog() = loadingDialog.show()
    private fun hideLoadingDialog() = loadingDialog.dismiss()
}
