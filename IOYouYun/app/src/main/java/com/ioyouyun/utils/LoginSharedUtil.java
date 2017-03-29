package com.ioyouyun.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.lang.reflect.Method;

public class LoginSharedUtil {

    public static final String NAME = "youyun_login_preference";
    public static final String KEY_IS_LOGIN = "key_is_login";
    public static final String KEY_ACCOUNT = "key_account";
    private static LoginSharedUtil sharedPreferenceUtil;
    private SharedPreferences sharedPreferences;
    private SharedPreferencesCompat compat;

    public static LoginSharedUtil getInstance() {
        if (sharedPreferenceUtil == null) {
            synchronized (LoginSharedUtil.class) {
                if (sharedPreferenceUtil == null) {
                    sharedPreferenceUtil = new LoginSharedUtil();
                }
            }
        }
        return sharedPreferenceUtil;
    }

    private LoginSharedUtil() {
        sharedPreferences = FunctionUtil.getmAppContext().getSharedPreferences(NAME, Context.MODE_PRIVATE);
        compat = new SharedPreferencesCompat();
    }

    public void setLogin(boolean isLogin) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGIN, isLogin);
        compat.apply(editor);
    }

    public boolean getLogin() {
        return sharedPreferences.getBoolean(KEY_IS_LOGIN, false);
    }

    public void setAccount(String account) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ACCOUNT, account);
        compat.apply(editor);
    }

    public String getAccount() {
        return sharedPreferences.getString(KEY_ACCOUNT, "");
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
