package com.ioyouyun.utils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.ioyouyun.YouyunApplication;
import com.ioyouyun.chat.ChatActivity;
import com.ioyouyun.chat.model.ChatMsgEntity;
import com.ioyouyun.datamanager.YouyunDbManager;
import com.ioyouyun.login.model.UserInfoEntity;
import com.ioyouyun.receivemsg.PushUtils;
import com.ioyouyun.receivemsg.ReceiveRunnable;
import com.ioyouyun.wchat.RequestType;
import com.ioyouyun.wchat.ServerType;
import com.ioyouyun.wchat.WeimiInstance;
import com.ioyouyun.wchat.data.AuthResultData;
import com.ioyouyun.wchat.message.ConvType;
import com.ioyouyun.wchat.message.WChatException;
import com.ioyouyun.wchat.util.DeviceInfo;
import com.ioyouyun.wchat.util.HttpCallback;
import com.weimi.media.WMedia;

import java.io.File;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by 卫彪 on 2016/6/6.
 */
public class FunctionUtil {

    public static final String MSG_TYPE_INCOMINGRECEIVED = "IncomingReceived"; // 来电消息 内容固定不可改
    public static final String MSG_TYPE_MEDIA_CALL_HEAD = "MediaSDK_"; // Media 头部 内容固定不可改
    public static final String MEDIA_CALL_CONNECTED = MSG_TYPE_MEDIA_CALL_HEAD + "Connected"; // 接通 内容固定不可改
    public static final String MEDIA_CALL_END = MSG_TYPE_MEDIA_CALL_HEAD + "CallEnd"; // 结束 内容固定不可改
    public static final String MEDIA_CALL_ERROR = MSG_TYPE_MEDIA_CALL_HEAD + "Error"; // 错误 内容固定不可改
    public static final String MSG_TYPE_CONFERENCE = "conference_"; // 来电消息
    public static final String CONFERENCE_REQUEST_ROOM = MSG_TYPE_CONFERENCE + "request_room"; // 申请房间
    public static final String CONFERENCE_LIST = MSG_TYPE_CONFERENCE + "list"; // 用户列表

    public static final String MSG_TYPE_NOTIFY = "msg_type_notify"; // 通知

    public static final String MSG_TYPE_RECEIVE = "msg_type_receive"; // 收到消息
    public static final String TYPE_MSG = "type_msg";
    public static final String MSG_TYPE_DOWNLOAD_FILE_PRO = "msg_type_download_file_pro"; // 下载文件进度
    public static final String MSG_TYPE_SEND_FILE_PRO = "msg_type_send_file_pro"; // 上传文件进度

    /**
     * Media
     */
    public static final String INCOMINGNAME = "incomingname"; // 来电人
    public static final String CONTENT = "content"; // 内容

    /**
     * notify
     */
    public static final String TYPE_NOTIFY = "type_notify";

    /**
     * im
     */
    public static final String FILE_FILEID = "file_fileid";
    public static final String FILE_PROGRESS = "file_progress";

    public static final String CONFERENCEACTIVITY_PATH = "com.ioyouyun.media.ConferenceActivity";
    public static final String VOIPACTIVITY_PATH = "com.ioyouyun.media.VoIPActivity";
    public static final String BEINVITEACTIVITYACTIVITY_PATH = "com.ioyouyun.media.BeInviteActivity";
    public static final String NOTIFYACTIVITYACTIVITY_PATH = "cn.youyunsample.home.NotifyActivity";
    public static final String CHATACTIVITYACTIVITY_PATH = "com.ioyouyun.chat.ChatActivity";

    private static Context mAppContext;

    private static Toast toast;
    public static boolean isOnlinePlatform = true;

    public static int loginType = 0; // 1: 免注册登录 2：帐号密码登录

    public static String CLIENT_ID = "1-20521-1b766ad17389c94e1dc1f2615714212a-andriod";
    public static String SECRET = "d5cf0a5812b4424f582ded05937e4387";
    public static String CLIENT_ID_TEST = "1-20140-201c24b1df50a4e3a8348274963ab0a6-andriod";
    public static String SECRET_TEST = "9d12b16f31926616582eabcf66a2a6ad";

    public static String TO_UID = ""; // 进入聊天界面赋值
    public static String uid; // 用户ID
    public static String nickname; // 昵称

    public static int mScreenWidth;
    public static int mScreenHeigth;
    public static float mDensity;

    public static final long MSG_TIME_SEPARATE = 300000L; // IM时间间隔5分钟

