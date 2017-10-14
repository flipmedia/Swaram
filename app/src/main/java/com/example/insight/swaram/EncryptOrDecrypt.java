package com.example.insight.swaram;

/**
 * Created by insight on 6/8/17.
 */


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import android.os.Environment;
import android.util.Base64;

public class EncryptOrDecrypt {

    private static String algorithm = "AES";


    public static String key2StringKey(){
        SecretKey yourKey = null;
        String stringKey = "";

        try{
            char[] password = {'s','w','a','r','a','m','l','o','c','k'};
            yourKey = generateKey(password, generateSalt().toString().getBytes());
        }catch (Exception e){

        }

        if (yourKey != null) {stringKey = Base64.encodeToString(yourKey.getEncoded(), Base64.NO_WRAP);}

        return stringKey;
    }

    public static SecretKey StringKey2key(String stringKey){
        byte[] encodedKey = Base64.decode(stringKey, Base64.NO_WRAP);
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }


    public static SecretKey generateKey(char[] passphraseOrPin, byte[] salt)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        // Number of PBKDF2 hardening rounds to use. Larger values increase
        // computation time. You should select a value that causes computation
        // to take >100ms.
        final int iterations = 1000;

        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecretKeyFactory secretKeyFactory = SecretKeyFactory
                .getInstance("PBKDF2WithHmacSHA1");
        KeySpec keySpec = new PBEKeySpec(passphraseOrPin, salt, iterations,
                outputKeyLength);
        return secretKeyFactory.generateSecret(keySpec);
    }



    public static SecretKey generateSalt() throws NoSuchAlgorithmException {
        // Generate a 256-bit key
        final int outputKeyLength = 256;

        SecureRandom secureRandom = new SecureRandom();
        // Do *not* seed secureRandom! Automatically seeded from system entropy.
        KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        keyGenerator.init(outputKeyLength, secureRandom);
        SecretKey key = keyGenerator.generateKey();
        return key;
    }




    public static byte[] encryptFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);

        byte[] encrypted = cipher.doFinal(fileData);
        return encrypted;
    }


    public static byte[] decryptFile(SecretKey yourKey, byte[] fileData)
            throws Exception {
        byte[] data = yourKey.getEncoded();
        SecretKeySpec skeySpec = new SecretKeySpec(data, 0, data.length,
                algorithm);
        Cipher cipher = Cipher.getInstance(algorithm);
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);

        byte[] decrypted = cipher.doFinal(fileData);
        return decrypted;
    }


    public static void saveFile(String foldername, String filename, String keystring, byte[] stringToSave) {
        try {
            File direct = new File(Environment.getExternalStorageDirectory() + foldername);
            File file = new File(direct+filename);

            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(file));

            byte[] filesBytes = encryptFile(StringKey2key(keystring), stringToSave);
            bos.write(filesBytes);
            bos.flush();
            bos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static byte[] decryptFile(String rootfolder,String subfolder,String keystring, String name) {
        byte[] decryptedData = null;
        try {
            decryptedData = decryptFile(StringKey2key(keystring), readFile(rootfolder,subfolder,name));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return decryptedData;
    }

    public static byte[] readFile(String rootfolder,String subfolder,String fileName) {
        byte[] contents = null;

        File direct = new File(Environment.getExternalStorageDirectory() + rootfolder+subfolder);
        File file = new File(direct+fileName);
        int size = (int) file.length();
        contents = new byte[size];
        try {
            BufferedInputStream buf = new BufferedInputStream(
                    new FileInputStream(file));
            try {
                buf.read(contents);
                buf.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return contents;
    }


//    private void playMp3(byte[] mp3SoundByteArray) {
//
//        try {
//            // create temp file that will hold byte array
//            File tempMp3 = File.createTempFile("kurchina", "mp3", getCacheDir());
//            tempMp3.deleteOnExit();
//            FileOutputStream fos = new FileOutputStream(tempMp3);
//            fos.write(mp3SoundByteArray);
//            fos.close();
//            // Tried reusing instance of media player
//            // but that resulted in system crashes...
//            MediaPlayer mediaPlayer = new MediaPlayer();
//            FileInputStream fis = new FileInputStream(tempMp3);
//            mediaPlayer.setDataSource(fis.getFD());
//            mediaPlayer.prepare();
//            mediaPlayer.start();
//        } catch (IOException ex) {
//            ex.printStackTrace();
//
//        }
//
//    }
}