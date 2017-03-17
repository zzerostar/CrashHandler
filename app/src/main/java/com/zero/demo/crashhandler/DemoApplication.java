package com.zero.demo.crashhandler;

import android.app.Application;

/**
 * Created by zengzhuo on 2017/3/17.
 */

public class DemoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(this);
    }
}
