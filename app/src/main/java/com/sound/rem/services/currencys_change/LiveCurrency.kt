package com.sound.rem.services.currencys_change

import android.content.Context
import android.content.SharedPreferences
import com.sound.rem.ui.FrontActivity
import com.sound.rem.utlis.Utils
import com.sound.rem.viewmodel.REM_Database_ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class LiveCurrency(activity: FrontActivity, val dbViewModel: REM_Database_ViewModel) {

    private val BASE_URL = "http://apilayer.net/api/"
    private val CURRENCY = "USDEUR"
    private val sharedPref:SharedPreferences = activity.getPreferences(Context.MODE_PRIVATE)

    private val retrofitInstance: Retrofit
        get() = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    val apiService: CurrencyApi
        get() = retrofitInstance.create(CurrencyApi::class.java)


    init {
        apiService.usdToEur.enqueue(object : Callback<CurrencyModel> {
            override fun onResponse(call: Call<CurrencyModel>, response: Response<CurrencyModel>) {
                if (response.isSuccessful) {
                    val usdEur = response.body()!!.quotes?.get(CURRENCY)
                    if (usdEur != null) saveResultToSharedPref(usdEur.toString())
                    dbViewModel.usdEur = usdEur!!
                }
            }

            override fun onFailure(call: Call<CurrencyModel>, t: Throwable) {
                val tmp = sharedPref.getString(CURRENCY,"1.08")
                dbViewModel.usdEur = tmp!!.toDouble()
            }
        })
    }

    fun saveResultToSharedPref(usd_eur:String){
        val setOfString:Set<String> = setOf(Utils.todayDate,usd_eur)
        sharedPref.edit().putStringSet(CURRENCY,setOfString).apply()
    }
}