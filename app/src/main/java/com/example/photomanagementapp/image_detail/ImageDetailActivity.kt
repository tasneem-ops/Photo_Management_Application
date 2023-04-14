package com.example.photomanagementapp.image_detail

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.photomanagementapp.databinding.ActivityImageDetailBinding
import com.squareup.picasso.Picasso

class ImageDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityImageDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras  != null){
            val imageUri = extras.getString("imageUri")
            val uri = Uri.parse(imageUri)
            Picasso.get()
                .load(uri)
                .into(binding.imageDetail)
        }
    }
}