package com.example.myapplication

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView


class mainactivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val bottomBar=findViewById<BottomNavigationView>(R.id.bottombar)
        bottomBar.setOnItemSelectedListener {menuitem->
        if(menuitem.itemId==R.id.nav_guard){
            infiltrated(guardFragment.newInstance())
        }
            else if (menuitem.itemId==R.id.nav_home)
            infiltrated(homefragment.newInstance())
            true
        }

    }



    private fun infiltrated(newInstance: Fragment) {
        val transaction=supportFragmentManager.beginTransaction()
        transaction.replace(R.id.container,newInstance)
        transaction.commit()

    }
}