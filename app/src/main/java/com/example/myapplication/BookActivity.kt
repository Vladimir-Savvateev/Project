package com.example.myapplication

import android.content.Context
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.TextView
import android.widget.Button
import android.widget.Toast
import android.content.Intent
import android.widget.EditText
import android.view.View
import androidx.core.content.edit
import android.util.Log
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager

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
        val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)
        if(id < 0 || id > path.size - 1) {
            scroll.scrollTo(0,0)
            id = 0
        }
        val bookTitle = findViewById<TextView>(R.id.book_title)
        val content = findViewById<TextView>(R.id.book_content)
        val inputStream = assets.open(path[id])
        val changePages = findViewById<EditText>(R.id.change_page)
        val size = inputStream.available()
        val buffer = ByteArray(size)
        changePages.setHint("Введите номер страницы от 1 до ${path.size}")
        changePages.textSize = 10.0F
        changePages.setOnEditorActionListener{ _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_SEARCH ||
                    event?.keyCode == KeyEvent.KEYCODE_ENTER
                ){
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(changePages.windowToken,0)
                if(changePages.text.toString().toInt()  < 1 || changePages.text.toString().toInt()  > path.size) {
                    Toast.makeText(this,"Вы ввели номер не существующей страницы",Toast.LENGTH_SHORT).show()
                }
                else {
                    intent = Intent(this, BookActivity::class.java)
                    intent.putExtra("PATH", path)
                    intent.putExtra("TITLE", title)
                    prefs.edit {
                        putInt(title, changePages.text.toString().toInt() - 1)
                    }
                    Log.d("MY_TAG", scroll.scrollY.toString())
                    scroll.scrollTo(0, 0)
                    startActivity(intent)
                }
                return@setOnEditorActionListener true
            }
            false
        }
        if (path.size == 1) {
            changePages.visibility = View.GONE
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
        prefs.edit {
            putInt(title + "scroll", scroll.scrollY)
        }
        if( scroll.getChildAt(0).height - scroll.height == scroll.scrollY) {
            prefs.edit { remove(title + "scroll") }
            prefs.edit { remove(title) }
        }
        Log.d("MY_TAG", scroll.scrollY.toString())
    }
}