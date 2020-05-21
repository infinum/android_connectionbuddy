package com.zplesac.connectionbuddy.sampleapp.activities;

import com.zplesac.connectionbuddy.models.ConnectivityEvent;
import com.zplesac.connectionbuddy.sampleapp.R;
import com.zplesac.connectionbuddy.sampleapp.dagger.components.DaggerMVPComponent;
import com.zplesac.connectionbuddy.sampleapp.dagger.modules.ContextModule;
import com.zplesac.connectionbuddy.sampleapp.dagger.modules.MVPModule;
import com.zplesac.connectionbuddy.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectionbuddy.sampleapp.mvp.views.MVPView;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MVPActivity extends Activity implements MVPView {

    private TextView tvTitle;
    private TextView tvConnectionType;

    @Inject
    MVPPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mvp);

        DaggerMVPComponent.builder().mVPModule(new MVPModule(this)).contextModule(new ContextModule(this)).build().init(this);

        presenter.init(savedInstanceState != null);
    }

    @Override
    public void initUI() {
        tvTitle = findViewById(R.id.tv_title);
        tvConnectionType = findViewById(R.id.tv_connection_type);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.registerForNetworkUpdates();
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterFromNetworkUpdates();
    }

    @Override
    public void onConnectionChangeEvent(ConnectivityEvent event) {
        tvTitle.setText(String.format(getString(R.string.connection_status), event.getState()));
        tvConnectionType.setText(String.format(getString(R.string.connection_type), event.getType()));
    }
}
