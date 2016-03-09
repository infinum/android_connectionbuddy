package com.zplesac.connectionbuddy.interfaces;

/**
 * Created by Željko Plesac on 09/03/16.
 */
public interface NetworkRequestCheckListener {

    void onResponseObtained();

    void onNoResponse();

}
