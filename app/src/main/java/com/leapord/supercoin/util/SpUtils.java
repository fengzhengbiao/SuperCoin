package com.leapord.supercoin.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.orhanobut.logger.Logger;


/**
 * 吐司工具类
 */

public class SpUtils {

    private static SharedPreferences sharedPreferences;

    public static synchronized void init(Context context) {
        if (sharedPreferences == null) {
            sharedPreferences = context.getSharedPreferences("coin_config", Context.MODE_PRIVATE);
        }
        Logger.d("SpUtils has been initialized");
    }

    public static boolean isInit() {
        return sharedPreferences != null;
    }

    public static void putInt(String key, int value) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        sharedPreferences.edit().putInt(key, value).commit();
    }

    public static int getInt(String key, int dValue) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.getInt(key, dValue);
    }

    public static void putLong(String key, long value) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        sharedPreferences.edit().putLong(key, value).commit();
    }

    public static long getLong(String key, Long dValue) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.getLong(key, dValue);
    }

    public static void putFloat(String key, float value) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        sharedPreferences.edit().putFloat(key, value).commit();
    }

    public static Float getFloat(String key, Float dValue) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.getFloat(key, dValue);
    }

    public static void putBoolean(String key, boolean value) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        sharedPreferences.edit().putBoolean(key, value).commit();
    }

    public static Boolean getBoolean(String key, boolean dValue) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.getBoolean(key, dValue);
    }

    public static void putString(String key, String value) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        sharedPreferences.edit().putString(key, value).commit();
    }

    public static String getString(String key, String dValue) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.getString(key, dValue);
    }

    public static void remove(String key) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        if (isExist(key)) {
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.remove(key);
            editor.commit();
        }
    }

    public static void clear() {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.commit();
    }

    public static boolean isExist(String key) {
        if (sharedPreferences == null) {
            throw new RuntimeException("You should call init() after use SpUtils");
        }
        return sharedPreferences.contains(key);
    }
}