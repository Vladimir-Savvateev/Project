package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class RegistrationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registration)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val reg = findViewById<AppCompatButton>(R.id.authorizationButton)
        val downloader = GitDownloader()
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        reg.setOnClickListener {
            lifecycleScope.launch {
                val result = downloader.authInfo(username.text.toString())
                result.onSuccess { authData ->
                        if(password.text.toString() == authData.password){
                            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            startActivity(intent)
                        }
                        else{
                            Toast.makeText(this@RegistrationActivity, "Вы ввели неправильный пароль.", Toast.LENGTH_LONG)
                                .show()
                        }
                    }

                result.onFailure { error ->
                    Toast.makeText(this@RegistrationActivity, error.message, Toast.LENGTH_LONG)
                        .show()
                }
            }


        }
    }
}