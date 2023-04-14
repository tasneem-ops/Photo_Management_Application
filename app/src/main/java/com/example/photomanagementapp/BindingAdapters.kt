package com.example.photomanagementapp

import android.net.Uri
import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photomanagementapp.img_list.ImageListAdapter
import com.example.photomanagementapp.model.Image
import com.squareup.picasso.Picasso

@BindingAdapter("imageUri")
fun bindImage(imageView: ImageView, imageUrl : Uri){
        val imageUri = imageUrl
        Picasso.get().load(imageUri)
            .placeholder(R.drawable.ic_launcher_background)
            .error(R.drawable.ic_launcher_foreground)
            .into(imageView)

}

@BindingAdapter("listData")
fun bindRecyclerView(recyclerView: RecyclerView, data: List<Image>?) {
    val adapter = recyclerView.adapter as ImageListAdapter
    adapter.submitList(data)
}