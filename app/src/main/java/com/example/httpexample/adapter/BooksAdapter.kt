package com.example.httpexample.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.httpexample.R
import com.example.httpexample.model.Book

class BooksAdapter(
    private val changeListener: (Book) -> Unit,
    private val swipeListener: (Book) -> Unit
): RecyclerView.Adapter<BooksViewHolder>(),
    BooksTouchHelperAdapter {

    private var books = listOf<Book>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BooksViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return BooksViewHolder(
            inflater.inflate(
                R.layout.book_view,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = books.size

    override fun onBindViewHolder(holder: BooksViewHolder, position: Int) {
        holder.bind(book = books[position])
    }

    fun updateBooks(newBooks: List<Book>) {
        books = newBooks
        notifyDataSetChanged()
    }

    override fun onItemDismiss(position: Int) {
        swipeListener.invoke(books[position])
    }

    override fun onItemChange(position: Int) {
        changeListener.invoke(books[position])
    }
}