package xstar.com.kotlintest

import android.databinding.DataBindingUtil.bind
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import xstar.com.kotlintest.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val rootview = LayoutInflater.from(this).inflate(R.layout.activity_main, null, false)
        val binder: ActivityMainBinding = bind(rootview)
        setContentView(rootview)
        binder.searchServerEt.addTextChangedListener(textChangeListener)
    }

    val textChangeListener = object : TextWatcher {
        override fun afterTextChanged(s: Editable?) {
            analyzeText(s.toString())
        }

        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
        }
    }

    fun analyzeText(str: String) {
        val result = when (str) {
            "5" -> println("number")
            "程序" -> println("program")
            else -> println("随便")
        }
    }
}
