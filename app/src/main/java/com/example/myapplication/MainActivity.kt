package com.example.myapplication

import android.os.Bundle
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent
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

        val items = listOf(
            ImageItem(1,"https://raw.githubusercontent.com/Vladimir-Savvateev/books/main/ic/ic_blazhen_nezlobivyj_poet.webp","Блажен незлобливый поэт",1,"blazhen_nezloblivi_poet"),
            ImageItem(2,"url","Шинель",1,"shinel"),
            ImageItem(3,"url","Гроза",5,"groza_dejstvie"),
            ImageItem(4,"url","Горе от ума",4,"gore_ot_uma"),
            ImageItem(5,"url","Мёртвые души",12,"mertvye_dushi")
        )


        val adapter = ImageListAdapter(this, items) {item ->
            val intent = Intent(this,BookActivity::class.java)
            intent.putExtra("SIZE",item.size)
            intent.putExtra("TITLE",item.headLine)
            intent.putExtra("PATH",item.path)
            intent.putExtra("ID",0)
            startActivity(intent)
        }

        listView.adapter = adapter






    }
}