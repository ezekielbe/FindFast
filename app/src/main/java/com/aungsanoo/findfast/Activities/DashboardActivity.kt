package com.aungsanoo.findfast.Activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Fragments.HomeFragment
import com.aungsanoo.findfast.Fragments.ProfileFragment
import com.aungsanoo.findfast.Fragments.SearchFragment
import com.aungsanoo.findfast.Fragments.TransactionsFragment
import com.aungsanoo.findfast.R
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * Entry activity where it controls to display different menu
 * For Admins =>    Dashboard  | Search    | Financial Report   | Add item | Profile
 * For Users =>     Home       | Search    | Transactions       | Profile
 */
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
            var selectedFragment: Fragment? = null
            var tag: String? = ""

            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    selectedFragment = homeFragment
                    tag = "HOME"
                }
                R.id.searchFragment -> {
                    selectedFragment = searchFragment
                    tag = "SEARCH"
                }
                R.id.transactionFragment -> {
                    selectedFragment = transactionsFragment
                    tag = "TRANSACTIONS"
                }
                R.id.profileFragment -> {
                    selectedFragment = profileFragment
                    tag = "PROFILE"
                }
                else -> return@setOnItemSelectedListener false
            }

            supportFragmentManager.beginTransaction()
                .apply {
                    supportFragmentManager.fragments.forEach { fragment ->
                        if (fragment != selectedFragment) {
                            hide(fragment)
                        }
                    }
                    if (selectedFragment?.isAdded == true) {
                        show(selectedFragment)
                    } else {
                        add(R.id.fragmentContainer, selectedFragment!!, tag)
                    }
                }
                .commit()

            true
        }
    }
}
