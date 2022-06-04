package com.example.mapbox_kotlin

import android.content.Context
import android.icu.util.UniversalTimeScale.toLong
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import com.example.mapbox_kotlin.model.AreaWithPoint


class CustomArrayAdapter(context: Context, resource: Int, var items: List<AreaWithPoint>)
    : ArrayAdapter<AreaWithPoint>(context, resource, items) {

    val inflater: LayoutInflater = LayoutInflater.from(context)


    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as TextView).text = getItem(position)!!.area.name
        return view
    }

    override fun getDropDownView(position: Int, convertView: View, parent: ViewGroup): View {
        var view: View? = convertView
        if (view == null) {
            view = inflater.inflate(R.layout.spinner_item, parent, false)
        }
        (view?.findViewById(android.R.id.text1) as TextView).text = getItem(position)!!.area.name
        return view
    }
}