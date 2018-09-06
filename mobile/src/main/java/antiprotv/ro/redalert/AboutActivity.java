/**
 * Copyright Cristian "ciuc" Starasciuc 2016
 * cristi.ciuc@gmail.com
 */
package antiprotv.ro.redalert;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.widget.TextView;

/**
 * Created by ciuc on 7/19/16.
 */
public class AboutActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        TextView foo = (TextView) findViewById(R.id.aboutText);
        foo.setText(Html.fromHtml(getString(R.string.about_text)));
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
}
