package com.sound.rem.ui.ui_utils

import android.content.Context
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.sound.rem.R
import com.sound.rem.models.PictureProp
import java.io.File

class SliderAdapter(private var context : Context, private var images: List<PictureProp>) : PagerAdapter(){


    lateinit var layout:LayoutInflater


    override fun isViewFromObject(view: View, `object`: Any): Boolean{
        return view === `object`
    }

    override fun getCount(): Int {
        return images.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        layout = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layout.inflate(R.layout.custom_slider,container,false)

        val uriPhoto: Uri = Uri.parse(images[position].pathPicProp)

        val tvPositionSlider :TextView = view.findViewById(R.id.tvPositionSlider)
        val image: ImageView = view.findViewById(R.id.slider_images)

        tvPositionSlider.text = ((position+1).toString() + "/" + images.size.toString())

        if (images.isNullOrEmpty()) Glide.with(view).load(R.drawable.no_photo).into(image)
        else Glide.with(view).load(File(uriPhoto.path!!)).into(image)

        container.addView(view)
        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {

        container.removeView(`object` as CardView)
    }
}