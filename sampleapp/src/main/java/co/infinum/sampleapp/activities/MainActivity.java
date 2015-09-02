package co.infinum.sampleapp.activities;

import android.app.Activity;
import android.os.Bundle;

import javax.inject.Inject;

import co.infinum.sampleapp.mvp.presenters.MainPresenter;
import co.infinum.sampleapp.mvp.views.MainView;
import co.infinum.sampleapp.R;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MainActivity extends Activity implements MainView {

    @Inject
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();
        presenter.registerForNetworkUpdates(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        presenter.unregisterFromNetworkUpdates(this);
    }

    @Override
    public void onConnectionChangeEvent(boolean hasConnection) {

    }
}
