package com.example.backstreet_cycles

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.navigation.NavigationView

class DrawerActivity : AppCompatActivity() {

    // Initialise the DrawerLayout, NavigationView and ToggleBar
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var actionBarToggle: ActionBarDrawerToggle
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_journey)


        // Call findViewById on the DrawerLayout
        drawerLayout = findViewById(R.id.drawer_layout)

        // Pass the ActionBarToggle action into the drawerListener
        actionBarToggle = ActionBarDrawerToggle(this, drawerLayout, 0, 0)
        drawerLayout.addDrawerListener(actionBarToggle)

        // Call syncState() on the action bar so it'll automatically change to the back button when the drawer layout is open
        actionBarToggle.syncState()

//        // Display the icon to launch the drawer
//        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Call findViewById on the NavigationView
        navView = findViewById(R.id.navView)

//        // Call setNavigationItemSelectedListener on the NavigationView to detect when items are clicked
//        navView.setNavigationItemSelectedListener { menuItem ->
//            when (menuItem.itemId) {
//                R.id.Instructions -> {
//                    Toast.makeText(this, "Instructions", Toast.LENGTH_SHORT).show()
//                    true
//                }
//                else -> {
//                    false
//                }
//            }
//        }
    }

//    // override the onSupportNavigateUp() function to launch the Drawer when the hamburger icon is clicked
//    override fun onSupportNavigateUp(): Boolean {
//        drawerLayout.openDrawer(navView)
//        return true
//    }
//
//    // override the onBackPressed() function to close the Drawer when the back button is clicked
//    override fun onBackPressed() {
//        if (this.drawerLayout.isDrawerOpen(GravityCompat.START)) {
//            this.drawerLayout.closeDrawer(GravityCompat.START)
//        } else {
//            super.onBackPressed()
//        }
//    }
}