package com.sound.rem.ui.fragment.new_property_frag

import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.sound.rem.models.PictureProp

class NewPropertyViewModel : ViewModel(){
    var photoList: ArrayList<PictureProp> = arrayListOf()
    var latLng:LatLng? = null
}