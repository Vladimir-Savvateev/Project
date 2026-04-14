package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import android.content.SharedPreferences
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import android.widget.EditText
import androidx.appcompat.widget.AppCompatButton
import kotlinx.coroutines.launch



fun isInHistory(book: ImageItem,prefs: SharedPreferences): Boolean{
    return prefs.getInt(book.headLine,-1) != -1
}
fun genreChek(book: ImageItem, arg: String): Boolean {
    for(genre in book.genres){
        if(genre.contains(arg, ignoreCase = true))
            return true
    }
    return false
}
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val listView = findViewById<ListView>(R.id.main_listView)

//        val items = listOf(
//            ImageItem(1,"blazhen_nezlobivyi_poet","Блажен незлобливый поэт",1),
//            ImageItem(2,"shinel","Шинель",1),
//            ImageItem(3,"groza_dejstvie","Гроза",5),
//            ImageItem(4,"gore_ot_uma","Горе от ума",4),
//            ImageItem(5,"mertvye_dushi","Мёртвые души",12)
//        )
        lateinit var adapter: ImageListAdapter
        val downloader = GitDownloader()
        var allBooks: List<ImageItem> = emptyList()
        val search = findViewById<EditText>(R.id.main_search)
        lifecycleScope.launch {
            val result = downloader.loadBooks()
            result.onSuccess { items ->
                allBooks = items
                adapter = ImageListAdapter(this@MainActivity, items) { item ->
                    val intent = Intent(this@MainActivity, BookActivity::class.java)
                    intent.putExtra("SIZE", item.size)
                    intent.putExtra("TITLE", item.headLine)
                    intent.putExtra("PATH", item.url)
                    startActivity(intent)
                }
                listView.adapter = adapter
            }

            result.onFailure { _ ->
                Toast.makeText(this@MainActivity, "Ошибка Загрузки.Перезапустите приложение", Toast.LENGTH_LONG)
                    .show()
            }
        }
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
        val history = findViewById<AppCompatButton>(R.id.main_history)
        history.setOnClickListener {
            val prefs = getSharedPreferences("page_saves", MODE_PRIVATE)
            val booksInHistory: List<ImageItem> = allBooks.filter { book->
                isInHistory(book,prefs)
            }
            val intent = Intent(this@MainActivity, HistoryActivity::class.java)
            intent.putExtra(    "BOOKS",ArrayList(booksInHistory))
            startActivity(intent)
        }


//        lifecycleScope.launch {
//            val success = downloader.uploadFile(
//                username = "user_123",
//                fileName = "test.json",
//                contentToUpload = """{"username": 42, "password": 1337}"""
//            )
//            if (success) {
//                Toast.makeText(this@MainActivity, "Данные сохранены!", Toast.LENGTH_SHORT).show()
//            } else {
//                Toast.makeText(this@MainActivity, "Ошибка сохранения", Toast.LENGTH_SHORT).show()
//            }
//        }


//        val adapter = ImageListAdapter(this@MainActivity, items) {item ->
//            val intent = Intent(this@MainActivity,BookActivity::class.java)
//            intent.putExtra("SIZE",item.size)
//            intent.putExtra("TITLE",item.headLine)
//            intent.putExtra("PATH",item.url)
//            intent.putExtra("ID",0)
//            startActivity(intent)
//        }

        //listView.adapter = adapter
    }
}