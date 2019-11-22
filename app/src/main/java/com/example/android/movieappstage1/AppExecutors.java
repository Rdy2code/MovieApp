package com.example.android.movieappstage1;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

/**Handle database queries off the main thread*/
public class AppExecutors {

    // For singleton instantiation
    private static final Object LOCK = new Object();
    private static AppExecutors sInstance;
    private final Executor diskIO;
    private final Executor networkIO;
    private final Executor mainThread;

    //Constructor
    private AppExecutors (Executor diskIO, Executor networkIO, Executor mainThread) {
        this.diskIO = diskIO;
        this.mainThread = mainThread;
        this.networkIO= networkIO;
    }

    //Instantiate single thread, network, and mainThread Executors using the singleton pattern
    public static AppExecutors getsInstance() {
        if (sInstance == null) {
            synchronized (LOCK) {
                sInstance = new AppExecutors(Executors.newSingleThreadExecutor(),   //single thread
                        Executors.newFixedThreadPool(3),                  //network thread
                        new MainThreadExecutor());                                 //mainThread
            }
        }
        return sInstance;
    }

    //Getter methods
    public Executor diskIO() {
        return diskIO;
    }

    public Executor networkIO() {
        return networkIO;
    }

    public Executor mainThread() {
        return mainThread;
    }

    //Inner class implementation of Executor interface
    private static class MainThreadExecutor implements Executor {
        private Handler mainThreadHandler = new Handler(Looper.myLooper().getMainLooper());

        @Override
        public void execute(@NonNull Runnable command) {
            mainThreadHandler.post(command);
        }
    }
}
