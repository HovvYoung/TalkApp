package com.hovvyoung.talkapp;

import android.app.Application;

import com.parse.Parse;


public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("dDl6C6FtGQYA8X2Y7zCNY6otQacFzYPajNJcRzZJ")
                // if defined
                .clientKey("WDcOrcgmhaWDIKxYHiyVoMNTxa1QRFDEYNBOU0gP")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
