package com.leosoft.eam.utils;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jeesoft.widget.pickerview.CharacterPickerView;

/**
 * Created by Leo on 2017-05-28.
 */

public class InitStepPickerData {

    private static final Map<String, List<String>> DATAs = new LinkedHashMap<>();
    public static List<String> options1Items = null;
    public static List<List<String>> options2Items = null;

    public static void init(Context context) {
        if (!DATAs.isEmpty()) {
            DATAs.clear();
        }

        DatabaseAccess databaseAccess = DatabaseAccess.getInstance(context);
        databaseAccess.open();
        Cursor cursorStep1 = databaseAccess.querySQL("SELECT STEPTITLE FROM T_STEPINFO WHERE STEPLEVEL=0 ORDER BY STEPTITLE");
        if (cursorStep1 != null && cursorStep1.moveToFirst()) {
            do {
                List<String> data = new ArrayList<>();

                Cursor cursorStep2 = databaseAccess.querySQL("SELECT STEPTITLE FROM T_STEPINFO WHERE STEPLEVEL=1 AND FATHERTITLE='"
                                   + cursorStep1.getString(0) + "' ORDER BY STEPTITLE");
                if (cursorStep2 != null && cursorStep2.moveToFirst()) {
                    do {
                        data.add(cursorStep2.getString(0));
                    } while (cursorStep2.moveToNext());
                } else {
                    data.add(cursorStep1.getString(0) + InfoText.TEXT_OTHER);
                }
                cursorStep2.close();

                DATAs.put(cursorStep1.getString(0), data);

            } while (cursorStep1.moveToNext());
        }
        cursorStep1.close();

        databaseAccess.close();
    }

    public static Map<String, List<String>> getAll(Context context) {
        init(context);
        return new HashMap<>(DATAs);
    }

    public static void setPickerData(Context context,CharacterPickerView view) {
        if (options1Items == null) {
            options1Items = new ArrayList<>();
            options2Items = new ArrayList<>();
        } else {
            options1Items.clear();
            options2Items.clear();
        }

        Map<String, List<String>> allStpes = getAll(context);

        for (Map.Entry<String, List<String>> entry : allStpes.entrySet()) {
            String key = entry.getKey();
            List<String> values = entry.getValue();

            options1Items.add(key);

            List<String> options2Items01 = new ArrayList<>();
            for (String value : values) {
                options2Items01.add(value);
            }
            options2Items.add(options2Items01);
        }

        //二级联动效果
        view.setPicker(options1Items, options2Items);
        view.setSelectOptions(0,0);

    }

}
