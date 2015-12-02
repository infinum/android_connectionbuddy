package com.zplesac.connectionbuddy.sampleapp.dagger.modules;

import com.zplesac.connectionbuddy.sampleapp.mvp.presenters.MVPPresenter;
import com.zplesac.connectionbuddy.sampleapp.mvp.presenters.impl.MVPPresenterImpl;
import com.zplesac.connectionbuddy.sampleapp.mvp.views.MVPView;

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
