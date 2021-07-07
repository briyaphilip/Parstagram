package com.example.parstagram;

import android.app.Application;

import com.parse.Parse;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("eLpkPo8JOuGDki1yLSMNAnva0dSNRsmSW7f7b8FL")
                .clientKey("k6zBWdA7pLgE76z43aIntocptX01gdY3IXsI33Yk")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
