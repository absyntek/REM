package com.sound.rem.database.repository

import com.sound.rem.database.dao.PropertyDao
import com.sound.rem.models.Property
import io.reactivex.Flowable
import io.reactivex.Single

class PropertyRepo(private val propertyDAO: PropertyDao){

    val allPropertys: Flowable<List<Property>> = propertyDAO.getAllPropertys()

    fun create (property: Property) : Single<Long> {
        return propertyDAO.createPropertyForID(property)
    }

    fun getProperty (propertyId: Long): Single<Property>{
        return propertyDAO.getProperty(propertyId)
    }
}