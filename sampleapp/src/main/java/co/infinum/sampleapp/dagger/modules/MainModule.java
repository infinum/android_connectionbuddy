package co.infinum.sampleapp.dagger.modules;

import co.infinum.sampleapp.mvp.presenters.MainPresenter;
import co.infinum.sampleapp.mvp.presenters.impl.MainPresenterImpl;
import co.infinum.sampleapp.mvp.views.MainView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Module
public class MainModule {

    private MainView view;

    public MainModule(MainView view) {
        this.view = view;
    }

    @Provides
    public MainView provideView(){
        return view;
    }

    @Provides
    public MainPresenter providePresenter(MainPresenterImpl presenter){
        return presenter;
    }
}
