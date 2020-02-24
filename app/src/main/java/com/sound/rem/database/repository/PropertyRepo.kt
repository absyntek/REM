package com.sound.rem.database.repository

import androidx.sqlite.db.SupportSQLiteQuery
import com.sound.rem.database.dao.PropertyDao
import com.sound.rem.models.Property
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.subjects.BehaviorSubject

class PropertyRepo(private val propertyDAO: PropertyDao){

    val allPropertys: Flowable<List<Property>> = propertyDAO.getAllPropertys()

    fun create (property: Property) : Single<Long> {
        return propertyDAO.createPropertyForID(property)
    }

    fun getProperty (propertyId: Long): Single<Property>{
        return propertyDAO.getProperty(propertyId)
    }

    fun searchProperty (query: SupportSQLiteQuery): Single<List<Property>> {
        return propertyDAO.searchProperty(query)
    }
}