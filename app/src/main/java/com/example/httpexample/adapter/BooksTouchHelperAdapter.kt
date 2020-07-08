package com.example.httpexample.adapter

import androidx.recyclerview.widget.ItemTouchHelper

/**
 * Notifies a View Holder of relevant callbacks from
 * [ItemTouchHelper.Callback].
 */
interface BooksTouchHelperAdapter {

    /**
     * Called when the [ItemTouchHelper] first registers an
     * item was swiped to left.
     */
    fun onItemDismiss(position: Int)

    /**
     * Called when the [ItemTouchHelper] first registers an
     * item was swiped to right.
     */
    fun onItemChange(position: Int)
}