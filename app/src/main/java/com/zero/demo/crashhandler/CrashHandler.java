package com.zero.demo.crashhandler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by zengzhuo on 2017/3/17.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = "CrashHandler";

    private static final String PATH = Environment.getExternalStorageDirectory().getPath() + "/CrashLog/";
    private static final String FILE_NAME = "crash.log";

    private static CrashHandler sInstance = new CrashHandler();

    private Thread.UncaughtExceptionHandler mDefaultUncaughtExceptionHandler;

    private Context mContext;

    private CrashHandler() {

    }

    public static CrashHandler getInstance() {
        return sInstance;
    }

    public void init(Context context) {
        mDefaultUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
        mContext = context.getApplicationContext();
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        try {
            saveExceptionToSdCard(e);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        e.printStackTrace();
        if (mDefaultUncaughtExceptionHandler != null) {
            mDefaultUncaughtExceptionHandler.uncaughtException(t, e);
        } else {
            Process.killProcess(Process.myPid());
        }
    }

    private void saveExceptionToSdCard(Throwable e) throws IOException {
        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            Log.d(TAG, "sd卡空间不足");
            return;
        }

        Log.d(TAG, PATH);
        File dir = new File(PATH);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        long current = System.currentTimeMillis();
        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(current));
        File file = new File(PATH + FILE_NAME);

        try {
            PrintWriter pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            pw.println(time);
            dumpPhoneInfo(pw);
            pw.println();
            e.printStackTrace(pw);
            pw.close();
        } catch (Exception ex) {
            Log.d(TAG, "Dump crash info failed!");
        }
    }

    private void dumpPhoneInfo(PrintWriter pw) throws PackageManager.NameNotFoundException {
        PackageManager pm = mContext.getPackageManager();
        PackageInfo pi = pm.getPackageInfo(mContext.getPackageName(), PackageManager.GET_ACTIVITIES);
        pw.print("App Version: ");
        pw.print(pi.versionName);
        pw.print("_");
        pw.println(pi.versionCode);

        pw.print("OS Version: ");
        pw.print(Build.VERSION.RELEASE);
        pw.print("_");
        pw.println(Build.VERSION.SDK_INT);

        pw.print("Vendor: ");
        pw.println(Build.MANUFACTURER);

        pw.print("Model: ");
        pw.println(Build.MODEL);

        pw.print("CPU ABI: ");
        pw.println(Build.CPU_ABI);

    }
}
