package com.example.photomanagementapp.camera

import android.Manifest
import android.content.ContentValues
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Surface.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.photomanagementapp.databinding.ActivityCameraBinding
import java.io.File


class CameraActivity : AppCompatActivity() {
    private lateinit var binding : ActivityCameraBinding
    private lateinit var imageCapture : ImageCapture
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding.root)
        requestCameraPermissions()
        requestStoragePermissions()
        binding.takePicFab.setOnClickListener {
            onClickTakePicture()
        }
    }

    private fun requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(applicationContext, "Starting Camera", Toast.LENGTH_SHORT).show()
            startCamera()
        }
        else{
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.CAMERA), requestCodeCamera)
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

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            requestCodeCamera ->{
                if (grantResults.isNotEmpty() && grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(applicationContext, "Starting Camera", Toast.LENGTH_SHORT).show()
                    startCamera()
                }
                else{

                }
            }
        }
    }

    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(applicationContext)
        processCameraProvider.addListener({
            try {
                val cameraProvider = processCameraProvider.get()
                val previewUseCase = Preview.Builder().build()
                previewUseCase
                    .setSurfaceProvider(binding.cameraPreview.surfaceProvider)



                imageCapture = ImageCapture.Builder()
                    .setTargetRotation(this.display?.rotation ?: ROTATION_0)
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(this, CameraSelector.DEFAULT_BACK_CAMERA,imageCapture, previewUseCase)
            }
            catch (e: Exception){
                Log.d("Error", e.message.toString())
            }

        }, ContextCompat.getMainExecutor(applicationContext))
    }

    fun onClickTakePicture() {
        if (checkStoragePermission()){
            val values : ContentValues = ContentValues()
            values.put(MediaStore.Images.Media.DATE_TAKEN , System.currentTimeMillis())
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            values.put(MediaStore.Images.Media.DISPLAY_NAME, System.currentTimeMillis())
            val outputFileOptions = ImageCapture.OutputFileOptions.Builder(applicationContext.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values).build()
            imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(applicationContext),
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(error: ImageCaptureException)
                    {
                        Toast.makeText(applicationContext, "Failed to Save Pic", Toast.LENGTH_LONG).show()
                        Log.d("Error", error.message.toString())
                    }
                    override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                        // insert your code here.
                        Toast.makeText(applicationContext, "Successfully Saved Pic at ${outputFileResults.savedUri.toString()}",
                            Toast.LENGTH_LONG).show()

                    }
                })
        }

    }

}

val requestCodeCamera = 1
val requestCodeStorage = 2