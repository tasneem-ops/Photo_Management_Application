package com.example.photomanagementapp.img_list

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.photomanagementapp.databinding.ImageItemBinding
import com.example.photomanagementapp.model.Image
import com.squareup.picasso.Picasso

class ImageListAdapter(val clickListener: ImagesListListener) : ListAdapter<Image, ImageListAdapter.ImageViewHolder>(DiffCallback){
    companion object DiffCallback: DiffUtil.ItemCallback <Image>(){
        override fun areItemsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Image, newItem: Image): Boolean {
            return oldItem.id == newItem.id
        }
    }
    class ImageViewHolder(private var binding : ImageItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(clickListener: ImagesListListener, image: Image){
            binding.image = image
            binding.clickListener = clickListener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        return ImageViewHolder(ImageItemBinding.inflate(LayoutInflater.from(parent.context)))
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val image =  getItem(position)
        holder.bind(clickListener, image)
    }
}

class ImagesListListener(val clickListener: (image: Image) -> Unit){
    fun onClick(image: Image) = clickListener(image)

}