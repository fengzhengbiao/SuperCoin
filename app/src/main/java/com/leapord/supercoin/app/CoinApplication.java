package com.leapord.supercoin.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.leapord.supercoin.entity.dao.DaoMaster;
import com.leapord.supercoin.entity.dao.DaoSession;
import com.leapord.supercoin.util.SpUtils;
import com.leapord.supercoin.util.ToastUtis;

/**
 * @author Biao
 * @date 2018/1/19
 * @description
 * @email fengzb0216@sina.com
 */

public class CoinApplication extends Application {

    public static CoinApplication INSTANCE;
    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        INSTANCE = this;
        ToastUtis.init(this);
        SpUtils.init(this);
        setDatabase();
    }


    /**
     * 设置greenDao
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "coin-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public SQLiteDatabase getDb() {
        return db;
    }


    private long lastOptimalTime;       //最后交易时间

    public long getLastOptimalTime() {
        return lastOptimalTime;
    }

    public void setLastOptimalTime(long lastOptimalTime) {
        this.lastOptimalTime = lastOptimalTime;
    }
}
