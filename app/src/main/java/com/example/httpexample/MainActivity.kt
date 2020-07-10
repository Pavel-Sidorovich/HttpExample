package com.example.httpexample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.httpexample.adapter.BooksAdapter
import com.example.httpexample.api.BooksApiImpl
import com.example.httpexample.model.Book
import com.example.httpexample.ui.AddFragment
import com.example.httpexample.ui.RecyclerFragment
import kotlinx.coroutines.runBlocking

class MainActivity : AppCompatActivity() {

    private val changeListener: (Book) -> Unit = {
        val fragment = AddFragment()
        val bundle = Bundle()

        bundle.apply {
            putInt(RecyclerFragment.CHANGE_ID, it.id)
            putString(RecyclerFragment.CHANGE_TITLE, it.title)
        }
        fragment.arguments = bundle
        replaceFragment(fragment)
    }

    val booksAdapter =
        BooksAdapter(changeListener) {
            runBlocking {
                var list: List<Book>
                BooksApiImpl.apply {
                    removeBook(it.id)
                    list = getBooksAndShowIt()
                }
                return@runBlocking list
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Checked that it create new activity not recreate
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.container,
                    RecyclerFragment()
                )
                .addToBackStack(null)
                .commit()
        }
    }

    fun replaceFragment(fragment: Fragment) {
        // Checked that the new fragment is different from the one at the top
        if (supportFragmentManager.findFragmentById(R.id.container)?.javaClass != fragment.javaClass) {
            // Replace fragment
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == android.R.id.home) {
            onBackPressed()
            true
        } else {
            super.onOptionsItemSelected(item)
        }
    }
}