package com.wwx.androidtinkerdemo.tinkersdk.app;

import com.tencent.tinker.loader.app.TinkerApplication;
import com.tencent.tinker.loader.shareutil.ShareConstants;


/**
 * <pre>
 *      author:         wangweixu
 *      date:           2022-07-05 09:59:19
 *      description:    初始化Bugly+Tinker
 *                      方式一：反射TinkerProxyApplicationLike
 *                      方式二：不反射，官方建议的方式，该方式会增加接入成本，但有更好的兼容性
 *                      ∆ 此处采用方式二,  那么enableProxyApplication要等于false，
 *                      那么想停用Tinker的话还需要去处理application。
 *      version:        v1.0
 * </pre>
 */
public class TinkerProxyApplication extends TinkerApplication {

    // 此文件只写这段代码，其他代码统一全部放到TinkerProxyApplicationLikeLike里面去

    /**
     * tinkerFlags 表示Tinker支持的类型 dex only、library only or all suuport，default: TINKER_ENABLE_ALL
     * delegateClassName Application代理类 这里填写你自定义的ApplicationLike
     * loaderClassName Tinker的加载器，使用默认即可
     * tinkerLoadVerifyFlag 加载dex或者lib是否验证md5，默认为false
     */
    public TinkerProxyApplication() {
        super(ShareConstants.TINKER_ENABLE_ALL, "com.wwx.androidtinkerdemo.app.TinkerProxyApplicationLike",
                "com.tencent.tinker.loader.TinkerLoader", false);
    }
}
