package com.example.httpexample.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.annotation.WorkerThread
import com.example.httpexample.MainActivity
import com.example.httpexample.R
import com.example.httpexample.ui.RecyclerFragment.Companion.BOOKS_URI
import com.example.httpexample.ui.RecyclerFragment.Companion.ENDPOINT
import com.example.httpexample.ui.RecyclerFragment.Companion.TITLE
import com.example.httpexample.databinding.FragmentAddBinding
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_ID
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_TITLE
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.lang.Exception
import java.net.HttpURLConnection
import java.net.URL

private const val TAG = "AddFragment"

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
                Thread {
                    changeBook(arguments!!.getInt(CHANGE_ID), title)
                }.start()
                mainActivity.onBackPressed()
            }
        } else {
            binding.addButton.setOnClickListener {
                val title = binding.title.text.toString()
                Thread {
                    addBook(title)
                }.start()
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

    /**
     * Function for create new book.
     * @param bookTitle is a title for new book.
     */
    @WorkerThread
    private fun addBook(bookTitle: String) {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL(ENDPOINT + BOOKS_URI).openConnection() as HttpURLConnection
            val body = JSONObject().apply {
                put(TITLE, bookTitle)
            }
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "POST"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            OutputStreamWriter(httpUrlConnection.outputStream).use {
                it.write(body.toString())
            }
            httpUrlConnection.responseCode
        } catch (exc: Exception) {
            Log.e(TAG, "addBook", exc)
        } finally {
            httpUrlConnection?.disconnect()
        }
    }

    /**
     * Function for update book.
     * @param bookTitle is a new book title.
     * @param id is a book id.
     */
    @WorkerThread
    private fun changeBook(id: Int, bookTitle: String) {
        var httpUrlConnection: HttpURLConnection? = null
        try {
            httpUrlConnection = URL("$ENDPOINT$BOOKS_URI/$id").openConnection() as HttpURLConnection
            val body = JSONObject().apply {
                put(TITLE, bookTitle)
            }
            httpUrlConnection.apply {
                connectTimeout = 10000 // 10 seconds
                requestMethod = "PUT"
                doOutput = true
                setRequestProperty("Content-Type", "application/json")
            }
            OutputStreamWriter(httpUrlConnection.outputStream).use {
                it.write(body.toString())
            }
            httpUrlConnection.responseCode
        } catch (exc: Exception) {
            Log.e(TAG, "removeBook", exc)
        } finally {
            httpUrlConnection?.disconnect()
        }
    }
}