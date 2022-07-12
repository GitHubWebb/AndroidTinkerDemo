package com.wwx.androidtinkerdemo.app;

import android.annotation.TargetApi;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.multidex.MultiDex;
import android.text.TextUtils;
import android.util.Log;

import com.hjq.toast.ToastUtils;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.wwx.androidtinkerdemo.BuildConfig;
import com.wwx.androidtinkerdemo.tinkersdk.config.TinkerHelper;
import com.wwx.androidtinkerdemo.tinkersdk.util.LogFactory;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-05 09:59:19
 *      description:    SampleApplicationLike这个类是Application的代理类，
 *                      ∆ 以前所有在Application的实现必须要全部拷贝到这里，在onCreate方法调用SDK的初始化方法，在onBaseContextAttached中调用Beta.installTinker(this);。
 *                      初始化Bugly+Tinker
 *                      方式一：反射TinkerProxyApplicationLike
 *                      方式二：不反射，官方建议的方式，该方式会增加接入成本，但有更好的兼容性
 *                      ∆ 此处采用方式二,  那么enableProxyApplication要等于false，
 *                      那么想停用Tinker的话还需要去处理application。
 *      version:        v1.0
 * </pre>
 */
public class TinkerProxyApplicationLike extends DefaultApplicationLike implements
        BetaPatchListener {
    private static final String TAG = "TinkerProxyApplicationLike";

    public static TinkerProxyApplicationLike getInstance(){
        return proxyApplicationLike;
    }

    //<editor-fold desc="Application 成员变量">
    private static TinkerProxyApplicationLike proxyApplicationLike;
    private Application mApp;

    //</editor-fold>

    //<editor-fold desc="构造函数">
    public TinkerProxyApplicationLike(Application application, int tinkerFlags,
                                      boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime,
                                      long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags,
                tinkerLoadVerifyFlag, applicationStartElapsedTime,
                applicationStartMillisTime, tinkerResultIntent);

        mApp = getApplication();
        proxyApplicationLike = this;

    }
    //</editor-fold>

    @Override
    public void onCreate() {
        super.onCreate();

        TinkerHelper
                .getInstance()
                .setApplication(mApp)
                .setmBetaPatchListener(this)
                .configTinker();

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);

        // you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        // 初始化设置日志框架Debug模式 是否后台打印log日志  打包时改为false
        LogFactory.syncIsDebug(base, BuildConfig.DEBUG);

        TinkerHelper
                .getInstance()
                .setApplication(mApp)
                .installTinker(this);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();

    }

    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public void registerActivityLifecycleCallback(Application.ActivityLifecycleCallbacks callbacks) {
        mApp.registerActivityLifecycleCallbacks(callbacks);
    }

    public Context getApplicationContext() {
        return getApplication().getApplicationContext();
    }

    //<editor-fold desc="检测是否在主线程">
    /**
     * 检测是否在主线程
     * @return
     */
    public boolean isInMainThread() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
    //</editor-fold>

    @Override
    public void onPatchReceived(String s) {
        ToastUtils.show(s);
        /*HttpHelper.postCatchLog(
                TinkerHelper.getInstance().getHotFixFunctionName(),
                Constants.CATCHLOGINFO_LEVEL_INFO, s);*/
    }

    @Override
    public void onDownloadReceived(long l, long l1) {

    }

    @Override
    public void onDownloadSuccess(String s) {
        /*HttpHelper.postCatchLog(
                TinkerHelper.getInstance().getHotFixFunctionName(),
                Constants.CATCHLOGINFO_LEVEL_INFO, s);*/
    }

    @Override
    public void onDownloadFailure(String s) {
        /*HttpHelper.postCatchLog(
                TinkerHelper.getInstance().getHotFixFunctionName(),
                Constants.CATCHLOGINFO_LEVEL_WARN, s);*/
    }

    @Override
    public void onApplySuccess(String s) {
        /*HttpHelper.postCatchLog(
                TinkerHelper.getInstance().getHotFixFunctionName(),
                Constants.CATCHLOGINFO_LEVEL_INFO, s);*/
    }

    @Override
    public void onApplyFailure(String s) {
        /*HttpHelper.postCatchLog(
                TinkerHelper.getInstance().getHotFixFunctionName(),
                Constants.CATCHLOGINFO_LEVEL_ERROR, s);*/
    }

    @Override
    public void onPatchRollback() {

    }
}
