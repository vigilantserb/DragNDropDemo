package com.example.pagerdraganddropdemo.pagerAdapters

import com.example.pagerdraganddropdemo.adapters.Item

interface DragNDropCallback {

    fun onDropEvent(from: Item, to: Item)
}
