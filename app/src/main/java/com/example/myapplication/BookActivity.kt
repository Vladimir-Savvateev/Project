package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button
import android.content.Intent
import android.view.View
import androidx.core.content.edit
import android.util.Log

class BookActivity : AppCompatActivity() {

    lateinit var title: String
    var id = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.book)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        title = intent.getStringExtra("TITLE")!!
        val prefs = getSharedPreferences("page_saves", Context.MODE_PRIVATE)
        id = prefs.getInt(title, 0)
        val path = intent.getStringArrayExtra("PATH")!!
        val bookTitle = findViewById<TextView>(R.id.book_title)
        val content = findViewById<TextView>(R.id.book_content)
        val inputStream = assets.open(path[id])
        val size = inputStream.available()
        val buffer = ByteArray(size)
        val next = findViewById<Button>(R.id.next)
        val prev = findViewById<Button>(R.id.prev)
        val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)

        if (path.size == 1) {
            prev.visibility = View.GONE
            next.visibility = View.GONE
        } else {
            if (id == 0) {
                bookTitle.visibility = View.VISIBLE
                prev.visibility = View.INVISIBLE
            } else {
                bookTitle.visibility = View.GONE
            }
            if (id == path.size - 1)
                next.visibility = View.INVISIBLE
        }

        next.setOnClickListener {
            intent = Intent(this, BookActivity::class.java)
            intent.putExtra("PATH", path)
            intent.putExtra("TITLE", title)
            prefs.edit().apply {
                putInt(title, id + 1)
            }.apply()
            Log.d("MY_TAG", scroll.scrollY.toString())
            scroll.scrollTo(0, 0)
            startActivity(intent)
        }

        prev.setOnClickListener {
            intent = Intent(this, BookActivity::class.java)
            intent.putExtra("PATH", path)
            intent.putExtra("TITLE", title)
            prefs.edit().apply {
                putInt(title, id - 1)
            }.apply()
            scroll.scrollTo(0, 0)
            startActivity(intent)
        }

        inputStream.read(buffer)
        inputStream.close()
        bookTitle.text = title
        content.text = String(buffer, Charsets.UTF_8)
        val back = findViewById<Button>(R.id.back)

        back.setOnClickListener {
            intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        scroll.post {
            scroll.scrollTo(0, prefs.getInt(title + "scroll", 0))
        }
    }

    override fun onPause() {
        super.onPause()
        val prefs = getSharedPreferences("page_saves", Context.MODE_PRIVATE)
        val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)
        prefs.edit().apply {
            putInt(title + "scroll", scroll.scrollY)
        }.apply()
        Log.d("MY_TAG", scroll.scrollY.toString())
    }
}