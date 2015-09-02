package co.infinum.sampleapp.mvp.views;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MainView {

    void initUI();

    void onConnectionChangeEvent(boolean hasConnection);
}
