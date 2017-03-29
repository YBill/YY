package com.ioyouyun.receivemsg;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.ioyouyun.loadpage.SplashActivity;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.utils.PushSharedUtil;
import com.ioyouyun.utils.SoundVibrateUtil;
import com.weimi.push.ClickReceiver;
import com.weimi.push.data.PayLoadMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;

import java.util.Map;

/**
 * Created by 卫彪 on 2016/9/13.
 */
public class YouYunPushReceiver extends ClickReceiver {

    @Override
    protected void onYouYunReceiveMessage(Context context, PayLoadMessage payLoadMessage) {
        super.onYouYunReceiveMessage(context, payLoadMessage);
        if (payLoadMessage.sound != null) {
            if (PushSharedUtil.getInstance().getVibration()) {
                SoundVibrateUtil.checkIntervalTimeAndVibrate(context);
            }
            if (PushSharedUtil.getInstance().getSound()) {
                SoundVibrateUtil.checkIntervalTimeAndSound(context);
            }
        }
    }

    @Override
    protected void onNotificationClicked(Context context, Object message) {
        if(message instanceof PayLoadMessage){
            PayLoadMessage payLoadMessage = (PayLoadMessage) message;
            Logger.d("click:" + payLoadMessage.toString());
            Map map = payLoadMessage.customDictionary;
            toIntent(context, map);
        }else if(message instanceof MiPushMessage){
            MiPushMessage miPushMessage = (MiPushMessage) message;
            Logger.d("mi click:" + miPushMessage.toString());
            Map<String, String> map = miPushMessage.getExtra();
            toIntent(context, map);
        }

    }

    private void toIntent(Context context, Map<String, String> map) {
        if (map != null) {
            String page = map.get("page");
            Logger.d("page:" + page);
            if (page != null) {
                if (page.startsWith("http")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(page));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } else if (page.equals("youyun")) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("youyun://"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }else{
                    gotoHome(context);
                }
            } else {
                gotoHome(context);
            }
        }
    }

    private void gotoHome(Context context){
        Intent intent = new Intent(context, SplashActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }


}
