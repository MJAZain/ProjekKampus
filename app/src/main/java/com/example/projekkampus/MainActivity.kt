package com.example.projekkampus

// MainActivity.kt
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.widget.ImageView


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val nameEditText = findViewById<EditText>(R.id.nameEditText)
        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val submitButton = findViewById<Button>(R.id.submitButton)
        val imageView = findViewById<ImageView>(R.id.image)

        submitButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()

            val message = "Name: $name, Email: $email"
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }
    }
}
