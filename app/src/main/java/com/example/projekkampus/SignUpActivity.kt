package com.example.projekkampus

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import kotlin.text.isBlank
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthException
import android.widget.Toast

class SignUpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val nameEditText = findViewById<EditText>(R.id.nameEditTextSignUp)
        val emailEditText = findViewById<EditText>(R.id.emailEditTextSignUp)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditTextSignUp)
        val signUpButton = findViewById<Button>(R.id.signUpButtonSignUp)
        val loginButton = findViewById<Button>(R.id.loginButton)

        signUpButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (name.isBlank() || email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                // Initialize FirebaseAuth
                val auth = FirebaseAuth.getInstance()

                // Create a new user with email and password
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            Toast.makeText(this, "Account created successfully", Toast.LENGTH_SHORT).show()
                            finish()  // Return to the previous activity
                        } else {
                            // Handle errors (e.g., email already in use, weak password, etc.)
                            val exception = (task.exception as? FirebaseAuthException)?.message
                            Toast.makeText(this, "Error: $exception", Toast.LENGTH_SHORT).show()
                        }
                    }
            }
        }

        loginButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }
    }
}