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
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton


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
        val back = findViewById<AppCompatButton>(R.id.history_back)
        back.setOnClickListener {
            finish()
        }
        val allBooks = intent.getSerializableExtra("BOOKS") as? ArrayList<ImageItem>
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
            intent.putExtra("HISTORY",true)
            startActivity(intent)
        }
        val search = findViewById<EditText>(R.id.history_search)

        search.setOnEditorActionListener{ _, actionId, event ->
            val temporary = search.text.toString()
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                actionId == EditorInfo.IME_ACTION_GO ||
                actionId == EditorInfo.IME_ACTION_SEND ||
                actionId == EditorInfo.IME_ACTION_SEARCH ||
                event?.keyCode == KeyEvent.KEYCODE_ENTER
            ){
                (getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(search.windowToken,0)
                var filteredBooks: List<ImageItem>
                if(temporary.isEmpty())
                    filteredBooks = allBooks
                else {
                    filteredBooks = allBooks.filter { book ->
                        book.headLine.contains(temporary, ignoreCase = true) || book.author.contains(temporary, ignoreCase = true) || genreChek(book,temporary)
                    }
                }
                search.clearFocus()
                adapter.update(filteredBooks)
                return@setOnEditorActionListener true
            }
            false
        }
        listView.adapter = adapter
    }
}