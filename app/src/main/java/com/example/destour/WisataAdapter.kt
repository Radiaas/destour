package com.example.destour

import android.content.Context
import android.content.Intent
import android.util.Log
import com.bumptech.glide.Glide
import com.crocodic.core.base.adapter.CoreListAdapter
import com.crocodic.core.helper.ImagePreviewHelper
import com.example.destour.databinding.ItemWisataBinding
import timber.log.Timber

class WisataAdapter(
    context: Context
) : CoreListAdapter<ItemWisataBinding, WisataItem>(R.layout.item_wisata) {

    private val imagePreviewHelper = ImagePreviewHelper(context)

    override fun onBindViewHolder(holder: ItemViewHolder<ItemWisataBinding, WisataItem>, position: Int) {
        val wisata = items[position] ?: return
        Log.d("WisataAdapter", "Binding data: ${wisata.title}")

        holder.binding.data = wisata
        holder.binding.executePendingBindings()

        val imageUrl = convertGoogleDriveUrl(wisata.image_url)
        Timber.tag("WisataAdapter").d("Image URL: $imageUrl")

        // Load gambar
        Glide.with(holder.binding.ivWisata.context)
            .load(imageUrl)
            .into(holder.binding.ivWisata)

        holder.binding.tvTitle.text = wisata.title
        holder.binding.tvLokasi.text = wisata.lokasi

        // Set status like
        updateLikeIcon(holder, wisata.liked)

        updateBookmarkIcon(holder, wisata.id)


        // Klik item untuk membuka DetailWisataActivity
        holder.itemView.setOnClickListener {
            val intent = Intent(holder.itemView.context, DetailWisataActivity::class.java).apply {
                putExtra("EXTRA_ID", wisata.id)
                putExtra("EXTRA_IMAGE_URL", convertGoogleDriveUrl(wisata.image_url))
                putExtra("EXTRA_TITLE", wisata.title)
                putExtra("EXTRA_LOKASI", wisata.lokasi)
                putExtra("EXTRA_DESKRIPSI", wisata.deskripsi)
                putExtra("EXTRA_LIKED", wisata.liked)
            }
            (holder.itemView.context as? MainActivity)?.detailLauncher?.launch(intent)
        }


    }

    fun updateLikeStatus(wisataId: Int, isLiked: Boolean) {
        val index = items.indexOfFirst { it!!.id == wisataId }
        if (index != -1) {
            items[index]?.let {
                it.liked = isLiked
                notifyItemChanged(index)
            }
        }
    }

    private fun updateBookmarkIcon(holder: ItemViewHolder<ItemWisataBinding, WisataItem>, wisataId: Int) {
        val sharedPrefs = holder.itemView.context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val isBookmarked = sharedPrefs.getBoolean("BOOKMARK_$wisataId", false)

        holder.binding.btnBookmark.setImageResource(
            if (isBookmarked) R.drawable.ic_bookmarked else R.drawable.ic_bookmark
        )

//        holder.binding.btnBookmark.setOnClickListener {
//            val newBookmarkStatus = !isBookmarked
//            sharedPrefs.edit().putBoolean("BOOKMARK_$wisataId", newBookmarkStatus).apply()
//            updateBookmarkStatus(wisataId, newBookmarkStatus)
//        }
    }



    fun updateBookmarkStatus(wisataId: Int, isBookmarked: Boolean) {
        val index = items.indexOfFirst { it!!.id == wisataId }
        if (index != -1) {
            items[index]?.let {
                it.bookmarked = isBookmarked
                notifyItemChanged(index)
            }
        } else {
            Log.e("WisataAdapter", "ID tidak ditemukan di daftar wisata")
        }
    }




    private fun convertGoogleDriveUrl(url: String?): String {
        if (url.isNullOrEmpty()) return ""
        return if (url.contains("/file/d/")) {
            val fileId = url.substringAfter("/file/d/").substringBefore("/view")
            "https://drive.google.com/uc?export=view&id=$fileId"
        } else {
            url
        }
    }

    private fun updateLikeIcon(holder: ItemViewHolder<ItemWisataBinding, WisataItem>, isLiked: Boolean) {
        val likeIcon = if (isLiked) R.drawable.ic_liked else R.drawable.ic_like
        holder.binding.btnLike.setImageResource(likeIcon)
    }
}
