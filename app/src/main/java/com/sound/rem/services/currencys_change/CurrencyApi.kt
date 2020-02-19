package com.sound.rem.services.currencys_change

import retrofit2.Call
import retrofit2.http.GET

interface CurrencyApi {

    @get:GET("live?access_key=69cabbb6b94c2630c3453bb06d5a99a9&currencies=EUR")
    val usdToEur: Call<CurrencyModel>
}
