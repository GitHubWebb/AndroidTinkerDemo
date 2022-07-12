# AndroidTinkerDemo
# 下面为热修复的一些介绍, 不感兴趣可跳过直接看使用

## 为什么需要热修复?

身为移动端开发相信大家都有这么一个痛点,  App一旦发版后, 在想修改只能重新发包, 近几年还好些审核速度很快, 前些年都是以天为单位, 正常的需求改造功能优化还好些, 要是线上出现bug, 那就等着扣绩效奖励吧, 

小bug还好, 如果后端同学肯配合可能某些数据可以通过别的途径拿到问题就还不大

大bug的话, 就只能紧急修复后走本地强制更新和奢求各大应用能快些审核通过

每次都特别羡慕H5的动态更新能力, 前端同学编写好代码通过git/svn把代码push到远端线上通过Jenkins部署线上直接就可以访问最新页面, 简直爽歪歪

基于此有了热更新热修复的概念于是国内的大厂各显身手弄出各种热更新的框架

## 什么是热修复?

动态更新和修复app的行为
	一般的bug修复，都是等下一个版本解决，然后发布新的apk。
热修复：可以直接在客户已经安装的程序当中修复bug。刚上线 用户才更新完，又来了一个强制更新很容易让用户发火而流失热修复是一个亡羊补牢的做法，和发布版本高质量的版本和热修复完美搭配流行技术：

## 常见的热修复有哪些?

| 对比                | Tinker                  | QZone | AndFix | Robust |
| ------------------- | ----------------------- | ----- | ------ | ------ |
| 类替换              | √                       | √     | X      | X      |
| So替换              | √                       | X     | X      | X      |
| 资源替换            | √                       | √     | X      | X      |
| 2.X－11.X全平台支持 | √                       | √     | X      | √      |
| 即时生效            | X                       | X     | √      | √      |
| 性能损耗            | 较小                    | 较大  | 较小   | 较小   |
| 补丁包大小          | 较小                    | 较大  | 一般   | 一般   |
| 开发透明            | √                       | √     | X      | X      |
| 复杂度              | 较低                    | 较低  | 复杂   | 复杂   |
| 成功率              | 较高                    | 较高  | 一般   | 最高   |
| 使用数              | 微信的数亿 Android 设备 | 较高  | 一般   | 较少   |



# 开始使用

出于对微信的信任, 经过技术调研后决定使用Tinker作为热更新框架, 在早期时候使用Tinker是使用TinkerPatch控制台上传补丁包, 后期已被Bugly进行整合可以在Bugly中进行补丁更新操作

1. 因为官方 WIKI过老, Demo版本过低, 如果在高版本Gradle中插件会无法使用, 经过一番摸索后整理出适合高版本AS使用的插件版本

```plain
Android Studio 4.2.2 
配套gradle版本为
classpath 'com.android.tools.build:gradle:4.1.0'
https\://services.gradle.org/distributions/gradle-6.5.1-all.zip

对应插件
        // 腾讯Bugly&Tinker合并插件
        // tinkersupport插件, 其中lastest.release指拉取最新版本，也可以指定明确版本号，例如1.0.4
        // https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix/?v=1.0.0
        // https://search.maven.org/search?q=a:tinker-support
        classpath "com.tencent.bugly:tinker-support:1.2.3"
```

2. module build.gradle 配置

```plain
   注意使用Tinker需要配置多Dex支持
   defaultConfig {
     multiDexEnabled true
   }
   
   implementation "com.android.support:multidex:1.0.2" // 多dex配置
   
   // 如果项目中使用了AndroidX 则crashreport_upgrade应该依赖版本1.5.23
   api 'com.tencent.bugly:crashreport_upgrade:1.5.0'
   // 指定tinker依赖版本（注：应用升级1.3.5版本起，不再内置tinker）
   // tinker-android-lib 1.9.14.22 版本为截止发文最新版本(2022-07-12), 建议使用写死版本方法
   // 如果想实时保持最新版本可以采用tinker-android-lib:latest.release
   api 'com.tencent.tinker:tinker-android-lib:1.9.14.22'
  // api 'com.tencent.tinker:tinker-android-lib:latest.release'
   api 'com.tencent.bugly:nativecrashreport:3.9.1' //其中latest.release指代最新版本号，也可以指定明确的版本号，例如2.2.0


  // 为了便于测试, 使用轮子哥的Toast框架进行展示
  // 吐司框架：https://github.com/getActivity/ToastUtils
  implementation 'com.github.getActivity:ToastUtils:10.3'
```

3. 常用Api

