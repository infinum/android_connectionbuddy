package co.infinum.sampleapp.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import co.infinum.sampleapp.R;

/**
 * Created by Željko Plesac on 08/09/15.
 */
public class MainActivity extends Activity{

    private Button buttonSimpleExample;

    private Button buttonMVPExample;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMVPExample = (Button) findViewById(R.id.button_mvp);
        buttonSimpleExample = (Button) findViewById(R.id.button_simple);

        buttonMVPExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MVPActivity.class);
                startActivity(intent);
            }
        });

        buttonSimpleExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SimpleActivity.class);
                startActivity(intent);
            }
        });
    }
}
