package com.sound.rem.ui.fragment.search_frag

import androidx.sqlite.db.SimpleSQLiteQuery

object UtilSearch{

    fun generateQuery(city:List<String>?, surfMin:Int?, surfMax:Int?, nbrRoom:Int?, nbrBed:Int?, priceMin:Long?, priceMax:Long?) : SimpleSQLiteQuery {
        var queryString = "SELECT * FROM Property"
        var containsCondition = false
        val args: MutableList<Any> = ArrayList()

        if (!city.isNullOrEmpty()){
            queryString += " WHERE"
            queryString += " city LIKE ?"
            args.add(city[0])
            if (city.size != 1){
                for(i in 1 until city.size){
                    args.add(city[i])
                    queryString +=" ?"
                }
            }
            containsCondition = true
        }

        if (surfMin != null || surfMax != null){
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }

            queryString += " surface"

            if (surfMax != null && surfMin !=null){
                args.add(surfMin)
                args.add(surfMax)
                queryString +=" BETWEEN ? AND ?"
            }else if (surfMax == null){
                args.add(surfMin!!)
                queryString +=" > ?"
            }else{
                args.add(surfMax)
                queryString += " < ?"
            }
        }

        if (priceMin != null || priceMax != null){
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }

            queryString += " price"

            if (priceMin != null && priceMax !=null){
                args.add(priceMin)
                args.add(priceMax)
                queryString +=" BETWEEN ? AND ?"
            }else if (priceMax == null){
                args.add(priceMin!!)
                queryString +=" > ?"
            }else{
                args.add(priceMax)
                queryString += " < ?"
            }
        }

        if (nbrRoom != null) {
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }
            queryString += " nbrRooms > ?"
            args.add(nbrRoom)
        }

        if (nbrBed != null) {
            if (containsCondition) {
                queryString += " AND"
            } else {
                queryString += " WHERE"
                containsCondition = true
            }
            queryString += " nbrRooms > ?"
            args.add(nbrBed)
        }
        return SimpleSQLiteQuery(queryString, args.toTypedArray())
    }
}