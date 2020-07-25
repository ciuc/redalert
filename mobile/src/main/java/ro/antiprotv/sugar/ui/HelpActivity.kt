/**
 * Copyright Cristian "ciuc" Starasciuc 2016
 * cristi.ciuc@gmail.com
 */
package ro.antiprotv.sugar.ui

import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Html
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_help.*
import ro.antiprotv.sugar.R

/**
 * Created by ciuc on 7/19/16.
 */
class HelpActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        helpText.text = Html.fromHtml(getString(R.string.help_text), Html.FROM_HTML_MODE_COMPACT)
        supportActionBar!!.setBackgroundDrawable(ColorDrawable(resources.getColor(R.color.red, theme)))

        help_back.setOnClickListener {
            val list = Intent()
            list.setClassName(this@HelpActivity, "ro.antiprotv.sugar.AlertListActivity")
            startActivity(list)
        }
    }

}