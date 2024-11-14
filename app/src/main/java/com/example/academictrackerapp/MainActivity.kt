package com.example.academictrackerapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.academictrackerapp.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
    companion object {
        lateinit var auth: FirebaseAuth
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val navView: BottomNavigationView = binding.navView

        if (auth.currentUser != null) {
            // Navigate to the main fragment or home page if user is logged in
            navController.navigate(R.id.navigation_home)
        } else {
            // Otherwise, navigate to the login screen
            navController.navigate(R.id.navigation_welcome)
        }

        // Set multiple destinations as top-level to exclude the back arrow on these pages
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_support,
                R.id.navigation_notifications,
                R.id.navigation_profile
            )
        )

        // Set up ActionBar with NavController and AppBarConfiguration
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Listener to control app bar and bottom nav visibility based on the destination
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.navigation_welcome -> {
                    // Hide the app bar and bottom navigation on the Welcome page
                    supportActionBar?.hide()
                    navView.visibility = View.GONE
                }
                R.id.navigation_home -> {
                    // Show the app bar without the back arrow and with the three-dots menu on Home
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
                else -> {
                    // Show the app bar with the back arrow on other screens, excluding the three-dots menu
                    supportActionBar?.show()
                    navView.visibility = View.VISIBLE
                }
            }

            when (destination.id) {
                R.id.navigation_home,
                R.id.navigation_dashboard,
                R.id.navigation_support,
                R.id.navigation_notifications,
                R.id.navigation_profile -> {
                    // Show the bottom navigation on these pages
                    navView.visibility = View.VISIBLE
                }

                else -> {
                    // Hide the bottom navigation on other pages
                    navView.visibility = View.GONE
                }
            }
            // Refresh the options menu based on the destination
            invalidateOptionsMenu()
        }
    }

    // Inflate the menu only on the Home page (with the three-dots menu)
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        return if (navController.currentDestination?.id == R.id.navigation_home) {
            menuInflater.inflate(R.menu.top_menu, menu)
            true
        } else {
            false
        }
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navigation_link_to_pages -> {
                // Navigate to the Progress Tracking screen
                navController.navigate(R.id.navigation_link_to_pages)
                return true
            }
            R.id.navigation_link_to_pages2 -> {
                // Navigate to the Material Marketplace screen
                navController.navigate(R.id.navigation_link_to_pages2)
                return true
            }
            R.id.navigation_logOut -> {
                // Log the user out of Firebase
                FirebaseAuth.getInstance().signOut()

                // Navigate to the welcome screen
                navController.navigate(R.id.navigation_welcome)
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }

    // Enable back navigation for non-top-level destinations
    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
