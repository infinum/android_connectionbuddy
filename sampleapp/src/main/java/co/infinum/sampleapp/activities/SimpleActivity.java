package co.infinum.sampleapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import co.infinum.connectionutils.ConnectifyPreferences;
import co.infinum.connectionutils.ConnectifyUtils;
import co.infinum.connectionutils.interfaces.ConnectivityChangeListener;
import co.infinum.connectionutils.receivers.NetworkChangeReceiver;
import co.infinum.sampleapp.R;

/**
 * Created by Željko Plesac on 08/09/15.
 */
public class SimpleActivity extends Activity implements ConnectivityChangeListener{

    private TextView tvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);

        if(savedInstanceState != null){
            ConnectifyPreferences.clearInternetConnection(this, this);
        }

        tvTitle = (TextView) findViewById(R.id.tv_title);
    }

    @Override
    protected void onStart() {
        super.onStart();

        ConnectifyUtils.registerForConnectivityEvents(this, this, this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        ConnectifyUtils.unregisterFromConnectivityEvents(this, this);
    }

    @Override
    public void onConnectionChange(NetworkChangeReceiver.ConnectivityEvent event) {
        if(event == NetworkChangeReceiver.ConnectivityEvent.CONNECTED){
            tvTitle.setText("Connection active");
        }
        else{
            tvTitle.setText("Connection inactive");
        }
    }
}
