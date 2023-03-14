package com.example.androidthings.attendapp;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

public class HelperFunctions {

    public static File getFile(String fileName) {
        String mainFolderName = "/Attendance App";
        File mainFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mainFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + mainFolderName );
        } else {
            mainFile = new File(Environment.getExternalStorageDirectory().toString() + mainFolderName );
        }
        if (!mainFile.exists()) {
            mainFile.mkdirs();
        }
        if (mainFile.exists()) {
            mainFile =new File(mainFile.getAbsoluteFile(), fileName);
        }
        return mainFile;
    }

    public static File getFilePath() {
        String mainFolderName = "/Attendance App";
        File mainFile;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            mainFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString() + mainFolderName );
        } else {
            mainFile = new File(Environment.getExternalStorageDirectory().toString() + mainFolderName );
        }
        if (!mainFile.exists()) {
            mainFile.mkdirs();
        }

        return mainFile;
    }

    @SuppressLint("DefaultLocale")
    public static String humanReadableByteCountSI(long bytes) {
        if (-1000 < bytes && bytes < 1000) {
            return bytes + " B";
        }
        CharacterIterator ci = new StringCharacterIterator("kMGTPE");
        while (bytes <= -999_950 || bytes >= 999_950) {
            bytes /= 1000;
            ci.next();
        }
        return String.format("%.1f %cB", bytes / 1000.0, ci.current());
    }

    public static File renameFile(String oldName, String newName, Activity activity){
        File dir =getFilePath();
        if(dir.exists()){
            File from = new File(dir,oldName);
            File to = new File(dir,newName);

            if (to.exists()){
                Toast.makeText(activity, "File name already exists", Toast.LENGTH_SHORT).show();
            }else {
                if(from.exists()){
                    boolean b = from.renameTo(to);
                    if (b){
                        return to;
                    }else {
                        Toast.makeText(activity, "Unknown error", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
     return null;
    }

}
