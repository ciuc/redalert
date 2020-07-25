package ro.antiprotv.sugar

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_main.*
import ro.antiprotv.sugar.ui.MainPagerAdapter

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        view_pager.adapter = MainPagerAdapter(this, supportFragmentManager)
        main_layout_tabs.setupWithViewPager(view_pager)
    }
}