    public static final String FACETYPE = "facetype"; // 单个表情
    public static final String EMOJITYPE = "emojitype"; // 图文混排

    public static List<String> sendFileStatus = new ArrayList<>(); // 上传文件状态
    public static Map<String, Integer> downloadFileStatus = new ConcurrentHashMap<>(); //下载文件状态 1:未下载 2:已下载 3：正在下载

    public static void init(Context context) {
        mAppContext = context;

        mDensity = mAppContext.getResources().getDisplayMetrics().density;
        mScreenWidth = mAppContext.getResources().getDisplayMetrics().widthPixels;
        mScreenHeigth = mAppContext.getResources().getDisplayMetrics().heightPixels;
        if (mScreenWidth > mScreenHeigth) {
            mScreenWidth = mAppContext.getResources().getDisplayMetrics().heightPixels;
            mScreenHeigth = mAppContext.getResources().getDisplayMetrics().widthPixels;
        }
        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    public static Context getmAppContext() {
        return mAppContext;
    }

    /**
     * 获取Android Id
     *
     * @return
     */
    public static String generateOpenUDID(Activity activity) {
        // Try to get the ANDROID_ID
        String OpenUDID = Settings.Secure.getString(activity.getContentResolver(), Settings.Secure.ANDROID_ID);
        if (OpenUDID == null || OpenUDID.equals("9774d56d682e549c") | OpenUDID.length() < 15) {
            // if ANDROID_ID is null, or it's equals to the GalaxyTab generic
            // ANDROID_ID or bad, generates a new one
            final SecureRandom random = new SecureRandom();
            OpenUDID = new BigInteger(64, random).toString(16);
        }
        return OpenUDID;
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(float dpValue) {
        final float scale = mAppContext.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(float pxValue) {
        final float scale = mAppContext.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * Toast
     *
     * @param msg
     */
    public static void toastMessage(String msg) {
        if (toast == null)
            toast = Toast.makeText(YouyunApplication.application, msg, Toast.LENGTH_SHORT);
        else
            toast.setText(msg);
        toast.show();
    }

    /**
     * Toast
     *
     * @param resId
     */
    public static void toastMessage(int resId) {
        if (toast == null)
            toast = Toast.makeText(YouyunApplication.application, resId, Toast.LENGTH_SHORT);
        else
            toast.setText(resId);
        toast.show();
    }

    public static final String MSG_ID_PRE = UUID.randomUUID() + "";
    public static int msg_p = 0;

    public static String genLocalMsgId() {
        msg_p++;
        String msgId = MSG_ID_PRE + msg_p;
        return msgId;
    }

    /**
     * 检测Sdcard是否存在
     *
     * @return
     */
    public static boolean isExitsSdcard() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return true;
        else
            return false;
    }

    /**
     * 检查网络是否可用
     *
     * @param context
     * @return
     */
    public static boolean isNetWorkConnected(Context context) {
        if (context != null) {
            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null)
                return networkInfo.isAvailable();
        }
        return false;
    }

    /**
     * 获取版本号
     *
     * @return
     */
    public static String getVersion() {
        try {
            PackageManager manager = mAppContext.getPackageManager();
            PackageInfo info = manager.getPackageInfo(mAppContext.getPackageName(), 0);
            String version = info.versionName;
            return "v " + version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * 短链请求
     *
     * @param path
     * @param param
     * @param requestType
     * @param listener
     */
    public static void shortConnectRequest(String path, String param, RequestType requestType, final OnRequestListener listener) {
        Logger.i("path = " + path);
        try {
            WeimiInstance.getInstance().commonInterface(path, param, requestType, ServerType.weimiPlatform, null, null,
                    null, new HttpCallback() {
                        @Override
                        public void onResponse(String s) {
                            Logger.i("request success:" + s);
                            if (listener != null)
                                listener.onSuccess(s);
                        }

                        @Override
                        public void onResponseHistory(List list) {

                        }

                        @Override
                        public void onError(Exception e) {
                            Logger.i("request error:" + e.getMessage());
                            if (listener != null)
                                listener.onError(e.getMessage());
                        }
                    }, 120);
        } catch (WChatException e) {
            e.printStackTrace();
        }
    }

    public interface OnRequestListener {
        void onSuccess(String response);

        void onError(String error);
    }

    /**
     * Map -> "a=?"+"&b=?"
     *
     * @param params
     * @return
     */
    public static String combineParamers(Map<String, Object> params) {
        String param = "";
        if (params != null) {
            Iterator<Map.Entry<String, Object>> it = params.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Object> entry = it.next();
                param += entry.getKey() + "=" + entry.getValue() + "&";
            }
            if (param.length() >= 1)
                param = param.substring(0, param.length() - 1);
            Logger.i("param = " + param);
        }
        return param;
    }

    /**
     * 拼接表名称
     *
     * @param toId 对方Id或gid
     * @return
     */
    public static String jointTableName(String toId) {
        return uid + "_" + toId;
    }

    public static String getCurrentActivity() {
        ActivityManager manager = (ActivityManager) YouyunApplication.application.getSystemService(Activity.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> taskInfos = manager.getRunningTasks(1);
        ComponentName componentName = taskInfos.get(0).topActivity;
        String name = componentName.getClassName();
        Logger.d("Bill", "currentActivity:" + name);
        return name;
    }

    public static final int AUDIO_RECORD_MAX_SEC_DEFAULT = 60;//语音录制默认最长时间

    public static int calAudioViewWidth(int audioSecond) {
        int minWidth = mScreenWidth / 4;
        int maxWidth = mScreenWidth / 3 * 2;
        int width = minWidth;
        if (audioSecond <= 4) {
            width = minWidth;
        } else {
            width += (maxWidth - minWidth) / (AUDIO_RECORD_MAX_SEC_DEFAULT - 4) * (audioSecond - 4);
        }
        return width;
    }

    private static NotificationManager notificationManager;
    private static int rq = 0;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void sendNotification(Context context, ChatMsgEntity entity) {
        if(entity == null)
            return;

        String message = entity.getText();
        switch (entity.getMsgType()) {
            case ChatMsgEntity.CHAT_TYPE_SEND_TEXT:
            case ChatMsgEntity.CHAT_TYPE_RECV_TEXT:
                break;
            case ChatMsgEntity.CHAT_TYPE_SEND_AUDIO:
            case ChatMsgEntity.CHAT_TYPE_RECV_AUDIO:
                message = "[语音]";
                break;
            case ChatMsgEntity.CHAT_TYPE_SEND_IMAGE:
            case ChatMsgEntity.CHAT_TYPE_RECV_IMAGE:
                message = "[图片]";
                break;
            case ChatMsgEntity.CHAT_TYPE_SEND_EMOJI:
            case ChatMsgEntity.CHAT_TYPE_RECV_EMOJI:
                message = "[表情]";
                break;
            case ChatMsgEntity.CHAT_TYPE_SEND_FILE:
            case ChatMsgEntity.CHAT_TYPE_RECV_FILE:
                message = "[文件]";
                break;
            case ChatMsgEntity.CHAT_TYPE_SEND_POS:
            case ChatMsgEntity.CHAT_TYPE_RECV_POS:
                message = "[位置]";
                break;
        }

        PackageManager pm = context.getPackageManager();
        ApplicationInfo ai = context.getApplicationInfo();
        String title = (String)pm.getApplicationLabel(ai);
        BitmapDrawable bitmapDrawable = (BitmapDrawable)ai.loadIcon(pm);
        Bitmap appIcon = bitmapDrawable.getBitmap();
        Notification.Builder builder = new Notification.Builder(context);
        builder.setLargeIcon(appIcon);
        builder.setSmallIcon(context.getApplicationInfo().icon);
        builder.setTicker(message);
        builder.setContentTitle(title);
        builder.setContentText(message);
        builder.setAutoCancel(true);

        if(PushSharedUtil.getInstance().getVibration() && PushSharedUtil.getInstance().getSound()){
            builder.setDefaults(Notification.DEFAULT_ALL);
        }else if(PushSharedUtil.getInstance().getSound()){
            builder.setDefaults(Notification.DEFAULT_SOUND);
        }else if(PushSharedUtil.getInstance().getVibration()){
            builder.setDefaults(Notification.DEFAULT_VIBRATE);
        }

        int type = 0;
        if (ConvType.group == entity.getConvType())
            type = 1;
        else if (ConvType.room == entity.getConvType())
            type = 2;
        Intent intent = ChatActivity.startActivity(context, 0, entity.getOppositeId(), entity.getName(), type, 0, "notify");
        PendingIntent pendingIntent = PendingIntent.getActivity(context, rq++, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        if (notificationManager != null)
            notificationManager.notify(0, notification);
    }

    public static void clearNotify() {
        if (notificationManager != null)
            notificationManager.cancelAll();
    }

    private static class LoginTask extends AsyncTask<Void, Void, AuthResultData>{

        private Activity activity;
        private LoginListener loginListener;

        public LoginTask(Activity activity, LoginListener loginListener){
            this.activity = activity;
            this.loginListener = loginListener;
        }

        @Override
        protected AuthResultData doInBackground(Void... params) {
            AuthResultData authResultData = null;
            try {
                if(FunctionUtil.loginType == 1){
                    if (FunctionUtil.isOnlinePlatform) {
                        authResultData = WeimiInstance.getInstance().registerApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                FunctionUtil.CLIENT_ID,
                                FunctionUtil.SECRET,
                                60);
                    } else {
                        authResultData = WeimiInstance.getInstance().testRegisterApp(
                                YouyunApplication.application,
                                FunctionUtil.generateOpenUDID(activity),
                                FunctionUtil.CLIENT_ID_TEST,
                                FunctionUtil.SECRET_TEST,
                                60);
                    }
                } else if(FunctionUtil.loginType == 2){
                    String encodeStr = LoginSharedUtil.getInstance().getAccount();
                    if(TextUtils.isEmpty(encodeStr))
                        return null;
                    String decodeStr = new String(Base64.decode(encodeStr.getBytes(), Base64.DEFAULT));
                    Logger.d("解密帐号:" + decodeStr);
                    int num = decodeStr.indexOf("@") + decodeStr.substring(decodeStr.indexOf("@") + 1, decodeStr.length()).indexOf("@");
                    String email = decodeStr.substring(0, (num + 1));
                    String password = decodeStr.substring((num + 2), decodeStr.length());
                    authResultData = WeimiInstance.getInstance().youyunAuthUser(email, password, true, 60);
                }

            } catch (WChatException e) {
                e.printStackTrace();
            }

            return authResultData;
        }

        @Override
        protected void onPostExecute(AuthResultData authResultData) {
            if (authResultData != null && authResultData.success){
                UserInfoEntity entity = YouyunDbManager.getIntance().getUserInfo(WeimiInstance.getInstance().getUID());
                FunctionUtil.uid = entity.getId();
                FunctionUtil.nickname = entity.getNickname();

                initMediaSDK(activity);
                initReceive();
                PushUtils.startPush();
                WeimiInstance.getInstance().initBqmmSDK(activity.getApplicationContext());

                if(loginListener != null)
                    loginListener.loginResult(true);
            }else{
                Logger.d("自动登录失败");
                if(loginListener != null)
                    loginListener.loginResult(false);
            }
        }

    }

    private static Handler handler = new Handler();
    private static ExecutorService mSingleThreadExecutor;
    private static synchronized void executorsLogin(final Activity activity, final LoginListener loginListener){
        if(mSingleThreadExecutor == null)
            mSingleThreadExecutor = Executors.newSingleThreadExecutor();// 每次只执行一个线程任务的线程池
        mSingleThreadExecutor.execute(new Runnable() {
            @Override
            public void run() {
                AuthResultData authResultData = null;
                try {
                    if(FunctionUtil.loginType == 1){
                        if (FunctionUtil.isOnlinePlatform) {
                            authResultData = WeimiInstance.getInstance().registerApp(
                                    getmAppContext(),
                                    FunctionUtil.generateOpenUDID(activity),
                                    FunctionUtil.CLIENT_ID,
                                    FunctionUtil.SECRET,
                                    60);
                        } else {
                            authResultData = WeimiInstance.getInstance().testRegisterApp(
                                    getmAppContext(),
                                    FunctionUtil.generateOpenUDID(activity),
                                    FunctionUtil.CLIENT_ID_TEST,
                                    FunctionUtil.SECRET_TEST,
                                    60);
                        }
                    } else if(FunctionUtil.loginType == 2){
                        String encodeStr = LoginSharedUtil.getInstance().getAccount();
                        if(TextUtils.isEmpty(encodeStr))
                            return;
                        String decodeStr = new String(Base64.decode(encodeStr.getBytes(), Base64.DEFAULT));
                        Logger.d("解密帐号:" + decodeStr);
                        int num = decodeStr.indexOf("@") + decodeStr.substring(decodeStr.indexOf("@") + 1, decodeStr.length()).indexOf("@");
                        String email = decodeStr.substring(0, (num + 1));
                        String password = decodeStr.substring((num + 2), decodeStr.length());
                        authResultData = WeimiInstance.getInstance().youyunAuthUser(email, password, true, 60);
                    }

                    if(authResultData != null && authResultData.success){
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UserInfoEntity entity = YouyunDbManager.getIntance().getUserInfo(WeimiInstance.getInstance().getUID());
                                FunctionUtil.uid = entity.getId();
                                FunctionUtil.nickname = entity.getNickname();

                                initMediaSDK(activity);
                                initReceive();
                                PushUtils.startPush();
                                WeimiInstance.getInstance().initBqmmSDK(activity.getApplicationContext());

                                if(loginListener != null)
                                    loginListener.loginResult(true);
                            }
                        });
                    } else {
                        Logger.d("自动登录失败");
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                if(loginListener != null)
                                    loginListener.loginResult(false);
                            }
                        });
                    }

                } catch (WChatException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 自动登录
     * @param activity
     */
    public static void autoLogin(Activity activity, LoginListener loginListener) {
//        LoginTask loginTask = new LoginTask(activity, loginListener);
//        loginTask.execute();

        executorsLogin(activity, loginListener);
    }

    public interface LoginListener{
        void loginResult(boolean result);
    }

    private static void initMediaSDK(Activity activity) {
        boolean result = false;
        try {
            WMedia.getInstance().initWMediaSDK(activity.getApplicationContext(), true);
            result = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (!result) {
            FunctionUtil.toastMessage("初始化Media_SDK失败");
        }
    }

    private static void initReceive() {
        ReceiveRunnable receiveRunnable = new ReceiveRunnable(getmAppContext());
        Thread msgHandler = new Thread(receiveRunnable);
        msgHandler.start();
    }

    /**
     * 初始化SDK
     */
    public static void initSDK(){
        try {
            DeviceInfo deviceInfo = prepareForDeviceInfo();
            if (FunctionUtil.isOnlinePlatform) {
                WeimiInstance.getInstance().initSDK(mAppContext, deviceInfo, getVersion(), ServerType.weimiPlatform,
                        "cn", "youyun", FunctionUtil.CLIENT_ID);
            } else {
                WeimiInstance.getInstance().initSDK(mAppContext, deviceInfo, getVersion(), ServerType.weimiTestPlatform,
                        "cn", "youyun", FunctionUtil.CLIENT_ID_TEST);
            }
        } catch (WChatException e) {
            e.printStackTrace();
        }
    }

    private static DeviceInfo prepareForDeviceInfo() {
        DeviceInfo deviceInfo = new DeviceInfo();
        deviceInfo.clientVersionMajor = 7;
        deviceInfo.clientVersionMinor = 7;
        deviceInfo.deviceType = android.os.Build.MODEL;
        return deviceInfo;
    }

    public static String getDeviceID() {
        TelephonyManager telephonyManager = (TelephonyManager) mAppContext.getSystemService(Context.TELEPHONY_SERVICE);
        return (TextUtils.isEmpty(telephonyManager.getDeviceId())) ? "unknown android device" : telephonyManager.getDeviceId();
    }

    /**
     * byte(字节)根据长度转成kb(千字节)和mb(兆字节)
     *
     * @param bytes
     * @return
     */
    public static String bytes2kb(long bytes) {
        BigDecimal filesize = new BigDecimal(bytes);
        BigDecimal megabyte = new BigDecimal(1024 * 1024);
        float returnValue = filesize.divide(megabyte, 2, BigDecimal.ROUND_UP).floatValue();
        if (returnValue > 1)
            return (returnValue + "MB");
        BigDecimal kilobyte = new BigDecimal(1024);
        returnValue = filesize.divide(kilobyte, 2, BigDecimal.ROUND_UP).floatValue();
        return (returnValue + "KB");
    }

    /**
     * 打开文件
     * @param context
     * @param filePath
     */
    public static void openFile(Context context, String filePath) {
        File file = new File(filePath);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
        //设置intent的data和Type属性。
        intent.setDataAndType(Uri.fromFile(file), type);
        context.startActivity(Intent.createChooser(intent, "选择应用打开文件"));
    }

    private static String getMIMEType(File file) {
        String type = "/";
        String fName = file.getName();
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        for (int i = 0; i < MIME_MapTable.length; i++) {
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    /**
     * MIME类型
     */
    private static final String[][] MIME_MapTable = {
            //{后缀名， MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".ape", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };

    public final static boolean isValidEmail(CharSequence target) {
        return !TextUtils.isEmpty(target) && android.util.Patterns.EMAIL_ADDRESS.matcher(target).matches();
    }

}
