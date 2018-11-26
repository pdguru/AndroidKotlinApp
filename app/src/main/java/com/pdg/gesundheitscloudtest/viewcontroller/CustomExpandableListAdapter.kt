package com.pdg.gesundheitscloudtest.viewcontroller

import android.content.Context
import android.icu.text.SimpleDateFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.bumptech.glide.Glide
import com.pdg.gesundheitscloudtest.R
import com.pdg.gesundheitscloudtest.model.SearchResultItem
import java.util.*

class CustomExpandableListAdapter(private val context: Context, private val arrayValues: ArrayList<SearchResultItem>):
    BaseExpandableListAdapter(){

    override fun getGroup(groupPosition: Int): Any {
        return groupPosition
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return false
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    override fun getGroupView(groupPosition: Int, isExpanded: Boolean, convertView: View?, parent: ViewGroup?): View {
        val rowView: View

        if(convertView==null) {
            val inflater = LayoutInflater.from(context)
            rowView = inflater.inflate(R.layout.main_list_row, parent, false)
        }else {
            rowView = convertView
        }

        val songTV = rowView.findViewById<TextView>(R.id.row_songName)
        songTV.text = arrayValues[groupPosition].trackName

        val artistTV = rowView.findViewById<TextView>(R.id.row_artistName)
        artistTV.text = arrayValues[groupPosition].artistName

        val imageView = rowView.findViewById<ImageView>(R.id.row_imageView)
        Glide.with(context).load(arrayValues[groupPosition].artworkUrl60).placeholder(R.drawable.loading).into(imageView)

        return rowView
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        return 1
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return arrayValues[groupPosition]
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        convertView: View?,
        parent: ViewGroup?
    ): View {
        val childRowView: View

        if(convertView==null) {
            val inflater = LayoutInflater.from(context)
            childRowView = inflater.inflate(R.layout.main_list_subrow, parent, false)
        }else {
            childRowView = convertView
        }

        val albumTV = childRowView.findViewById<TextView>(R.id.childrow_album)
        albumTV.text = "Album: "+arrayValues[groupPosition].collectionName

        val releaseTV = childRowView.findViewById<TextView>(R.id.childrow_releaseDate)
        var date =
            SimpleDateFormat("yyyy-MM-dd").format(
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'").parseObject(arrayValues[groupPosition].releaseDate)).toString()
        releaseTV.text = "Released on: "+date

        val genreTV = childRowView.findViewById<TextView>(R.id.childrow_genre)
        genreTV.text = "Genre: "+arrayValues[groupPosition].primaryGenreName

        val lengthTV = childRowView.findViewById<TextView>(R.id.childrow_songLength)
        var len = SimpleDateFormat("mm:ss").format(arrayValues[groupPosition].trackTimeMillis).toString()
        lengthTV.text = "Length: "+len+"m"

        val priceTV = childRowView.findViewById<TextView>(R.id.childrow_price)
        priceTV.text = "Price: "+arrayValues[groupPosition].currency+" "+arrayValues[groupPosition].trackPrice

        return childRowView
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun getGroupCount(): Int {
        return arrayValues.size
    }
}