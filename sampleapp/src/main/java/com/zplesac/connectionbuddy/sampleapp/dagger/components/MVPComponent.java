package com.zplesac.connectionbuddy.sampleapp.dagger.components;

import com.zplesac.connectionbuddy.sampleapp.activities.MVPActivity;
import com.zplesac.connectionbuddy.sampleapp.dagger.modules.ContextModule;
import com.zplesac.connectionbuddy.sampleapp.dagger.modules.MVPModule;

import dagger.Component;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Component(modules = {MVPModule.class, ContextModule.class})
public interface MVPComponent {

    void init(MVPActivity activity);
}
