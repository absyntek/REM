package com.sound.rem.ui.util

import android.content.Context
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.sound.rem.R

class DropDownAdapter(context: Context,tvId: Int,retString:List<String>, private var adresse : List<AutocompletePrediction>): ArrayAdapter<String>(context,tvId,retString){

    override fun getCount(): Int {
        return adresse.size
    }

    override fun getItem(position: Int): String? {
        return adresse.get(position).getFullText(null).toString()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setTextView(position)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return setTextView(position)
    }

    fun setTextView(position: Int):View{
        val label = TextView(context)
        label.setTextColor(Color.BLACK)
        label.text = adresse.get(position).getFullText(null)
        return label
    }

    internal fun updateData(adresse: List<AutocompletePrediction>) {
        this.adresse = adresse
        notifyDataSetChanged()
    }
}