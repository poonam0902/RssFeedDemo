package com.app.myapplication.listener

import android.view.View

interface OnItemClickListener<T> {
    fun onItemClick(view: View?, feed: T, position: Int)

}