package com.jamiexu.app.reflectmaster;

import android.app.Application;
import android.content.Context;

import com.jamiexu.app.J;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public final class MyApplication extends Application {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            try {
                PrintWriter printWriter = new PrintWriter(new FileOutputStream(MainActivity.BASE_PATH + "error.log"));
                e.printStackTrace(printWriter);
                printWriter.println("ApplicationName:" + getApplicationInfo().className);
                printWriter.flush();
                printWriter.close();
                System.exit(0);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

        });

        J.i(base);
    }

}
