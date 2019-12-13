package com.specialprojects.experiments.envelopecall.ui

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.specialprojects.experiments.envelopecall.R
import com.specialprojects.experiments.envelopecall.ui.util.bindView

class HomeActivity: AppCompatActivity() {
    private val helpView by bindView<TextView>(R.id.help)
    private val useView by bindView<TextView>(R.id.use_envelope)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        helpView.setOnClickListener {
            startActivity(Intent(this, HelpActivity::class.java))
        }

        useView.setOnClickListener {
            startActivity(Intent(this, CountdownActivity::class.java))
        }
    }
}