package com.example.photomanagementapp

import android.Manifest
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.photomanagementapp.camera.CameraActivity
import com.example.photomanagementapp.databinding.ActivityMainBinding
import com.example.photomanagementapp.image_detail.ImageDetailActivity
import com.example.photomanagementapp.img_list.ImageListAdapter
import com.example.photomanagementapp.img_list.ImagesListListener
import com.example.photomanagementapp.model.Image
import com.squareup.picasso.Picasso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ViewModel
    private lateinit var adapter: ImageListAdapter
    var images = mutableListOf<Image>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        adapter = ImageListAdapter(ImagesListListener {
            val intent = Intent(this, ImageDetailActivity::class.java)
            intent.putExtra("imageUri", "${it.imgUri}")
            startActivity(intent)
        })
        binding.lifecycleOwner = this
        binding.gridLayout.adapter = adapter
        binding.gridLayout.layoutManager = GridLayoutManager(applicationContext, 2)
        requestStoragePermissions()
        lifecycleScope.launch {
            if (checkStoragePermission()){
                images = loadPhotos().toMutableList()
                adapter.submitList(images)
            }
        }
        binding.fab.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenResumed {
            if (checkStoragePermission()){
                images = loadPhotos().toMutableList()
                adapter.submitList(images)
                adapter.notifyDataSetChanged()
            }
        }

    }

    private fun requestStoragePermissions(){
        val readPermission = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val writePermission = (ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) || minSdk
        val permissionsArray = mutableListOf<String>()
        if (!readPermission)
            permissionsArray.add(Manifest.permission.READ_EXTERNAL_STORAGE)
        if (!writePermission)
            permissionsArray.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permissionsArray.isNotEmpty()){
            ActivityCompat.requestPermissions(this, permissionsArray.toTypedArray(), requestCodeStorage)
        }
    }
    private fun checkStoragePermission(): Boolean{
        val readPermission = ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
        val minSdk = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
        val writePermission = (ContextCompat.checkSelfPermission(applicationContext,
            Manifest.permission.WRITE_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED) || minSdk

        return readPermission && writePermission
    }


    private suspend fun loadPhotos() : List<Image>{
        return withContext(Dispatchers.IO){
            val sdk29Up = Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q
            val collection = if (sdk29Up){
                MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
            } else{
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
            }

            val projection = arrayOf(
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.DISPLAY_NAME)

            val photos = mutableListOf<Image>()

            applicationContext.contentResolver.query(
                collection,
                projection,
                null,
                null,
                "${MediaStore.Images.Media.DATE_TAKEN} DESC"
            )?.use { cursor ->
                val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
                val dispalyNameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)

                while (cursor.moveToNext()){
                    val id = cursor.getLong(idColumn)
                    val displayName = cursor.getString(dispalyNameColumn)

                    val contentUri : Uri = ContentUris.appendId(
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI.buildUpon(),
                        id
                    ).build()
                    photos.add(Image(id, contentUri))
                }
                photos.toList()
            }?: listOf()
        }
    }
}

val requestCodeStorage =  2