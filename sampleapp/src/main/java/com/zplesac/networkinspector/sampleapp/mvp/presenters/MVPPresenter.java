package com.zplesac.networkinspector.sampleapp.mvp.presenters;


import com.zplesac.networkinspector.presenters.NetworkInspectorPresenter;

/**
 * Created by Željko Plesac on 02/09/15.
 */
public interface MVPPresenter extends NetworkInspectorPresenter {

    void init(boolean hasSavedInstanceState);
}
