package com.example.academictrackerapp.Imraan

// BaseActivity.kt
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Enable the back arrow in the action bar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
    }

    // Handle the back arrow click to navigate back
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressed() // Navigates back
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}
