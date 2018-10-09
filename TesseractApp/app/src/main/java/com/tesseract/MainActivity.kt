package com.tesseract

import android.app.Activity
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentTransaction
import android.util.Log
import android.widget.Toast

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val mMainNav: BottomNavigationView = findViewById(R.id.home_nav_bar)

        setFragment(HomeFragment() as Fragment)

        mMainNav.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    setFragment(HomeFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_light -> {
                    setFragment(LightFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_spotify -> {
                    setFragment(SpotifyFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_about -> {
                    setFragment(AboutFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                R.id.nav_connections-> {
                    setFragment(ConnectionsFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
                else -> {
                    setFragment(HomeFragment() as Fragment)
                    return@setOnNavigationItemSelectedListener true
                }
            }
        }

    }

    private fun setFragment(fragment: Fragment) {
        val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_view_frame, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }


}
