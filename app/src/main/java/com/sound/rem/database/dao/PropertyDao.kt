package com.sound.rem.database.dao

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery
import com.sound.rem.models.Property
import io.reactivex.*
import io.reactivex.subjects.BehaviorSubject

@Dao
interface PropertyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createProperty(property: Property)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun createPropertyForID(property: Property): Single<Long>

    @Query("SELECT * FROM Property")
    fun getAllPropertys(): Flowable<List<Property>>


    @Query("SELECT * FROM Property WHERE idProperty = :propertyId")
    fun getProperty(propertyId: Long): Single<Property>

    @Update
    suspend fun updateProperty(property: Property)

    @Query("DELETE FROM Property WHERE idProperty = :propertyId")
    suspend fun deleteProperty(propertyId: Long)

    @Query("DELETE FROM Property")
    suspend fun deleteAll()


    //Search Query

    @RawQuery
    fun searchProperty(query:SupportSQLiteQuery): Single<List<Property>>
}