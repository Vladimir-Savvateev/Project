package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.content.edit

class AccountInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_account_info)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
            val history = findViewById<AppCompatButton>(R.id.main_history)
            val temporary = intent.getSerializableExtra("BOOKS")  as? ArrayList<ImageItem>
            val allBooks: List<ImageItem> = temporary ?: emptyList()
            history.setOnClickListener {
            val prefs = getSharedPreferences("page_saves", MODE_PRIVATE)
                for (book in allBooks) {
                    Log.d("ACCOUNT", "Книга: ${book.url}")
                }
            val booksInHistory: List<ImageItem> = allBooks.filter { book->
                isInHistory(book,prefs)
            }
                Log.d("ACCOUNT", "booksInHistory.size = ${booksInHistory.size}")
            val intent = Intent(this@AccountInfo, HistoryActivity::class.java)
            intent.putExtra("BOOKS",ArrayList(booksInHistory))
            startActivity(intent)
        }
        val logout = findViewById<AppCompatButton>(R.id.logout)
        logout.setOnClickListener {
            val prefs = getSharedPreferences("page_saves", MODE_PRIVATE)
            prefs.edit { remove("USERNAME") }
            startActivity(Intent(this, RegistrationActivity::class.java))
            finishAffinity()
        }
    }
}