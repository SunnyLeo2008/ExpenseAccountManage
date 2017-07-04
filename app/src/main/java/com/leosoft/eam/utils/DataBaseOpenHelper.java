package com.leosoft.eam.utils;

import android.content.Context;

import com.readystatesoftware.sqliteasset.SQLiteAssetHelper;

/**
 * Created by Leo on 2017-05-26.
 */

public class DataBaseOpenHelper extends SQLiteAssetHelper {

    private static final String DATABASE_NAME = "MainDB.db";
    private static final int DATABASE_VERSION = 1;

    public DataBaseOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
