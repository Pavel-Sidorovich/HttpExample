package com.example.httpexample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpexample.adapter.BooksAdapter
import com.example.httpexample.adapter.BooksTouchHelperCallback
import com.example.httpexample.MainActivity
import com.example.httpexample.databinding.FragmentRecyclerBinding
import com.example.httpexample.model.Book
import org.json.JSONArray
import java.io.InputStreamReader
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

class RecyclerFragment : Fragment() {

    private var _binding: FragmentRecyclerBinding? = null
    private val binding: FragmentRecyclerBinding
        get() = _binding!!

    private val changeListener: (Book) -> Unit = {
        val fragment = AddFragment()
        val bundle = Bundle()

        bundle.apply {
            putInt(CHANGE_ID, it.id)
            putString(CHANGE_TITLE, it.title)
        }
        fragment.arguments = bundle
        mainActivity.replaceFragment(fragment)
    }

    private val booksAdapter =
        BooksAdapter(changeListener) {
            Thread {
                removeBook(it.id)
                getBooksAndShowIt()
            }.start()
        }

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
            BooksTouchHelperCallback(booksAdapter)

        val touchHelper = ItemTouchHelper(touchHelperCallback)
        touchHelper.attachToRecyclerView(binding.recycler)

        binding.recycler.apply {
            adapter = booksAdapter
            layoutManager = LinearLayoutManager(mainActivity)
        }

        Thread {
            getBooksAndShowIt()
        }.start()

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

    /**
     * Function to get all records from the database.
     */

    @WorkerThread
    private fun getBooksAndShowIt() {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "GET"
                doInput = true
            }
            if (httpUrlConnection.responseCode != HttpURLConnection.HTTP_OK) {
                Toast.makeText(mainActivity, "Response code is ${httpUrlConnection.responseCode}", Toast.LENGTH_SHORT).show()
                return
            }
            val streamReader = InputStreamReader(httpUrlConnection.inputStream)
            var text = ""
            streamReader.use {
                text = it.readText()
            }

            val books = mutableListOf<Book>()
            val json = JSONArray(text)
            for (i in 0 until json.length()) {
                val jsonBook = json.getJSONObject(i)
                val title = jsonBook.getString(TITLE)
                val id = jsonBook.getInt(ID)
                books.add(Book(title, id))
            }

            Handler(Looper.getMainLooper()).post {
                booksAdapter.updateBooks(books)
            }
        } catch (exc: Exception) {
            Log.e(TAG, "getBooksAndShowIt", exc)
        } finally {
            httpUrlConnection?.disconnect()
        }
    }

    /**
     * Function for delete book.
     * @param id is a book id.
     */
    @WorkerThread
    private fun removeBook(id: Int) {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI + "/${id}").openConnection() as HttpURLConnection
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "DELETE"
                setRequestProperty("Content-Type", "application/json")
            }
            httpUrlConnection.responseCode
        } catch (exc: Exception) {
            Log.e(TAG, "removeBook", exc)
        } finally {
            httpUrlConnection?.disconnect()
        }
    }

    companion object {
        const val ENDPOINT = "http://10.0.2.2:3000"  // Im using json-server running on my localhost and emulator
        const val BOOKS_URI = "/books"
        const val TITLE = "title"
        const val CHANGE_ID = "changeID"
        const val CHANGE_TITLE = "changeTitle"
        private const val ID = "id"
        private const val TAG = "RecyclerFragment"
    }
}