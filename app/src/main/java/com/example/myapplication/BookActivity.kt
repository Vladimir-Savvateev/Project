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
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class BookActivity : AppCompatActivity() {

    lateinit var title: String
    var id = 0
    var curScroll = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_book)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.book)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val downloader = GitDownloader()
        val size = intent.getIntExtra("SIZE",1)
        title = intent.getStringExtra("TITLE")!!
        val prefs = getSharedPreferences("page_saves", MODE_PRIVATE)
        val path = intent.getStringExtra("PATH")!!
        id = prefs.getInt(path, 0)
        val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)
        val bookTitle = findViewById<TextView>(R.id.book_title)
        val content = findViewById<TextView>(R.id.book_content)
        val changePages = findViewById<EditText>(R.id.change_page)
        changePages.setHint("Введите номер страницы от 1 до ${size.toString()}")
        changePages.textSize = 10.0F
        changePages.setOnEditorActionListener{ _, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE ||
                    actionId == EditorInfo.IME_ACTION_GO ||
                    actionId == EditorInfo.IME_ACTION_SEND ||
                    actionId == EditorInfo.IME_ACTION_SEARCH ||
                    event?.keyCode == KeyEvent.KEYCODE_ENTER
                ){
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager).hideSoftInputFromWindow(changePages.windowToken,0)
                if(changePages.text.toString().toInt()  < 1 || changePages.text.toString().toInt()  > size) {
                    Toast.makeText(this,"Вы ввели номер не существующей страницы",Toast.LENGTH_SHORT).show()
                }
                else {
                    content.text = "Идёт загрузка подождите"
                    id = changePages.text.toString().toInt() - 1
                    lifecycleScope.launch{
                        val result = downloader.loadTextFile("https://raw.githubusercontent.com/Vladimir-Savvateev/books/refs/heads/main/" + "text/" + path + "_" + (id + 1).toString() + ".txt")
                        result.onSuccess { res ->
                            content.text = res
                        }
                        result.onFailure { error ->
                            content.text = error.message
                        }
                    }
                    prefs.edit {
                        putInt(title, changePages.text.toString().toInt() - 1)
                    }
                    Log.d("MY_TAG", scroll.scrollY.toString())
                    scroll.scrollTo(0, 0)
                }
                changePages.clearFocus()
                changePages.text.clear()
                return@setOnEditorActionListener true
            }
            false
        }
        if (size == 1) {
            changePages.visibility = View.GONE
       }


        bookTitle.text = title
        val back = findViewById<Button>(R.id.back)
        if(intent.getBooleanExtra("HISTORY",false))
            back.text = "Назад к истории"
        content.text = "Идёт загрузка подождите"
        back.setOnClickListener {
            finish()
        }
    }


    override fun onResume(){
        super.onResume()
        lifecycleScope.launch{
            val path = intent.getStringExtra("PATH")!!
            val prefs = getSharedPreferences("page_saves", Context.MODE_PRIVATE)
            val downloader = GitDownloader()
            val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)
            curScroll = prefs.getInt("${path}scroll",0)
            val content = findViewById<TextView>(R.id.book_content)
            val result = downloader.loadTextFile("https://raw.githubusercontent.com/Vladimir-Savvateev/books/refs/heads/main/" + "text/" + path + "_" + (id + 1).toString() + ".txt")
            result.onSuccess { res ->
                content.text = res
                scroll.post {
                    scroll.scrollTo(0, curScroll)
                }
            }
            result.onFailure { error ->
                if(error.message.toString() == "403")
                    Toast.makeText(this@BookActivity,"Ошибка загрузки.Обновите приложение",Toast.LENGTH_SHORT).show()
                else
                    content.text = error.message
            }
        }
    }

    override fun onPause() {
        super.onPause()
        val downloader = GitDownloader()
        val path = intent.getStringExtra("PATH")!!
        val prefs = getSharedPreferences("page_saves", Context.MODE_PRIVATE)
        val scroll = findViewById<androidx.core.widget.NestedScrollView>(R.id.scroll_view)
        val username = prefs.getString("USERNAME","NONE") ?: "NONE"
        lifecycleScope.launch{
            downloader.uploadFile(username,"${path}Info.json", """
    {
        "curPage": $id,
        "curScroll": ${scroll.scrollY}
    }
""".trimIndent())
        }
        prefs.edit {
            putInt("${path}scroll", scroll.scrollY)
            putInt(path, id)
        }
        Log.d("MY_TAG", scroll.scrollY.toString())
    }
}