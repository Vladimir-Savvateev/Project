package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
import androidx.lifecycle.lifecycleScope
import android.widget.Toast
import kotlinx.coroutines.launch
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
        val downloader = GitDownloader()

        lifecycleScope.launch {
            val result = downloader.loadBooks()
            result.onSuccess { items ->
                val adapter = ImageListAdapter(this@MainActivity, items) { item ->
                    val intent = Intent(this@MainActivity, BookActivity::class.java)
                    intent.putExtra("SIZE", item.size)
                    intent.putExtra("TITLE", item.headLine)
                    intent.putExtra("PATH", item.url)
                    startActivity(intent)
                }
                listView.adapter = adapter
            }

            result.onFailure { error ->
                Toast.makeText(this@MainActivity, "Ошибка Загрузки", Toast.LENGTH_LONG)
                    .show()
            }
        }
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