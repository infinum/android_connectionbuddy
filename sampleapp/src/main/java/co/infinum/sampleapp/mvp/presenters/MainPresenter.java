package co.infinum.sampleapp.mvp.presenters;

import co.infinum.connectionutils.presenters.ConnectionPresenter;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MainPresenter extends ConnectionPresenter{

    void init(boolean hasSavedInstanceState);
}
