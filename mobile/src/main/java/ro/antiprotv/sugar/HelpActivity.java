/**
 * Copyright Cristian "ciuc" Starasciuc 2016
 * cristi.ciuc@gmail.com
 */
package ro.antiprotv.sugar;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Created by ciuc on 7/19/16.
 */
public class HelpActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help);
        TextView foo = (TextView) findViewById(R.id.helpText);
        foo.setText(Html.fromHtml(getString(R.string.help_text)));
        getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red)));
        Button back = findViewById(R.id.help_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent list = new Intent();
                list.setClassName(HelpActivity.this, "ro.antiprotv.sugar.AlertListActivity");
                startActivity(list);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
