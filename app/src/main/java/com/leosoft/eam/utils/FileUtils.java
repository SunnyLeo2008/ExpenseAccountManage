package com.leosoft.eam.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Leo on 2017-06-01.
 */

public class FileUtils {

    public static void copyAssetDirToFiles(Context context, String dirname)
            throws IOException {
        File dir = new File(context.getFilesDir() + "/" + dirname);
        dir.mkdir();

        AssetManager assetManager = context.getAssets();
        String[] children = assetManager.list(dirname);
        for (String child : children) {
            child = dirname + '/' + child;
            String[] grandChildren = assetManager.list(child);
            if (0 == grandChildren.length)
                copyAssetFileToFiles(context, child);
            else
                copyAssetDirToFiles(context, child);
        }
    }

    public static void copyAssetFileToFiles(Context context, String filename)
            throws IOException {
        InputStream is = context.getAssets().open(filename);
        byte[] buffer = new byte[is.available()];
        is.read(buffer);
        is.close();

        File of = new File(context.getFilesDir() + "/" + filename);
        of.createNewFile();
        FileOutputStream os = new FileOutputStream(of);
        os.write(buffer);
        os.close();
    }

    public static void backupDataToFiles(String filePath, String fileName, List<String> mDataSet)
            throws IOException {

        File dir = new File(filePath);
        dir.mkdirs();

        File mFile = new File(filePath, fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(mFile, true));
        for (int i = 0; i < mDataSet.size(); i++) {
            bw.write(mDataSet.get(i));
            bw.newLine();
        }
        bw.flush();
        bw.close();

    }

    public static void backupDataStringToFiles(String filePath, String fileName, String mDataSet)
            throws IOException {

        File dir = new File(filePath);
        dir.mkdirs();

        File mFile = new File(filePath, fileName);
        BufferedWriter bw = new BufferedWriter(new FileWriter(mFile, true));

        bw.write(mDataSet);
        bw.newLine();

        bw.flush();
        bw.close();

    }

    public static ArrayList<String> restoreDataFromFiles(String filePath, String fileName)
            throws IOException {

        ArrayList<String> mDateSet = new ArrayList<>();

        File mFile = new File(filePath, fileName);
        BufferedReader br = new BufferedReader(new FileReader(mFile));

        String readLine = "";

        while ((readLine = br.readLine()) != null) {
            mDateSet.add(readLine);
        }
        br.close();

        return mDateSet;

    }

    public static String restoreDataStringFromFiles(String filePath, String fileName)
            throws IOException {

        File mFile = new File(filePath, fileName);
        BufferedReader br = new BufferedReader(new FileReader(mFile));

        String readLine = br.readLine();

        br.close();

        return readLine;

    }

    /**
     * 获取当前应用程序的包名
     * @param context 上下文对象
     * @return 返回包名
     */
    public static String getAppProcessName(Context context) {
        //当前应用pid
        int pid = android.os.Process.myPid();
        //任务管理类
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        //遍历所有应用
        List<ActivityManager.RunningAppProcessInfo> infos = manager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo info : infos) {
            if (info.pid == pid) {
                return info.processName;
            }
        }
        return "";
    }

    public static List<String> getCurrentChildDirectory(String filePath) {

        List<String> mDateSet = new ArrayList<>();

        File mFile = new File(filePath);
        File[] mFiles = mFile.listFiles();

        if (mFiles == null) {
            return null;
        }

        Arrays.sort(mFiles, new Comparator<File>() {

            public int compare(File f1, File f2) {
                long diff = f2.lastModified() - f1.lastModified();
                if (diff > 0) {
                    return 1;
                } else if (diff == 0) {
                    return 0;
                } else {
                    return -1;
                }
            }

            public boolean equals(Object obj) {
                return true;
            }

        });

        if (mFiles != null) {
            for (int i = 0; i < mFiles.length; i++) {
                if (mFiles[i].isDirectory()) {
                    mDateSet.add(mFiles[i].getName());
                }

                if (2 == i) {
                    break;
                }
            }
        }

        return mDateSet;
    }

}
