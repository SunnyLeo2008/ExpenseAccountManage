package com.leosoft.eam.utils;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Leo on 2017-05-27.
 */

public class DatabaseAccess {

    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;

    /**
     * Private constructor to aboid object creation from outside classes.      *      * @param context
     */
    private DatabaseAccess(Context context) {
        this.openHelper = new DataBaseOpenHelper(context);
    }

    /**
     * Return a singleton instance of DatabaseAccess.      *      * @param context the Context      * @return the instance of DabaseAccess
     */
    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    /**
     * Open the database connection.
     */
    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    /**
     * Close the database connection.
     */
    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public void beginTransaction() {

        if ((database != null) && (!database.inTransaction())) {
            this.database.beginTransaction();
        }
    }

    public void setTransactionSuccessful() {

        if ((database != null) && (database.inTransaction())) {
            this.database.setTransactionSuccessful();
        }
    }

    public void endTransaction() {

        if ((database != null) && (database.inTransaction())) {
            this.database.endTransaction();
        }
    }

    public String ddlSQL(String sqlStr,boolean flagInTransaction) {

        String Result = "";

        //插入数据库中
        try {
            database.execSQL(sqlStr);
        } catch(Exception e) {
            Result = e.getMessage();
            e.printStackTrace();
        } finally {
            if (! flagInTransaction) {
                this.close();
            }
            return Result;
        }
    }

    public Cursor querySQL(String sqlStr) {

        //获取Cursor
        Cursor cursor = database.rawQuery(sqlStr,null);
        return cursor;
    }
}
