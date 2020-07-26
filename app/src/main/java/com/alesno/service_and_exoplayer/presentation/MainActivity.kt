package com.alesno.service_and_exoplayer.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.alesno.service_and_exoplayer.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportFragmentManager.beginTransaction()
            .add(
                R.id.container,
                PlayerFragment.newInstance(),
                TAG_FRAGMENT
            )
            .commit()
    }

    companion object {

        private const val TAG_FRAGMENT = "fragment"

    }
}