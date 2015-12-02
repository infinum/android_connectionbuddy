package com.zplesac.connectionbuddy.sampleapp.dagger.modules;

import android.content.Context;
import android.content.res.Resources;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Module
public class ContextModule {

    private Context context;

    public ContextModule(Context context) {
        this.context = context;
    }

    @Provides
    public Context provideContext() {
        return context;
    }

    @Provides
    public Resources provideResources(Context context) {
        return context.getResources();
    }
}
