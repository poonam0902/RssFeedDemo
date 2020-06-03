package com.app.myapplication

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.myapplication.databinding.FragmentRssItemBinding
import com.bumptech.glide.request.RequestOptions


class MyItemRecyclerViewAdapter(
    private val mValues: List<RssItem>,
    private val context : FragmentActivity?,
    private val host : Fragment?
   ) : RecyclerView.Adapter<MyItemRecyclerViewAdapter.ViewHolder>() {


    private var mListener: OnItemClickListener<RssItem>? = null

    fun setItemClickListener(mListener: OnItemClickListener<RssItem>?) {
        this.mListener = mListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: FragmentRssItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.fragment_rss_item, parent, false
        )


        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = mValues[position]

        holder.bind(item)
        holder.itemViewpagerBinding.setClickListener(mListener)

    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder internal constructor(var itemViewpagerBinding: FragmentRssItemBinding) :
        RecyclerView.ViewHolder(itemViewpagerBinding.root) {
        fun bind(obj: Any?) {
            itemViewpagerBinding.setVariable(BR.model, obj)
            itemViewpagerBinding.executePendingBindings()

        }init {
            itemViewpagerBinding.root.setOnClickListener {
               mListener!!.onItemClick(it, RssItem(), position  )

            }
        }
    }

}

@BindingAdapter(value = ["imageUrl"])
fun   showImage(imageView: ImageView, imageUrl: String?) {
    val requestOptions = RequestOptions()
        .error(R.drawable.jobs_place_holder)
        .placeholder(R.drawable.jobs_place_holder)
        .centerCrop()
    GlideApp.with(imageView.context)
        .setDefaultRequestOptions(requestOptions)
        .load(imageUrl)
        .into(imageView)
}