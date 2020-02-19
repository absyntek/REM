package com.sound.rem.database.repository

import androidx.lifecycle.LiveData
import com.sound.rem.database.dao.PropertyDao
import com.sound.rem.models.Property
import io.reactivex.Maybe
import io.reactivex.Observable

class PropertyRepo(private val propertyDAO: PropertyDao){

    val allPropertys : LiveData<List<Property>> = propertyDAO.getAllPropertys()

    fun create (property: Property) : Maybe<Long> {
        return propertyDAO.createPropertyForID(property)
    }

    fun getProperty (propertyId: Long): Maybe<Property>{
        return propertyDAO.getProperty(propertyId)
    }
}