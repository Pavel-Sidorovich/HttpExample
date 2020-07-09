package com.example.httpexample.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.MainThread
import androidx.annotation.WorkerThread
import androidx.fragment.app.Fragment
import com.example.httpexample.MainActivity
import com.example.httpexample.R
import com.example.httpexample.databinding.FragmentAddBinding
import com.example.httpexample.ui.RecyclerFragment.Companion.BOOKS_URI
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_ID
import com.example.httpexample.ui.RecyclerFragment.Companion.CHANGE_TITLE
import com.example.httpexample.ui.RecyclerFragment.Companion.ENDPOINT
import com.example.httpexample.ui.RecyclerFragment.Companion.TITLE
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException

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
                changeBook(arguments!!.getInt(CHANGE_ID), title)
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
        try {
            val json = JSONObject().apply {
                put(TITLE, bookTitle)
            }
            val body = json.toString().toRequestBody(mainActivity.mediaType)
            val request = Request.Builder()
                .url(ENDPOINT + BOOKS_URI)
                .post(body)
                .build()
            mainActivity.client.newCall(request).execute()
        } catch (exc: IOException) {
            Log.e(TAG, "addBookIO", exc)
        } catch (exc: IllegalStateException) {
            Log.e(TAG, "addBookIllegal", exc)
        } catch (exc: JSONException) {
            Log.e(TAG, "addBookJSON", exc)
        } catch (exc: Exception) {
            Log.e(TAG, "addBook", exc)
        }
    }

    /**
     * Function for update book. Work on WorkerThread
     * @param bookTitle is a new book title.
     * @param id is a book id.
     */
    @MainThread
    private fun changeBook(id: Int, bookTitle: String) {
        try {
            val json = JSONObject().apply {
                put(TITLE, bookTitle)
            }
            val body = json.toString().toRequestBody(mainActivity.mediaType)
            val request = Request.Builder()
                .url("$ENDPOINT$BOOKS_URI/$id")
                .put(body)
                .build()
            mainActivity.client.newCall(request).enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    Log.e(TAG, "removeBookCall", e)
                }

                override fun onResponse(call: Call, response: Response) {
                    val handler = Handler(Looper.getMainLooper())
                    handler.post {
                        Toast.makeText(mainActivity, response.message, Toast.LENGTH_SHORT).show()
                    }
                }

            })
        } catch (exc: IllegalStateException) {
            Log.e(TAG, "removeBookIllegal", exc)
        } catch (exc: JSONException) {
            Log.e(TAG, "removeBookJSON", exc)
        } catch (exc: Exception) {
            Log.e(TAG, "removeBook", exc)
        }
    }
}