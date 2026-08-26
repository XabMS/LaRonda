package com.lostandfondue.ronda

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View

class Reglas : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reglas)
        findViewById<View>(android.R.id.content).applySystemBarInsetsAsPadding()
    }

    //override fun onCreateOptionsMenu(menu: Menu): Boolean {
     //   menuInflater.inflate(R.menu.topbar, menu)
     //   return true
   // }
}
