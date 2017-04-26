package com.jeeson.android.mvp.base.delegate;

import android.app.Application;


import com.jeeson.android.mvp.di.component.AppComponent;
import com.jeeson.android.mvp.di.component.DaggerAppComponent;
import com.jeeson.android.mvp.di.module.AppModule;
import com.jeeson.android.mvp.di.module.ClientModule;
import com.jeeson.android.mvp.di.module.GlobalConfigModule;
import com.jeeson.android.mvp.di.module.ImageModule;
import com.jeeson.android.mvp.integration.ActivityLifecycle;
import com.jeeson.android.mvp.integration.ConfigModule;
import com.jeeson.android.mvp.integration.ManifestParser;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * AppDelegate可以代理Application的生命周期,在对应的生命周期,执行对应的逻辑,因为Java只能单继承
 * 而我的框架要求Application要继承于BaseApplication
 * 所以当遇到某些三方库需要继承于它的Application的时候,就只有自定义Application继承于三方库的Application
 * 再将BaseApplication的代码复制进去,而现在就不用在复制代码,只用在对应的生命周期调用AppDelegate对应的方法,
 *
 * Created by jess on 24/04/2017 09:44
 * Contact with jess.yan.effort@gmail.com
 */

public class AppDelegate {
    private Application mApplication;
    private AppComponent mAppComponent;
    @Inject
    protected ActivityLifecycle mActivityLifecycle;
    private final List<ConfigModule> mModules;
    private List<Lifecycle> mLifecycles = new ArrayList<>();

    public AppDelegate(Application application) {
        this.mApplication = application;
        this.mModules = new ManifestParser(mApplication).parse();
        for (ConfigModule module : mModules) {
            module.injectAppLifecycle(mApplication, mLifecycles);
        }
    }


    public void onCreate() {
        mAppComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(mApplication))////提供application
                .clientModule(new ClientModule())//用于提供okhttp和retrofit的单例
                .imageModule(new ImageModule())//图片加载框架默认使用glide
                .globalConfigModule(getGlobalConfigModule(mApplication, mModules))//全局配置
                .build();
        mAppComponent.inject(this);

        mApplication.registerActivityLifecycleCallbacks(mActivityLifecycle);

        for (ConfigModule module : mModules) {
            module.registerComponents(mApplication, mAppComponent.repositoryManager());
        }


        for (Lifecycle lifecycle : mLifecycles) {
            lifecycle.onCreate(mApplication);
        }

    }


    public void onTerminate() {
        if (mActivityLifecycle != null) {
            mApplication.unregisterActivityLifecycleCallbacks(mActivityLifecycle);
        }
        this.mAppComponent = null;
        this.mActivityLifecycle = null;
        this.mApplication = null;

        for (Lifecycle lifecycle : mLifecycles) {
            lifecycle.onTerminate(mApplication);
        }
    }


    /**
     * 将app的全局配置信息封装进module(使用Dagger注入到需要配置信息的地方)
     * 需要在AndroidManifest中声明{@link ConfigModule}的实现类,和Glide的配置方式相似
     *
     * @return
     */
    private GlobalConfigModule getGlobalConfigModule(Application context, List<ConfigModule> modules) {

        GlobalConfigModule.Builder builder = GlobalConfigModule
                .builder();

        for (ConfigModule module : modules) {
            module.applyOptions(context, builder);
        }

        return builder.build();
    }


    /**
     * 将AppComponent返回出去,供其它地方使用, AppComponent接口中声明的方法返回的实例,在getAppComponent()拿到对象后都可以直接使用
     *
     * @return
     */
    public AppComponent getAppComponent() {
        return mAppComponent;
    }


    public interface Lifecycle {
        void onCreate(Application application);

        void onTerminate(Application application);
    }

}


