package com.zplesac.connectify.sampleapp.dagger.components;

import com.zplesac.connectify.sampleapp.activities.MVPActivity;
import com.zplesac.connectify.sampleapp.dagger.modules.ContextModule;
import com.zplesac.connectify.sampleapp.dagger.modules.MVPModule;
import dagger.Component;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Component(modules = {MVPModule.class, ContextModule.class})
public interface MVPComponent {

    void init(MVPActivity activity);
}
