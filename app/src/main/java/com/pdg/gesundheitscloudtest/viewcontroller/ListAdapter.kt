package com.pdg.gesundheitscloudtest.viewcontroller

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView
import com.pdg.gesundheitscloudtest.R
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import java.util.ArrayList

class ListAdapter(private val context: Context, private val arrayValues: ArrayList<SearchResultItem>) : BaseAdapter() {

    override fun getItem(position: Int): Any {
        return arrayValues.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position as Long
    }

    override fun getCount(): Int {
        return arrayValues.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val inflater = LayoutInflater.from(context)
        val rowView = inflater.inflate(R.layout.main_list_row, parent, false)

        val nameTV = rowView.findViewById<TextView>(R.id.row_songName)
        nameTV.text = arrayValues[position].trackName

        val subtitleTV = rowView.findViewById<TextView>(R.id.row_artistName)
        subtitleTV.text = arrayValues[position].artistName

        val imageView = rowView.findViewById<ImageView>(R.id.row_imageView)


        return rowView
    }
}