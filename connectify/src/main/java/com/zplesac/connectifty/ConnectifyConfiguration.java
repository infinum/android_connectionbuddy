package com.zplesac.connectifty;

import android.content.Context;

/**
 * Created by Željko Plesac on 09/10/15.
 */
public class ConnectifyConfiguration {

    private Context context;

    private ConnectifyConfiguration(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public static class Builder {

        private Context context;

        public Builder(Context context) {
            this.context = context.getApplicationContext();
        }

        public ConnectifyConfiguration build() {
            Context context = this.context;
            return new ConnectifyConfiguration(context);
        }
    }
}
