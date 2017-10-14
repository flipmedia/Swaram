package com.example.insight.swaram;

import android.os.Environment;

import java.io.File;
import java.util.Iterator;
import java.util.List;

/**
 * Created by insight on 9/8/17.
 */

public class Utils {

    public static void removeAllNullFromStringArray(List lst)
    {
        for(Iterator<String> it = lst.iterator(); it.hasNext();){
            String elem = it.next();
            if("".equals(elem)){
                it.remove();
            }
        }
    }

    public static void creatDirectory(String foldername)
    {
        File direct = new File(Environment.getExternalStorageDirectory() + foldername);
        if(!isDirectoryExisted(direct)){
            direct.mkdirs();
        }
    }

    public static boolean isDirectoryExisted(File direct)
    {
        if (!direct.exists()) {
            return false;
        }else {
            return true;
        }
    }

    public static void deleteFileIfAlreadyExist(String foldername,String filename)
    {
        File direct = new File(Environment.getExternalStorageDirectory() + foldername);
        File file = new File(direct+filename);
        if(isFileExisted(file)){
            file.delete();
        }
    }

    public static boolean isFileExisted(File file)
    {
        if (!file.exists()) {
            return false;
        }else {
            return true;
        }
    }
}
