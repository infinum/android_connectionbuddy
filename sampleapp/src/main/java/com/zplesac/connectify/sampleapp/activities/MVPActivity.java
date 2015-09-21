package com.zplesac.connectify.sampleapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import com.zplesac.connectify.sampleapp.R;
import com.zplesac.connectify.sampleapp.dagger.components.DaggerMVPComponent;
import com.zplesac.connectify.sampleapp.dagger.modules.ContextModule;
import com.zplesac.connectify.sampleapp.dagger.modules.MVPModule;
import com.zplesac.connectify.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectify.sampleapp.mvp.views.MVPView;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MVPActivity extends Activity implements MVPView {

    private TextView tvTitle;

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
        tvTitle = (TextView) findViewById(R.id.tv_title);
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
    public void onConnectionChangeEvent(boolean hasConnection) {
        if(hasConnection){
            tvTitle.setText("Connection active");
        }
        else{
            tvTitle.setText("Connection inactive");
        }
    }
}
