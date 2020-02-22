package com.sound.rem.database.repository

import com.sound.rem.database.dao.PicturePropDao
import com.sound.rem.models.PictureProp
import io.reactivex.Maybe
import io.reactivex.Single

class PicturePropRepo (private val picturePropDao: PicturePropDao){

    suspend fun addPhoto(pictureProp: PictureProp){
        picturePropDao.addPhoto(pictureProp)
    }

    fun addPhotos (pictureProp: List<PictureProp>): Single<List<Long>>{
        return picturePropDao.addPhotos(pictureProp)
    }

    fun getPhotosProp(propertyId : Long): Maybe<List<PictureProp>>{
        return picturePropDao.getPhotos(propertyId)
    }

    fun getMainPhoto(propertyId : Long): Maybe<PictureProp>{
        return picturePropDao.getMainPhoto(propertyId)
    }

    suspend fun deletePropPhotos (propertyId : Long){
        //TODO delete on device asWell
        picturePropDao.deletePropertyPhotos(propertyId)
    }

    suspend fun deleteOnePhotos (photoId : String){
        //TODO delete on device asWell
        picturePropDao.deleteOnePhoto(photoId)
    }
}