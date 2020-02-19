package com.sound.rem.viewmodel

import android.app.Application
import android.text.method.SingleLineTransformationMethod
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.net.PlacesClient
import com.sound.rem.database.REM_Database
import com.sound.rem.database.repository.PicturePropRepo
import com.sound.rem.database.repository.PropertyRepo
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import io.reactivex.Maybe
import io.reactivex.Observable
import kotlinx.coroutines.launch

class REM_Database_ViewModel (application: Application) : AndroidViewModel(application) {

    private val propertyRepo: PropertyRepo
    private val picturePropRepo: PicturePropRepo

    lateinit var placesClient:PlacesClient

    val allPropertys : LiveData<List<Property>>
    val actualProperty = MutableLiveData<Property>()

    init
    {
        val propertyDao = REM_Database.getDatabase(application, viewModelScope).propertyDAO()
        val picturePropDao = REM_Database.getDatabase(application, viewModelScope).pictureDAO()

        propertyRepo = PropertyRepo(propertyDao)
        picturePropRepo = PicturePropRepo(picturePropDao)

        allPropertys = propertyRepo.allPropertys
    }

    fun addProperty(property: Property): Maybe<Long> {
        return propertyRepo.create(property)
    }

    fun getPropertyById(propertyId: Long): Maybe<Property>{
        return propertyRepo.getProperty(propertyId)
    }

    fun addPhotos(photos:List<PictureProp>) = viewModelScope.launch{
        photos.forEach{picturePropRepo.addPhoto(it)}
    }

    fun getAllPics (propertyId: Long): Maybe<List<PictureProp>>{
        return picturePropRepo.getPhotosProp(propertyId)
    }


}