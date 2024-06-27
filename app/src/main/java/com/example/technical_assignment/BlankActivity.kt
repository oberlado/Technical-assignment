package com.example.technical_assignment

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class BlankActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_empty)

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }
    }
}