package co.infinum.sampleapp.mvp.presenters;

import co.infinum.connectionutils.presenters.ConnectifyPresenter;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPPresenter extends ConnectifyPresenter {

    void init(boolean hasSavedInstanceState);
}
