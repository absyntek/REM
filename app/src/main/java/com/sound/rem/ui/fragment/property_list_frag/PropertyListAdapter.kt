package com.sound.rem.ui.fragment.property_list_frag

import android.content.Context
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.sound.rem.R
import com.sound.rem.models.PictureProp
import com.sound.rem.models.Property
import com.sound.rem.viewmodel.REM_Database_ViewModel
import io.reactivex.Maybe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import java.io.File

class PropertyListAdapter internal constructor(context: Context, val adapterOnClick: OnItemClickListener) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var propertys = emptyList<Property>() // Cached copy of propertys
    private lateinit var dbViewModel: REM_Database_ViewModel
    private lateinit var photoUri: Uri
    private val compositeDisposable = CompositeDisposable()

    inner class PropertyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPrice: TextView = itemView.findViewById(R.id.tvPrice)
        val tvAdresse: TextView = itemView.findViewById(R.id.tvAdresse)
        val tvKind: TextView = itemView.findViewById(R.id.tvType)
        val imgListProperty: ImageView = itemView.findViewById(R.id.imgListProperty)
        val tvNbrPic: TextView = itemView.findViewById(R.id.tvNumberPicPropList)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val itemView = inflater.inflate(R.layout.fragment_property, parent, false)
        return PropertyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val current = propertys[position]
        holder.tvPrice.text = current.price.toString().plus(" €") //TODO check if $ or €
        holder.tvAdresse.text = current.city
        holder.tvKind.text = current.propertyKind.plus(" . ").plus(current.surface.toString()).plus(" m²")

        dbViewModel.getAllPics(current.idProperty!!)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnError{Log.e(this.toString(), it.message!!)}
            .doOnSuccess {list ->
                holder.tvNbrPic.text = list.size.toString()
                if (!list.isNullOrEmpty()){
                        list.forEach {
                            if (it.isTopPic!!){
                                photoUri = Uri.parse(it.pathPicProp)
                                Glide.with(holder.imgListProperty).load(File(photoUri.path!!)).into(holder.imgListProperty)
                            }
                        }
                }else Glide.with(holder.imgListProperty).load(R.drawable.no_photo).into(holder.imgListProperty)
            }
            .subscribe().addTo(compositeDisposable)

        holder.itemView.setOnClickListener {
            adapterOnClick.onClick(current)
        }
    }

    internal fun setPropertys(property: List<Property>) {
        this.propertys = property
        notifyDataSetChanged()
    }

    internal fun setViewModel(dbViewModel: REM_Database_ViewModel) {
        this.dbViewModel = dbViewModel
    }

    override fun getItemCount() = propertys.size

    interface OnItemClickListener {
        fun onClick(item: Property)
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        compositeDisposable.clear()
        super.onDetachedFromRecyclerView(recyclerView)
    }
}