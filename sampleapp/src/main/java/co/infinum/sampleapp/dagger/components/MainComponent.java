package co.infinum.sampleapp.dagger.components;

import co.infinum.sampleapp.activities.MainActivity;
import co.infinum.sampleapp.dagger.modules.MainModule;
import dagger.Component;

/**
 * Created by Željko Plesac on 02/09/15.
 */
@Component(modules = MainModule.class)
public interface MainComponent {

    void init(MainActivity activity);
}
