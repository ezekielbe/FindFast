package com.aungsanoo.findfast.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Fragments.AdminAddFragment
import com.aungsanoo.findfast.Fragments.AdminHomeFragment
import com.aungsanoo.findfast.Fragments.AdminSearchFragment
import com.aungsanoo.findfast.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class AdminDashboard : AppCompatActivity() {
    private lateinit var homeFragment: AdminHomeFragment
    private lateinit var searchFragment: AdminSearchFragment
    private lateinit var addFragment: AdminAddFragment
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_admin_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val bottomNavigationView =
            findViewById<BottomNavigationView>(R.id.adminBottomNavigationContainer)

        val fab: FloatingActionButton = findViewById(R.id.fab)
        homeFragment = AdminHomeFragment()
        searchFragment = AdminSearchFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment, "HOME")
                .commit()
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment = when (menuItem.itemId) {
                R.id.homeFragment -> homeFragment
                R.id.searchFragment -> searchFragment
                else -> homeFragment
            }

            supportFragmentManager.beginTransaction()
                .apply {
                    supportFragmentManager.fragments.forEach { fragment ->
                        if (fragment != selectedFragment) hide(fragment)
                    }
                    if (selectedFragment.isAdded) show(selectedFragment) else add(
                        R.id.fragmentContainer,
                        selectedFragment
                    )
                }
                .commit()

            true
        }
        fab.setOnClickListener {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, AdminAddFragment())
                .addToBackStack(null)
                .commit()
        }
    }
}