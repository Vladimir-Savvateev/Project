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
            ImageItem(1,R.drawable.ic_blazhen_nezlobivyj_poet,"Блажен незлобливый поэт",arrayOf("blazhen_nezloblivi_poet.txt")),
            ImageItem(2,R.drawable.ic_shinel,"Шинель",arrayOf("shinel.txt")),
            ImageItem(3,R.drawable.ic_groza,"Гроза",arrayOf("groza_dejstvie_1.txt","groza_dejstvie_2.txt","groza_dejstvie_3.txt","groza_dejstvie_4.txt","groza_dejstvie_5.txt")),
            ImageItem(4,R.drawable.ic_gore_ot_uma,"Горе от ума",arrayOf("gore_ot_uma_1.txt","gore_ot_uma_2.txt","gore_ot_uma_3.txt","gore_ot_uma_4.txt")),
            ImageItem(5,R.drawable.ic_mertvye_dushi,"Мёртвые души",arrayOf("mertvye_dushi_1.txt","mertvye_dushi_2.txt","mertvye_dushi_3.txt","mertvye_dushi_4.txt","mertvye_dushi_5.txt","mertvye_dushi_6.txt","mertvye_dushi_7.txt","mertvye_dushi_8.txt","mertvye_dushi_9.txt","mertvye_dushi_10.txt","mertvye_dushi_11.txt","mertvye_dushi_12.txt"))
        )


        val adapter = ImageListAdapter(this, items) {item ->
            val intent = Intent(this,BookActivity::class.java)
            intent.putExtra("TITLE",item.headLine)
            intent.putExtra("PATH",item.pathTocontent)
            intent.putExtra("ID",0)
            startActivity(intent)
        }

        listView.adapter = adapter






    }
}