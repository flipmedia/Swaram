package com.example.insight.swaram;

import android.content.Context;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by insight on 9/8/17.
 */

public class ReadUtils {

    public static byte[] getDecryptedData(String rootfolder, String subfolder, String filename) {
        // for Initialisation
        SecurePreferences preferences = new SecurePreferences(MainActivity.context, "my-preferences", "PreferencesLock", true);

        // Getting the value
        String keystring = preferences.getString(filename.replace("/",""));

        return EncryptOrDecrypt.decryptFile(rootfolder, subfolder,keystring, filename);
    }

    public static String[] readTextFileFromExternalStorage(String rootfolder,String subfolder,String filename) {
        byte[] decryptedData = ReadUtils.getDecryptedData(rootfolder, subfolder, filename);

        //splitting with \r\n and also removing empty lines
        return new String(decryptedData).split("[\\r\\n]+");
    }


    public static List getMagazinelistArray(String rootfolder, String subfolder, String filename) {
        List<String> list = new ArrayList<String>();
        list.clear();

        String lines[] =readTextFileFromExternalStorage(rootfolder, subfolder, filename);

        for(int i=0; i<lines.length; i++){
            list.add(lines[i]);
        }

        Utils.removeAllNullFromStringArray(list);
        return list;
    }
}
