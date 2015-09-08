package co.infinum.sampleapp.dagger.components;

import co.infinum.sampleapp.activities.MVPActivity;
import co.infinum.sampleapp.dagger.modules.ContextModule;
import co.infinum.sampleapp.dagger.modules.MVPModule;
import dagger.Component;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Component(modules = {MVPModule.class, ContextModule.class})
public interface MVPComponent {

    void init(MVPActivity activity);
}
