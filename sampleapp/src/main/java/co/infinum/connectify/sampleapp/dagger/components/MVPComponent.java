package co.infinum.connectify.sampleapp.dagger.components;

import co.infinum.connectify.sampleapp.activities.MVPActivity;
import co.infinum.connectify.sampleapp.dagger.modules.ContextModule;
import co.infinum.connectify.sampleapp.dagger.modules.MVPModule;
import dagger.Component;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Component(modules = {MVPModule.class, ContextModule.class})
public interface MVPComponent {

    void init(MVPActivity activity);
}
