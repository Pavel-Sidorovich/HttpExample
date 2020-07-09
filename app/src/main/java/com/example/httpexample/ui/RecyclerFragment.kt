package com.example.httpexample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.httpexample.adapter.BooksAdapter
import com.example.httpexample.adapter.BooksTouchHelperCallback
import com.example.httpexample.MainActivity
import com.example.httpexample.databinding.FragmentRecyclerBinding
import com.example.httpexample.model.Book
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONException
import java.io.IOException
import java.lang.Exception

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
        try {
            val request = Request.Builder()
                .url(ENDPOINT + BOOKS_URI)
                .build()
            val response = mainActivity.client.newCall(request).execute()
            val books = mutableListOf<Book>()
            val json = JSONArray(response.body?.string() ?: "")
            for (i in 0 until json.length()) {
                val jsonBook = json.getJSONObject(i)
                val title = jsonBook.getString(TITLE)
                val id = jsonBook.getInt(ID)
                books.add(Book(title, id))
            }
            Handler(Looper.getMainLooper()).post {
                booksAdapter.updateBooks(books)
            }
        } catch (exc: IOException) {
            Log.e(TAG, "getBooksAndShowItIO", exc)
        } catch (exc: IllegalStateException) {
            Log.e(TAG, "getBooksAndShowItIllegal", exc)
        } catch (exc: JSONException) {
            Log.e(TAG, "getBooksAndShowItJSON", exc)
        } catch (exc: Exception) {
            Log.e(TAG, "getBooksAndShowIt", exc)
        }
    }

    /**
     * Function for delete book.
     * @param id is a book id.
     */
    @WorkerThread
    private fun removeBook(id: Int) {
        try {
            val body = "".toRequestBody(mainActivity.mediaType)
            val request = Request.Builder()
                .url(ENDPOINT + BOOKS_URI + "/${id}")
                .delete(body)
                .build()
            mainActivity.client.newCall(request).execute()
        } catch (exc: IOException) {
            Log.e(TAG, "removeBookIO", exc)
        } catch (exc: IllegalArgumentException) {
            Log.e(TAG, "removeBookIllegal", exc)
        } catch (exc: Exception) {
            Log.e(TAG, "removeBook", exc)
        }
    }

    companion object {
        const val ENDPOINT =
            "http://10.0.2.2:3000"  // Im using json-server running on my localhost and emulator
        const val BOOKS_URI = "/books"
        const val TITLE = "title"
        const val CHANGE_ID = "changeID"
        const val CHANGE_TITLE = "changeTitle"
        private const val ID = "id"
        private const val TAG = "RecyclerFragment"
    }
}