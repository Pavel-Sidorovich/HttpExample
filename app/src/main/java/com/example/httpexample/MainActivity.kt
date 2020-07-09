package com.example.httpexample

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.httpexample.ui.RecyclerFragment
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient

class MainActivity : AppCompatActivity() {

    val client = OkHttpClient()

    val mediaType: MediaType = "application/json; charset=utf-8".toMediaType()

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