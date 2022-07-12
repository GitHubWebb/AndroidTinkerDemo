# [完整接入流程](https://bugly.qq.com/docs/user-guide/instruction-manual-android-hotfix-demo/?v=1.0.0)
  * 打基准包安装并上报联网（注：填写唯一的tinkerId）
  * 对基准包的bug修复（可以是Java代码变更，资源的变更）
  * 修改基准包路径、修改补丁包tinkerId、mapping文件路径（如果开启了混淆需要配置）、resId文件路径
  * 执行buildTinkerPatchRelease打Release版本补丁包
  * 选择app/build/outputs/patch目录下的补丁包并上传（注：不要选择tinkerPatch目录下的补丁包，不然上传会有问题）
  * 编辑下发补丁规则，点击立即下发
  * 杀死进程并重启基准包，请求补丁策略（SDK会自动下载补丁并合成）
  * 再次重启基准包，检验补丁应用结果
  * 查看页面，查看激活数据的变化

---



# [Github Demo](https://github.com/BuglyDevTeam/Bugly-Android-Demo)

---



# 打包方式

## 普通打包

### 1、编译基准包

**配置基准包的tinkerId**

![配置基准包的tinkerId](https://bugly.qq.com/docs/img/hotfix/android/Snip20170113_3.png)

tinkerId最好是一个唯一标识，例如git版本号、versionName等等。 如果你要测试热更新，你需要对基线版本进行联网上报。

> 这里强调一下，基线版本配置一个唯一的tinkerId，而这个基线版本能够应用补丁的前提是集成过热更新SDK，并启动上报过联网，这样我们后台会将这个tinkerId对应到一个目标版本，例如tinkerId = "bugly_1.0.0" 对应了一个目标版本是1.0.0，基于这个版本打的补丁包就能匹配到目标版本。

执行`assembleRelease`编译生成基准包：

![编译基准包](https://bugly.qq.com/docs/img/hotfix/android/Snip20170113_4.png)

这个会在build/outputs/bakApk路径下生成每次编译的基准包、混淆配置文件、资源Id文件，如下图所示：

![生成的基线版本](https://bugly.qq.com/docs/img/hotfix/android/Snip20170209_2.png)

> **实际应用中，请注意保存线上发布版本的基准apk包、mapping文件、R.txt文件，如果线上版本有bug，就可以借助我们tinker-support插件进行补丁包的生成**。

**启动apk，上报联网数据**

我们每次冷启动都会请求补丁策略，会上报当前版本号和tinkerId，这样我们后台就能将这个唯一的tinkerId对应到一个版本，大家测试的时候可以打开logcat查看我们的日志，如下图所示：

![上报联网数据](https://bugly.qq.com/docs/img/hotfix/android/Snip20161213_28.png)

**如果看不到log，您需要将bugly初始化的第三个参数设置为true才能看到**。

### 2、对基线版本的bug修复

**未修复前**

![未修复前](https://bugly.qq.com/docs/img/hotfix/android/Snip20170113_7.png)

这个类有一个会造成空指针的方法。

**修复后**

![修复后](https://bugly.qq.com/docs/img/hotfix/android/Snip20170113_8.png)

对产生bug的类进行修复，作为补丁下次覆盖基线版本的类。

### 3、根据基线版本生成补丁包

**修改待修复apk路径、mapping文件路径、resId文件路径**

![配置apk路径、mapping文件路径、resId文件路径](https://bugly.qq.com/docs/img/hotfix/android/Snip20170113_12.png)

**执行构建补丁包的task**

![执行构建补丁包的task](https://bugly.qq.com/docs/img/hotfix/android/Snip20170209_4.png)

如果你要生成不同编译环境的补丁包，只需要执行TinkerSupport插件生成的task，比如`buildTinkerPatchRelease`就能生成release编译环境的补丁包。 注：TinkerSupport插件版本低于1.0.4的，需要使用tinkerPatchRelease来生成补丁包 。

生成的补丁包在**build/outputs/patch**目录下：

![build/outputs/patch](https://bugly.qq.com/docs/img/hotfix/android/1479216059696.png)

大家这里可能会有一个疑问，补丁版本是怎么匹配到目标版本的，可以双击patch包，我们提供的插件会在tinker生成的patch包基础上插入一个MF文件：

![tinker-support插件日志](https://bugly.qq.com/docs/img/hotfix/android/Snip20161215_54.png)

![Yapatch.MF文件](https://bugly.qq.com/docs/img/hotfix/android/Snip20161215_55.png)

### 4、上传补丁包到平台

**上传补丁包到平台并下发编辑规则**

![点击发布新补丁](https://bugly.qq.com/docs/img/hotfix/android/1479199765798.png)

![选择文件](https://bugly.qq.com/docs/img/hotfix/android/1479199723899.png)

![编辑规则](https://bugly.qq.com/docs/img/hotfix/android/Snip20170209_6.png)

点击`发布新补丁`，上传前面生成的patch包，我们平台会自动为你匹配到目标版本，你可以选择下发范围（开发设备、全量设备、自定义），填写完备注之后，点击立即下发让补丁生效，这样你就可以在客户端当中收到我们的策略，SDK会自动帮你把补丁包下到本地。

### 5、测试补丁应用效果

**启动app应用patch**

![启动app应用patch](https://bugly.qq.com/docs/img/hotfix/android/1479215662913.png)

如果匹配到目标版本，后台就会下发补丁策略，可以在logcat看到如下日志：

![补丁策略下发](https://bugly.qq.com/docs/img/hotfix/android/Snip20161213_27.png)

下载成功之后，我们会立即去合成补丁，可以看到patch合成的日志：

![patch合成日志](https://bugly.qq.com/docs/img/hotfix/android/Snip20161213_26.png)

**重启app查看效果**

![重启app查看效果](https://bugly.qq.com/docs/img/hotfix/android/1479200382416.png)

注：我们方案是基于Tinker方案的实现，需要下次启动才能让补丁生效。

## 多渠道打包

> Bugly支持两种方式进行多渠道打包：
>
> 1. gradle配置productFlavors方式
> 2. 多渠道打包工具打多渠道包方式（推荐）

### 方法1：gradle配置productFlavors方式

#### 1. 配置productFlavors

```gradle
android {
    ...

    // 多渠道打包（示例配置）
    productFlavors {
        xiaomi {
        }

        yyb {
        }
    }

  ...

}
```

#### 2. 执行`assembleRelease`生成基线apk

按照普通打包方式正常配置基线版本的tinkerId，然后执行assembleRelease生成不同渠道的apk，会在工程中build/bakApk/生成如下图所示文件：

![生成渠道基线apk](https://bugly.qq.com/docs/img/hotfix/android/Snip20161218_8.png)

#### 3. 打渠道补丁包配置

![打渠道补丁包配置](https://bugly.qq.com/docs/img/hotfix/android/Snip20170209_12.png)

#### 4.执行`buildAllFlavorsTinkerPatchRelease`生成所有渠道补丁包

如下图所示：

![生成渠道补丁包](https://bugly.qq.com/docs/img/hotfix/android/Snip20170209_13.png)

#### 5.测试应用补丁包

与普通打包一致。

### 方法2：多渠道打包工具打多渠道包方式（推荐）

> 多渠道工具打包方式，参考这篇文章：[Bugly多渠道热更新解决方案](https://buglydevteam.github.io/2017/05/15/solution-of-multiple-channel-hotpatch/)

## 加固打包

> 需要集成升级SDK版本1.3.0以上版本才支持加固。
>
> 经过测试的加固产品：
>
> 1. 腾讯乐固
> 2. 爱加密
> 3. 梆梆加固
> 4. 360加固（SDK 1.3.1之后版本支持）
>
> 其他产品需要大家进行验证。

### 1.设置isProtectedApp参数

> 在tinker-support配置当中设置**isProtectedApp = true**，
>表示你要**打加固的的apk**。 是否使用加固模式，仅仅将变更的类合成补丁。注意，这种模式仅仅可以用于加固应用中。

### 2.将基准包进行加固

如果你的app需要进行加固，你需要将你打出的基准包上传到具体的加固平台进行加固，例如[乐固](http://legu.qcloud.com/)，加固完成之后需要对apk进行**重签名**：

```
jarsigner -verbose -keystore <YOUR_KEYSTORE> -signedjar <SIGNED_APK> <UNSIGNED_APK> <KEY_ALIAS>
```

> 以上命令说明：
> -verbose：指定生成详细输出
> -keystore：指定证书存储路径
> -signedjar：改选项的三个参数分别为签名后的apk包、未签名的apk包、数字证书别名

示例：

```
 jarsigner -verbose -keystore keystore/release.keystore -signedjar app-release-signed.apk app-release.encrypted.apk testres
```

如果你使用的加固产品提供了GUI的加固和签名工具，可以通过工具来进行加固和签名。

### 3.根据未加固的基准包生成补丁包

打patch包的操作跟普通打包方式一致。

> 注意：这里指定的基线版本是未加固的版本。

### 4.测试验证

> 测试验证的版本是**经过加固并且已经重签名后的apk**，安装到测试设备并上报联网，其他操作与普通打包无异。



---

<1>使用assembleRelease构建base apk，构建完了要安装这个apk并联网激活，否则补丁无法上传到后台，因为补丁合法的前提是有对应的base版本存在

<2>修改tinker-support.gradle，def baseApkDir = "base apk所在目录"，这是为了第三步构建patch做准备

<3>随便修改一行代码，使用buildTinkerPatchRelease构建生成patch，好了到此为止你可以将patch上传到后台

<4>打开base apk后很多同学疑惑为什么收不到更新包，如果你也是这样，不放参考demo加个按钮主动查询后台是否有更新包存在，
同时查看logcat日志TAG为"crashreport"。bugly获取更新包策略需要客户端主动请求而非后台推送，
因此用户可以根据app使用场景来决定什么时机在什么页面调用Beta.checkUpgrade()来请求更新策略。





---




## [热更新so，怎样判断是否成功](https://hub.fastgit.xyz/BuglyDevTeam/Bugly-Android-Demo/wiki/%E7%83%AD%E6%9B%B4%E6%96%B0so%EF%BC%8C%E6%80%8E%E6%A0%B7%E5%88%A4%E6%96%AD%E6%98%AF%E5%90%A6%E6%88%90%E5%8A%9F)

## [热更新SDK接入说明](https://github.com/BuglyDevTeam/Bugly-Android-Demo/wiki/%E7%83%AD%E6%9B%B4%E6%96%B0SDK%E6%8E%A5%E5%85%A5%E8%AF%B4%E6%98%8E)