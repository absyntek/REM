package com.sound.rem.services.currencys_change

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LiveCurrency {

    private val BASE_URL = "http://apilayer.net/api/"

    private val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    val apiService: CurrencyApi
        get() = retrofitInstance.create(CurrencyApi::class.java)


    init {
        val api = apiService
        val call = api.usdToEur
        call.enqueue(object : Callback<CurrencyModel> {
            override fun onResponse(call: Call<CurrencyModel>, response: Response<CurrencyModel>) {
                if (response.isSuccessful) {
                    val usd_eur = response.body()!!.quotes?.get("USDEUR")
                }
            }

            override fun onFailure(call: Call<CurrencyModel>, t: Throwable) {

            }
        })
    }
}