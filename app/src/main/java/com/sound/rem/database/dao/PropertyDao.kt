package com.sound.rem.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.sound.rem.models.Property
import io.reactivex.Maybe
import io.reactivex.Observable

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPropertyForID(property: Property): Maybe<Long>

    @Query("SELECT * FROM Property")
    fun getAllPropertys(): LiveData<List<Property>>

    @Query("SELECT * FROM Property WHERE idProperty = :propertyId")
    fun getProperty(propertyId: Long): Maybe<Property>

    @Update
    suspend fun updateProperty(property: Property)

    @Query("DELETE FROM Property WHERE idProperty = :propertyId")
    suspend fun deleteProperty(propertyId: Long)

    @Query("DELETE FROM Property")
    suspend fun deleteAll()
}