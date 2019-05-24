package xstar.com.kotlintest

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_by_sun_and_clock.*

class BySunAndClockActivity : AppCompatActivity(), View.OnClickListener {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_by_sun_and_clock)
        back_btn.setOnClickListener(this)
        cdv.scale_len = 20
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.back_btn -> {
                startActivity(Intent(this, GuideActivity::class.java))
                finish()
            }
        }
    }
}
