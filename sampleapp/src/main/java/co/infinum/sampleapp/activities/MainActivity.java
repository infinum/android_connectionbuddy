package co.infinum.sampleapp.activities;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import co.infinum.sampleapp.R;
import co.infinum.sampleapp.dagger.components.DaggerMainComponent;
import co.infinum.sampleapp.dagger.modules.MainModule;
import co.infinum.sampleapp.mvp.presenters.MainPresenter;
import co.infinum.sampleapp.mvp.views.MainView;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public class MainActivity extends Activity implements MainView {

    private TextView tvTitle;

    @Inject
    MainPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        DaggerMainComponent.builder().mainModule(new MainModule(this)).build().init(this);

        presenter.init();
    }

    @Override
    public void initUI() {
        tvTitle = (TextView) findViewById(R.id.tv_title);
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
        if(hasConnection){
            tvTitle.setText("Connection active");
        }
        else{
            tvTitle.setText("Connection inactive");
        }
    }
}
