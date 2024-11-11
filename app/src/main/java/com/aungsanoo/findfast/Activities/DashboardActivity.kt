package com.aungsanoo.findfast.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Fragments.*
import com.aungsanoo.findfast.R
import com.google.android.material.bottomnavigation.BottomNavigationView

class DashboardActivity : AppCompatActivity() {

    private lateinit var homeFragment: HomeFragment
    private lateinit var searchFragment: SearchFragment
    private lateinit var transactionsFragment: TransactionsFragment
    private lateinit var profileFragment: ProfileFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dashboard)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, 0)
            insets
        }

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigationContainer)

        homeFragment = HomeFragment()
        searchFragment = SearchFragment()
        transactionsFragment = TransactionsFragment()
        profileFragment = ProfileFragment()

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.fragmentContainer, homeFragment, "HOME")
                .commit()
        }

        bottomNavigationView.setOnItemSelectedListener { menuItem ->
            val selectedFragment: Fragment? = when (menuItem.itemId) {
                R.id.homeFragment -> homeFragment
                R.id.searchFragment -> searchFragment
                R.id.cartFragment -> CartFragment()
                R.id.profileFragment -> profileFragment
                else -> null
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .apply {
                        supportFragmentManager.fragments.forEach { fragment ->
                            if (fragment != selectedFragment) hide(fragment)
                        }
                        if (selectedFragment.isAdded) show(selectedFragment) else add(R.id.fragmentContainer, selectedFragment)
                    }
                    .commit()
            }
            true
        }
    }
}
