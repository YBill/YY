package com.ioyouyun.home.presenter;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;

import com.ioyouyun.base.BasePresenter;
import com.ioyouyun.home.view.HomeView;
import com.ioyouyun.settings.biz.OnSettingListener;
import com.ioyouyun.settings.biz.SettingRequest;
import com.ioyouyun.settings.biz.SettingRequestImpl;
import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.LoginSharedUtil;
import com.ioyouyun.utils.PlatformSharedUtil;
import com.weimi.media.WMedia;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 卫彪 on 2016/8/12.
 */
public class HomePresenter extends BasePresenter<HomeView> {

    private SettingRequest settingRequest;
    private Handler handler;
    private Activity activity;

    public HomePresenter(Activity activity) {
        this.activity = activity;
        settingRequest = new SettingRequestImpl();
        handler = new Handler(Looper.getMainLooper());
    }

    // 注销
    public void onDestroy() {
        WMedia.getInstance().exit();
    }

    /**
     * 切换版本
     *
     * @return
     */
    public void switchVersion() {
        try {
            LoginSharedUtil.getInstance().setLogin(false);
            PlatformSharedUtil.getInstance().setPlatform(!FunctionUtil.isOnlinePlatform);
        } catch (Exception e) {
            e.printStackTrace();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mView != null)
                    mView.switchVersion();
            }
        }, 500);
    }

    /**
     * 获取Push信息
     */
    public void getPushInfo() {
        settingRequest.getPushTime(new OnSettingListener() {
            @Override
            public void onSuccess(String response) {
                analysisPushInfo(response);
            }

            @Override
            public void onFail() {

            }
        });
    }

    /**
     * 设置Push时段
     *
     * @param startTime
     * @param endTime
     */
    public void setPushInfo(final int startTime, final int endTime) {
        settingRequest.pushCreate(String.valueOf(startTime), String.valueOf(endTime), new OnSettingListener() {
            @Override
            public void onSuccess(String response) {
                pushHandler(startTime, endTime);
            }

            @Override
            public void onFail() {

            }
        });
    }

    private void pushHandler(final int startTime, final int endTime) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (mView != null) {
                    mView.setPushTime(startTime, endTime);
                }
            }
        });
    }

    private void analysisPushInfo(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            if (jsonObject == null)
                return;
            String code = jsonObject.optString("code", null);
            if (code != null && code.equals("200")) {
                JSONObject msgObj = jsonObject.optJSONObject("msg");
                int startTime = msgObj.optInt("start_time");
                int endTime = msgObj.optInt("end_time");
                pushHandler(startTime, endTime);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