```plain
Beta.installTinker(tinkerProxyApplicationLike);
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
        // ∆注意 要想修改配置 需要在init之前修改才会生效
        Bugly.init(mApp, BuildConfig.BuglyAppId, LogFactory.isDebug());
    }
public void checkHotFixUp() {

/*
       上面 init方法会自动检测更新，不需要再手动调用Beta.checkUpgrade(), 如需增加自动检查时机可以使用Beta.checkUpgrade(false,false);

        参数1：isManual 用户手动点击检查，非用户点击操作请传false
        参数2：isSilence 是否显示弹窗等交互，[true:没有弹窗和toast] [false:有弹窗或toast]
        第一个参数传递false 屏蔽 Bugly版本更新Toast提示 true 会Toast提示 "当前为最新版本"
        
        个人建议在代理 Application 调用init初始化 进行自动补丁检测机制后, 在页面其他地方手动在调用一遍 增加成功几率
    */
        Beta.checkUpgrade(false, false);
        
        // 经过测验, 如果不调用该方法有部分手机加固后 不能正常检测下载补丁
        Beta.downloadPatch();

    }
    /**
     * 注：清除补丁之后，就会回退基线版本状态。
     * @param isNowDelete true 立刻删除(爱加密加固后 会被重启) false 等待重启进程界面在删除补丁
     */
    public void cleanTinkerPatch(boolean isNowDelete) {
        Beta.cleanTinkerPatch(isNowDelete);
    }
```



---



# 查看具体使用方式[AndroidTinkerDemo](https://github.com/GitHubWebb/AndroidTinkerDemo)



---



# 疑难杂症解决办法

1. **Gradle版本问题**

>  A problem occurred configuring project ':app'.
>
> Failed to notify project evaluation listener.No such property: variantConfiguration for class: com.android.build.gradle.internal.variant.ApplicationVariantData
>
> can't find tinkerProcessDebugManifest, you must init tinker plugin first! 
>
> 使用         classpath "com.tencent.bugly:tinker-support:1.2.3"

2. **public.txt : ERROR： failed reading stable ID file.**

> AGPBI: {"kind":"error","text":"Android resource linking failed","sources":[{}],"original":"ERROR:: AAPT: ...\build\intermediates\tinker_intermediates\public.txt: error: failed reading stable ID file.\n\n    ","tool":"AAPT"}
> ERROR:: AAPT: ...\build\intermediates\tinker_intermediates\public.txt: 
>
> error: failed reading stable ID file.

```plain
def createFile(path) {
  File file = new File(path)
  if (!file.exists()) {
    file.createNewFile()
  }
}

android.applicationVariants.all { variant ->
  /**
  * task type, you want to bak
  */
  def taskName = variant.name
  def path = "${buildDir}/intermediates/tinker_intermediates/public.txt"
  
  tasks.all {
      if ("process${taskName.capitalize()}Resources".equalsIgnoreCase(it.name)) {
          it.doFirst {
              createFile(path)
          }
      }
  }
}
```

3. 补丁包发布无效

1. 1. 要将在**build/outputs/patch**目录下的补丁包上传在Bugly中, 不是 tinkerpatch
   2. 注意打包方式不能使用'run'模式编译的app, 可以使用assembleDebug / buildTinkerPatchDebug 
   3. 检查tinker-support.gradle 是否开启加固模式isProtectedApp = true 基准包加固状态对应不上可能会出现不能加载现象
   4. 非加固包读取补丁很快, 加固包很慢甚至不能加载可以尝试调用        Beta.downloadPatch();

4. 打补丁包时提示tinker_id is empty!

1. 1. 检查是否有手动设置tinker_id
   2. overrideTinkerPatchConfiguration 为false 后需要在*tinkerPatch设置tinker_id*
   3. 建议使用自动生成策略    autoGenerateTinkerId = true

5. 必须在app的build.gradle里面配置签名文件，否则打包时会因为找不到签名文件而失败

6. 同一个基准包下，发布了很多补丁包，建议把老补丁包撤销一下，否则用户可能会出现补丁混乱的问题 

7. 打补丁包错误:loader classes are found in old secondary dex.Found classes:XXX

> 在tinker-support.gradle的tinkerSupport里面加入tinkerEnable = true，ignoreWarning = true 
>
> 若还是无法处理，可以参考tinker官网该问题的处理(https://github.com/tencent/tinker/issues/104) 

8. 调用        Beta.cleanTinkerPatch(isNowDelete); 

>  'run'模式下 不会重启
>
> 打签名包安装手机上后, 调用Bugly清除补丁 重置为基准包 会重启App 

9. 使用爱加密后, debug包不能看到Bugly日志

>  原因是爱加密会拦截替换掉log.i log.d log.w方法, 可以通过ndk编写native方法解决, 或者在换成log.e
