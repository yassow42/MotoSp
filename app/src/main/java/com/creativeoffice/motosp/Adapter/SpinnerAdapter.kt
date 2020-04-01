package com.creativeoffice.motosp.Adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.BaseAdapter

import com.creativeoffice.motosp.Datalar.SpinnerData
import com.creativeoffice.motosp.R
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.spinner_item.view.*


class SpinnerAdapter(val myContext: Context?, val mData: ArrayList<SpinnerData>) : BaseAdapter() {
    private val mInflater: LayoutInflater

    init {
        mInflater = LayoutInflater.from(myContext)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: ViewHolder
        var view = convertView

        if (view == null) {
            view = mInflater.inflate(R.layout.spinner_item, parent, false)
            viewHolder = ViewHolder(view)
        } else {
            viewHolder = view.tag as ViewHolder
        }

        view?.tag = viewHolder


        viewHolder.tvMarka.text = mData[position].marka
        Picasso.get().load(mData[position].resim.toString().toInt()).into(viewHolder.resim)

        return view
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        var viewHolder: DropDownViewHolder
        var view = convertView

        if (view == null) {
            view = mInflater.inflate(R.layout.spinner_item_drop, parent, false)
            viewHolder = DropDownViewHolder(view)
        } else {
            viewHolder = view.tag as DropDownViewHolder
        }

        view?.tag = viewHolder


        viewHolder.tvMarka.text = mData[position].marka
        Picasso.get().load(mData[position].resim.toString().toInt()).into(viewHolder.resim)
        return view
    }

    override fun getItem(position: Int): Any = mData[position]

    override fun getItemId(position: Int): Long = position.toLong()

    override fun getCount(): Int = mData.size


    class ViewHolder(view: View) {
        var tvMarka = view.tvMarka
        var resim = view.imgMarka

    }

    class DropDownViewHolder(view: View){
        var tvMarka = view.tvMarka
        var resim = view.imgMarka
    }
}