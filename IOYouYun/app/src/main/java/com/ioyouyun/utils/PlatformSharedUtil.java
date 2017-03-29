package com.ioyouyun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

public class PlatformSharedUtil {

    public static final String NAME = "youyun_platform_preference";
    public static final String KEY_IS_ONLINE = "key_is_online"; // 正式平台 or 测试平台
    public static final String KEY_IS_DEV = "key_is_dev"; // 开发者模式 or 正常模式
    private static PlatformSharedUtil sharedPreferenceUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferencesCompat compat;

    public static PlatformSharedUtil getInstance() {
        if (sharedPreferenceUtil == null) {
            synchronized (PlatformSharedUtil.class) {
                if (sharedPreferenceUtil == null) {
                    sharedPreferenceUtil = new PlatformSharedUtil();
                }
            }
        }
        return sharedPreferenceUtil;
    }

    private PlatformSharedUtil() {
        sharedPreferences = FunctionUtil.getmAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        compat = new SharedPreferencesCompat();
    }

    public void setPlatform(boolean isOnline) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_ONLINE, isOnline);
        compat.apply(editor);
    }

    public boolean getPlatform() {
        return sharedPreferences.getBoolean(KEY_IS_ONLINE, true);
    }

    public void setDeveloper(boolean isOnline) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_DEV, isOnline);
        compat.apply(editor);
    }

    public boolean getDeveloper() {
        return sharedPreferences.getBoolean(KEY_IS_DEV, false);
    }

    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     */
    private static class SharedPreferencesCompat {
        private final Method sApplyMethod = findApplyMethod();

        /**
         * 反射查找apply的方法
         *
         * @return
         */
        private Method findApplyMethod() {
            try {
                Class<SharedPreferences.Editor> clz = SharedPreferences.Editor.class;
                return clz.getMethod("apply");
            } catch (Exception e) {
            }

            return null;
        }

        /**
         * 如果找到则使用apply执行，否则使用commit
         *
         * @param editor
         */
        public void apply(SharedPreferences.Editor editor) {
            try {
                if (sApplyMethod != null) {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (Exception e) {
            }
            editor.commit();
        }
    }

}
