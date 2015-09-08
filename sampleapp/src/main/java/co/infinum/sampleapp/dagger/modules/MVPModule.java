package co.infinum.sampleapp.dagger.modules;

import co.infinum.sampleapp.mvp.presenters.MVPPresenter;
import co.infinum.sampleapp.mvp.presenters.impl.MVPPresenterImpl;
import co.infinum.sampleapp.mvp.views.MVPView;
import dagger.Module;
import dagger.Provides;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Module
public class MVPModule {

    private MVPView view;

    public MVPModule(MVPView view) {
        this.view = view;
    }

    @Provides
    public MVPView provideView(){
        return view;
    }

    @Provides
    public MVPPresenter providePresenter(MVPPresenterImpl presenter){
        return presenter;
    }
}
