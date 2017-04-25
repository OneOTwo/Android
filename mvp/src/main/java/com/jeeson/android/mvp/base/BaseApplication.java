package com.jeeson.android.mvp.base;

import android.app.Application;

import com.jeeson.android.mvp.base.delegate.AppDelegate;
import com.jeeson.android.mvp.di.component.AppComponent;



/**
 * 本项目由
 * mvp
 * +dagger2
 * +retrofit
 * +rxjava
 * +androideventbus
 * +butterknife组成
 */
public  class BaseApplication extends Application {
    private AppDelegate mAppDelegate;


    @Override
    public void onCreate() {
        super.onCreate();
        this.mAppDelegate = new AppDelegate(this);
        this.mAppDelegate.onCreate();
    }

    /**
     * 程序终止的时候执行
     */
    @Override
    public void onTerminate() {
        super.onTerminate();
        this.mAppDelegate.onTerminate();
    }


    /**
     * 将AppComponent返回出去,供其它地方使用, AppComponent接口中声明的方法返回的实例,在getAppComponent()拿到对象后都可以直接使用
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return mAppDelegate.getAppComponent();
    }


}