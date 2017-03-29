package com.ioyouyun.contacts.biz;

import com.ioyouyun.utils.FunctionUtil;
import com.ioyouyun.utils.Logger;
import com.ioyouyun.wchat.RequestType;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.util.HttpCallback;

import java.util.HashMap;
import java.util.List;

/**
 * Created by 卫彪 on 2016/6/25.
 */
public class ContactRequestImpl implements ContactRequest {

    @Override
    public void addLocation(String uid, double longitude, double latitude, int type, final OnContactListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("type", type);
        FunctionUtil.shortConnectRequest("/recommend/location/add", FunctionUtil.combineParamers(map), RequestType.POST, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public void getNearbyUsers(String uid, double longitude, double latitude, long range, final OnContactListener listener) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("uid", uid);
        map.put("longitude", longitude);
        map.put("latitude", latitude);
        map.put("range", range);
        FunctionUtil.shortConnectRequest("/recommend/users/nearby", FunctionUtil.combineParamers(map), RequestType.GET, new FunctionUtil.OnRequestListener() {
            @Override
            public void onSuccess(String response) {
                if (listener != null)
                    listener.onSuccess(response);
            }

            @Override
            public void onError(String error) {
                if (listener != null)
                    listener.onFaild();
            }
        });
    }

    @Override
    public void uploadAppInfo(String userName, String imgUrl, double longitude, double latitude, String provinceId, final OnContactListener listener) {
        WeimiInstance.getInstance().uploqadAppInfo(userName, imgUrl, longitude, latitude, provinceId, new HttpCallback() {
            @Override
            public void onResponse(String s) {
                Logger.v("uploadAppInfo success:" + s);
                if (listener != null)
                    listener.onSuccess(s);
            }

            @Override
            public void onResponseHistory(List list) {

            }

            @Override
            public void onError(Exception e) {
                Logger.v("uploadAppInfo:" + e.getMessage());
                if (listener != null)
                    listener.onFaild();
            }
        }, 60);
    }
}
