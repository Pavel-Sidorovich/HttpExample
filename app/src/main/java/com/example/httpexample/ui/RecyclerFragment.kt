package com.example.httpexample.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpexample.adapter.BooksTouchHelperCallback
import com.example.httpexample.MainActivity
import com.example.httpexample.api.BooksApiImpl
import com.example.httpexample.databinding.FragmentRecyclerBinding
import kotlinx.coroutines.runBlocking

class RecyclerFragment : Fragment() {

    private var _binding: FragmentRecyclerBinding? = null
    private val binding: FragmentRecyclerBinding
        get() = _binding!!

    private val mainActivity by lazy { this.activity as MainActivity }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRecyclerBinding.inflate(inflater)

        binding.fab.setOnClickListener {
            mainActivity.replaceFragment(AddFragment())
        }

        val touchHelperCallback =
            BooksTouchHelperCallback(mainActivity.booksAdapter)

        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(binding.recycler)

        binding.recycler.apply {
            adapter = mainActivity.booksAdapter
            layoutManager = LinearLayoutManager(mainActivity)
        }
        runBlocking {
            mainActivity.booksAdapter.updateBooks(BooksApiImpl.getBooksAndShowIt())
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    companion object {
        const val ENDPOINT = "http://10.0.2.2:3000/"  // Im using json-server running on my localhost and emulator
        const val BOOKS_URI = "books"
        const val CHANGE_ID = "changeID"
        const val CHANGE_TITLE = "changeTitle"
    }
}