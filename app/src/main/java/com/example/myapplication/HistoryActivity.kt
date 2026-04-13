package com.example.myapplication

import android.content.Intent
import android.os.Bundle
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.util.Log

class HistoryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_history)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.history)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val listView = findViewById<ListView>(R.id.history_listView)
        @Suppress("UNCHECKED_CAST")
        val allBooks = intent.getSerializableExtra("BOOKS", ArrayList::class.java) as? ArrayList<ImageItem>
        if (allBooks == null || allBooks.isEmpty()) {
            Toast.makeText(this, "История пуста", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        val adapter = ImageListAdapter(this, allBooks.toList()) { item ->
            val intent = Intent(this, BookActivity::class.java)
            intent.putExtra("SIZE", item.size)
            intent.putExtra("TITLE", item.headLine)
            intent.putExtra("PATH", item.url)
            startActivity(intent)
        }
        listView.adapter = adapter
    }
}