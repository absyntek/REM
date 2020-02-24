package com.sound.rem.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.sqlite.db.SupportSQLiteQuery
import com.sound.rem.database.REM_Database
import com.sound.rem.database.repository.PicturePropRepo
import com.sound.rem.database.repository.PropertyRepo
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import com.sound.rem.ui.fragment.property_list_frag.PropertyListAdapter
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class REM_Database_ViewModel (application: Application) : AndroidViewModel(application) {

    lateinit var listener: PropertyListAdapter.OnItemClickListener

    private val propertyRepo: PropertyRepo
    private val picturePropRepo: PicturePropRepo

    val allPropertys : Flowable<List<Property>>
    val actualProperty : BehaviorSubject<Property>
    val isDollar: BehaviorSubject<Boolean>
    val searchResultProperty: BehaviorSubject<List<Property>>
    var usdEur:Double = 1.08
    var mapLite:Boolean = true

    init
    {
        val propertyDao = REM_Database.getDatabase(application, viewModelScope).propertyDAO()
        val picturePropDao = REM_Database.getDatabase(application, viewModelScope).pictureDAO()

        propertyRepo = PropertyRepo(propertyDao)
        picturePropRepo = PicturePropRepo(picturePropDao)

        isDollar = BehaviorSubject.create()
        isDollar.onNext(true)
        actualProperty = BehaviorSubject.create()
        searchResultProperty = BehaviorSubject.create()
        allPropertys = propertyRepo.allPropertys
    }

    fun addProperty(property: Property): Single<Long> {
        return propertyRepo.create(property)
    }

    fun getPropertyById(propertyId: Long): Single<Property>{
        return propertyRepo.getProperty(propertyId)
    }

    fun addPhotos(photos:List<PictureProp>): Single<List<Long>>{
        return picturePropRepo.addPhotos(photos)
    }

    fun getAllPics (propertyId: Long): Maybe<List<PictureProp>>{
        return picturePropRepo.getPhotosProp(propertyId)
    }

    fun searchProperty(query: SupportSQLiteQuery):Single<List<Property>>{
        return propertyRepo.searchProperty(query)
    }
}