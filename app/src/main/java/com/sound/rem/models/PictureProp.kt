package com.sound.rem.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ ForeignKey( entity = Property::class, parentColumns = ["idProperty"], childColumns = ["idProperty"])], indices = [(Index(value = ["idProperty"]))])
class PictureProp (
    @PrimaryKey
    var pathPicProp: String,
    var idProperty: Long,
    var isTopPic: Boolean? = false
)