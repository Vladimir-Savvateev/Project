package com.example.myapplication

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.edit
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
        val prefs = getSharedPreferences("page_saves", MODE_PRIVATE)
        if(prefs.getString("USERNAME","NONE") != "NONE"){
            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
        val reg = findViewById<AppCompatButton>(R.id.authorizationButton)
        val downloader = GitDownloader()
        val username = findViewById<EditText>(R.id.username)
        val password = findViewById<EditText>(R.id.password)
        reg.setOnClickListener {
            if(reg.text == "Войти") {
                lifecycleScope.launch {
                    val result = downloader.authInfo(username.text.toString())
                    result.onSuccess { authData ->
                        if (password.text.toString() == authData.password) {
                            prefs.edit {
                                putString("USERNAME", username.text.toString())
                            }
                            val intent = Intent(this@RegistrationActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Вы ввели неправильный пароль.",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        }
                    }

                    result.onFailure { error ->
                        Toast.makeText(this@RegistrationActivity, error.message, Toast.LENGTH_LONG)
                            .show()
                    }
                }
            }
            else{
                lifecycleScope.launch {
                    if(username.text == null)
                        Toast.makeText(this@RegistrationActivity, "Введите ваше имя",Toast.LENGTH_LONG)
                            .show()
                    else {
                        if (password.text.toString().length < 7)
                            Toast.makeText(
                                this@RegistrationActivity,
                                "Пароль должен содержать не менее 7 символов",
                                Toast.LENGTH_LONG
                            )
                                .show()
                        else {
                            if (!downloader.fileExists(username.text.toString(), "userInfo.json") && username.text.toString() != "NONE") {
                                downloader.uploadFile(
                                    username.text.toString(), "userInfo.json", """
                                                                                        {
                                                                                            "username": "${username.text.toString()}",
                                                                                            "password": "${password.text.toString()}"
                                                                                        }
                                                                                        """.trimIndent()
                                )
                                val intent =
                                    Intent(this@RegistrationActivity, MainActivity::class.java)
                                prefs.edit {
                                    putString("USERNAME", username.text.toString())
                                }
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    this@RegistrationActivity,
                                    "Это имя уже занято",
                                    Toast.LENGTH_LONG
                                )
                                    .show()
                            }
                        }
                    }

                }
            }
        }

        val newReg = findViewById<AppCompatButton>(R.id.registrationButton)

        newReg.setOnClickListener {
            if(newReg.text == "Создать аккаунт") {
                newReg.text = "Войти"
                username.text.clear()
                password.text.clear()
                reg.text = "Создать аккаунт"
            }
            else{
                newReg.text = "Создать аккаунт"
                username.text.clear()
                password.text.clear()
                reg.text = "Войти"
            }
        }

    }
}