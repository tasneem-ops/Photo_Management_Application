package com.example.photomanagementapp

import androidx.lifecycle.MutableLiveData
import com.example.photomanagementapp.model.Image
import java.util.*

class ViewModel {

    val images = MutableLiveData<MutableList<Image>>()
//    init {
//        for (i in 0..10){
//            images.value?.add(Image(UUID.randomUUID().toString(), "https://random-d.uk/api/randomimg"))
//        }
//    }
}