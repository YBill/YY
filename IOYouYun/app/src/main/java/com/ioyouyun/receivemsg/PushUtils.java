package com.ioyouyun.receivemsg;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.utils.FunctionUtil;
import com.weimi.push.WeimiPush;

/**
 * Created by 卫彪 on 2016/9/13.
 */
public class PushUtils {

    public static void startPush() {
        if (FunctionUtil.isOnlinePlatform)
            WeimiPush.connect(YouyunApplication.application, WeimiPush.pushServerIp, true);
        else
            WeimiPush.connect(YouyunApplication.application, WeimiPush.testPushServerIp, false);
    }

    public static void stopPush() {
        WeimiPush.disconnect(YouyunApplication.application);
    }
}
