package com.wwx.androidtinkerdemo.tinkersdk.config;

import android.app.Application;

import com.tencent.bugly.Bugly;
import com.tencent.bugly.beta.Beta;
import com.tencent.bugly.beta.interfaces.BetaPatchListener;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.wwx.androidtinkerdemo.tinkersdk.BuildConfig;
import com.wwx.androidtinkerdemo.tinkersdk.util.LogFactory;

import java.util.Locale;

/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-07 15:51:55
 *      description:    Tinker包装类
 *      version:        v1.0
 * </pre>
 */
public class TinkerHelper {
    private static final String HOTFIX_TAG = "CrashReport-Tinker";
    private String hotFixFunctionName = "HotFix-Tinker\t";
    // 使用volatile修饰 目的是为了在JVM层编译顺序一致
    private static volatile TinkerHelper instance;

    private Application mApp;

    /** 补丁回调接口 */
    private BetaPatchListener mBetaPatchListener;

    private TinkerHelper() {

    }

    public static TinkerHelper getInstance() {
        //第一次校验
        if (instance == null) {
            synchronized (TinkerHelper.class) {

                //第二次校验
                if (instance == null) {
                    instance = new TinkerHelper();
                }
            }
        }

        return instance;
    }

    /** 安装tinker */
    public void installTinker(DefaultApplicationLike tinkerProxyApplicationLike) {
        Beta.installTinker(tinkerProxyApplicationLike);
    }

    /**
     * 初始化Tinker
     */
    public void configTinker() {
        //是否开启热更新能力
        Beta.enableHotfix = true;

        //是否开启自动下载补丁
        Beta.canAutoDownloadPatch = true;
        //是否自动合成补丁
        Beta.canAutoPatch = true;
        //是否提示用户重启
        Beta.canNotifyUserRestart = false;

        //补丁回调接口
        Beta.betaPatchListener = new BetaPatchListener() {
            @Override
            public void onPatchReceived(String s) {
                String patchRecived = "补丁下载地址：" + s ;
                LogFactory.e(HOTFIX_TAG, patchRecived);

                if(mBetaPatchListener != null)
                    mBetaPatchListener.onPatchReceived(patchRecived);

            }

            @Override
            public void onDownloadReceived(long l, long l1) {
                LogFactory.e(HOTFIX_TAG, String.format(Locale.getDefault(), "%s %s %d%%",
                        hotFixFunctionName + "补丁下载中",
                        Beta.strNotificationDownloading,
                        (int) (l1 == 0 ? 0 : l * 100 / l1)));
            }

            @Override
            public void onDownloadSuccess(String s) {
                String downLoadSuccess = "补丁下载成功";
                LogFactory.e(HOTFIX_TAG, downLoadSuccess);

                if(mBetaPatchListener != null)
                    mBetaPatchListener.onDownloadSuccess(downLoadSuccess);

            }

            @Override
            public void onDownloadFailure(String s) {
                String downLoadFail = "补丁下载失败";
                LogFactory.e(HOTFIX_TAG, downLoadFail);

                if(mBetaPatchListener != null)
                    mBetaPatchListener.onDownloadFailure(downLoadFail);
            }

            @Override
            public void onApplySuccess(String s) {
                String applySuccess = "补丁应用成功";
                LogFactory.e(HOTFIX_TAG, applySuccess);

                if(mBetaPatchListener != null)
                    mBetaPatchListener.onApplySuccess(applySuccess);
            }

            @Override
            public void onApplyFailure(String s) {
                String applyFail = "补丁应用失败";
                LogFactory.e(HOTFIX_TAG, applyFail);

                if(mBetaPatchListener != null)
                    mBetaPatchListener.onApplyFailure(applyFail);
            }

            @Override
            public void onPatchRollback() {

            }
        };

        //第二个参数true表示是开发设备，在Bugly的后台发布补丁时，可以选择全部用户还是开发设备
        Bugly.setIsDevelopmentDevice(mApp, LogFactory.isDebug());
        // 多渠道需求塞入
        // String channel = WalleChannelReader.getChannel(getApplication());
        // Bugly.setAppChannel(getApplication(), channel);
        // 这里实现SDK初始化，isDebug:是否是调试模式
        // 这里实现SDK初始化，appId替换成你的在Bugly平台申请的appId
        // 调试时，将第三个参数改为true
        Bugly.init(mApp, BuildConfig.BuglyAppId, LogFactory.isDebug());
    }

    /*
        init方法会自动检测更新，不需要再手动调用Beta.checkUpgrade(), 如需增加自动检查时机可以使用Beta.checkUpgrade(false,false);

        参数1：isManual 用户手动点击检查，非用户点击操作请传false
        参数2：isSilence 是否显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
        第一个参数传递false 屏蔽 Bugly版本更新Toast提示

    */
    public void checkHotFixUp() {

        Beta.checkUpgrade(false, false);
        Beta.downloadPatch();

    }

    /**
     * 清除补丁
     * 注：清除补丁之后，就会回退基线版本状态。
     * @param isNowDelete true 立刻删除(爱加密加固后 会被重启) false 等待重启进程界面在删除补丁
     */
    public void cleanTinkerPatch(boolean isNowDelete) {
        Beta.cleanTinkerPatch(isNowDelete);
    }

    public TinkerHelper setApplication(Application mApp) {
        this.mApp = mApp;
        return this;
    }

    public TinkerHelper setmBetaPatchListener(BetaPatchListener mBetaPatchListener) {
        this.mBetaPatchListener = mBetaPatchListener;
        return this;
    }

    public String getHotFixFunctionName() {
        return hotFixFunctionName;
    }
}
