package com.example.httpexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.httpexample.MainActivity
import com.example.httpexample.R
import com.example.httpexample.api.BooksApiImpl
import com.example.httpexample.databinding.FragmentAddBinding
import com.example.httpexample.model.Book
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_ID
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_TITLE
import kotlinx.coroutines.runBlocking

class AddFragment : Fragment() {

    private var _binding: FragmentAddBinding? = null
    private val binding: FragmentAddBinding
        get() = _binding!!

    private val mainActivity by lazy { this.activity as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAddBinding.inflate(inflater)

        // Check it's a request for update book or create new book
        if (arguments != null) {
            binding.title.setText(arguments!!.getString(CHANGE_TITLE))
            binding.addButton.text = getString(R.string.change_title)
            binding.addButton.setOnClickListener {
                val title = binding.title.text.toString()
                runBlocking {
                    BooksApiImpl.changeBook(arguments!!.getInt(CHANGE_ID), title)
                }
                mainActivity.onBackPressed()
            }
        } else {
            binding.addButton.setOnClickListener {
                val title = binding.title.text.toString()
                val id = mainActivity.booksAdapter.getLastId()
                runBlocking {
                    BooksApiImpl.addBook(Book(title, id + 1))
                }
                mainActivity.onBackPressed()
            }
        }

        return binding.root
    }

    override fun onStart() {
        super.onStart()
        mainActivity.supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
        }
    }

    override fun onStop() {
        super.onStop()
        mainActivity.supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}