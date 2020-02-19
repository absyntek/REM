package com.sound.rem.models

import androidx.room.*
import com.google.android.gms.maps.model.LatLng
import java.net.URI
import java.util.*

@Entity(indices = arrayOf(Index(value = ["idProperty"])))
class Property
    (@PrimaryKey(autoGenerate = true)
     var idProperty: Long? = null,
     var propertyKind: String,
     var description: String?,
     var adresse: String,
     var city:String,
     var country:String,
     var lat: Double?,
     var lng:Double?,
     var price: Int,
     var isDollar: Boolean,
     var surface: Int,
     var nbrRooms: Int,
     var entrance: String,
     var soldDate: String? = null)