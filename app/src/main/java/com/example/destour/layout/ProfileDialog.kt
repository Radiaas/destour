package com.example.destour.layout

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import com.example.destour.api.ProfileData
import com.example.destour.databinding.ActivityProfileDialogBinding

class ProfileDialog(context: Context, private val profile: ProfileData) : Dialog(context) {

    private lateinit var binding: ActivityProfileDialogBinding
    var onLogoutListener: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileDialogBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvName.text = profile.nama_lengkap
        binding.tvEmail.text = profile.email
        binding.tvPhone.text = profile.nomor_hp

        binding.btnClose.setOnClickListener { dismiss() }

        binding.btnLogout.setOnClickListener {
            onLogoutListener?.invoke() // Panggil listener logout
            dismiss() // Tutup dialog setelah logout
        }
    }
}
