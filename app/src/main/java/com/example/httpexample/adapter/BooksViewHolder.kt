package com.example.httpexample.adapter

import android.graphics.Color
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.httpexample.model.Book
import kotlinx.android.synthetic.main.book_view.view.*

class BooksViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
    BooksTouchHelperViewHolder {
    private val id = itemView.book_id
    private val title = itemView.book_title

    fun bind (book: Book) {
        id.text = book.id.toString()
        title.text = book.title
    }

    override fun onItemSelected() {
        itemView.setBackgroundColor(Color.LTGRAY)
    }

    override fun onItemClear() {
        itemView.setBackgroundColor(0)
    }
}