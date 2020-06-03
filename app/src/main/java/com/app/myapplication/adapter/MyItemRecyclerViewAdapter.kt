package com.app.myapplication.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.app.myapplication.R
import com.app.myapplication.databinding.FragmentRssItemBinding
import com.app.myapplication.listener.OnItemClickListener
import com.app.myapplication.model.RssItem
import com.app.myapplication.utils.GlideApp
import com.bumptech.glide.request.RequestOptions
import androidx.databinding.library.baseAdapters.BR


class MyItemRecyclerViewAdapter(
    private val mValues: List<RssItem>
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
        holder.itemViewpagerBinding.clickListener = mListener

    }

    override fun getItemCount(): Int = mValues.size


    inner class ViewHolder internal constructor(var itemViewpagerBinding: FragmentRssItemBinding) :
        RecyclerView.ViewHolder(itemViewpagerBinding.root) {
        fun bind(obj: Any?) {
            itemViewpagerBinding.setVariable(BR.model, obj)
            itemViewpagerBinding.executePendingBindings()
        }

        init {
            itemViewpagerBinding.root.setOnClickListener {
                mListener!!.onItemClick(it, RssItem(), position)

            }
        }
    }

}

/**
 * parsing image url into imageview
 */
@BindingAdapter(value = ["imageUrl"])
fun showImage(imageView: ImageView, imageUrl: String?) {
    val requestOptions = RequestOptions()
        .error(R.drawable.jobs_place_holder)
        .placeholder(R.drawable.jobs_place_holder)
        .centerCrop()
    GlideApp.with(imageView.context)
        .setDefaultRequestOptions(requestOptions)
        .load(imageUrl)
        .into(imageView)
}