package com.sound.rem.database.dao

import androidx.room.*
import com.sound.rem.models.PictureProp
import io.reactivex.Maybe

@Dao
interface PicturePropDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhoto(pictureProp: PictureProp)

    @Query("SELECT * FROM PictureProp WHERE idProperty = :propertyId")
    fun getPhotos(propertyId:Long): Maybe<List<PictureProp>>

    @Query("SELECT * FROM PictureProp WHERE idProperty = :propertyId AND isTopPic = 1")
    fun getMainPhoto(propertyId:Long): Maybe<PictureProp>

    @Update
    suspend fun updatePhoto(pictureProp: PictureProp)

    @Query("DELETE FROM PictureProp WHERE idProperty = :propertyId")
    suspend fun deletePropertyPhotos(propertyId:Long)

    @Query("DELETE FROM PictureProp WHERE pathPicProp = :picturePropId")
    suspend fun deleteOnePhoto(picturePropId: String)

    @Query("DELETE FROM PictureProp")
    suspend fun deleteAll()
}