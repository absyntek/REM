package com.sound.rem.services.currencys_change

import com.google.gson.annotations.SerializedName

class CurrencyModel {

    @SerializedName("success")
    var succes: Boolean? = null

    @SerializedName("quotes")
    var quotes: Map<String, Double>? = null
}
