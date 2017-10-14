package com.example.insight.swaram;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;


public class MainActivity extends AppCompatActivity {
    // Dot(.com.example.insight.swaram & .Swaram) for hiding the folder
    public static final String SWRAM_ROOT_FOLDER = "/Android/data/.com.example.insight.swaram/.Swaram";
    public static final String SWARAM_URL = "http://insight.org.in/@downloads/swaram";
    public static final String SWARAM_LIST = "/SwaramList.txt";
    public static final String SWARAM_LIST_FOLDER = "";  // No special folder for "SwaramList.txt", it is in SWRAM_ROOT_FOLDER

    public static NiftyDialogBuilder builder;  //for Alert dialog

    private ProgressDialog customProgDialog;
    private ProgressDialog pDialog;
    // Progress dialog type (0 - for Horizontal progress bar)
    public static final int pDialog_bar_type = 0;

    public static Context context;
    public static boolean isSubListDownloadingStarted = false;

    public static String swaramListString[];
    public static int subListPresentCount = 0;

    public static boolean downloadingSwaramList = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        builder = NiftyDialogBuilder.getInstance(this);

        context = getBaseContext();
    }

    @Override
    protected void onResume()
    {
        super.onResume();

        File direct = new File(Environment.getExternalStorageDirectory() + SWRAM_ROOT_FOLDER);
        File file = new File(direct+SWARAM_LIST);

        if(Utils.isDirectoryExisted(direct)){
            if(Utils.isFileExisted(file)){
                loadHomeFragmentAsDefault(); //Displaying the Home fragment
            }else{
                isSubListDownloadingStarted = false;  // clearing this for extracting SwaramList
                if(checkConectivity("Swaram needs internet connection for first time to work. Do you want to turn it ON")){
                    customProgDialog = CustomLoadingProgressBar.creator(MainActivity.this);
                    customProgDialog.show();
                    //Downloading swaram list
                    downloadingSwaramList = true;
                    AsyncDownloadInBackGround downloadAsync = new AsyncDownloadInBackGround(SWRAM_ROOT_FOLDER, SWARAM_LIST_FOLDER, SWARAM_LIST);
                    downloadAsync.execute();
                }
            }
        }else{
            isSubListDownloadingStarted = false;  // clearing this for extracting SwaramList
            if(checkConectivity("Swaram needs internet connection for first time to work. Do you want to turn it ON")){
                customProgDialog = CustomLoadingProgressBar.creator(MainActivity.this);
                customProgDialog.show();
                //Downloading swaram list
                downloadingSwaramList = true;
                AsyncDownloadInBackGround downloadAsync = new AsyncDownloadInBackGround(SWRAM_ROOT_FOLDER,SWARAM_LIST_FOLDER,SWARAM_LIST);
                downloadAsync.execute();
            }
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;

            switch (item.getItemId()) {
                case R.id.navigation_home:
                    selectedFragment = HomeFragment.newInstance();
                    break;
                case R.id.navigation_library:
                    selectedFragment = LibraryFragment.newInstance();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = NotificationFragment.newInstance();
                    break;
            }
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, selectedFragment);
            transaction.commit();
            return true;
        }
    };

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            loadHomeFragmentAsDefault();
            Toast.makeText(this, "App launching screen ", Toast.LENGTH_SHORT).show();
            return true;
        }else {
            return super.onKeyLongPress(keyCode,event);
        }
    }

    public void loadHomeFragmentAsDefault(){
        try{
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.content, HomeFragment.newInstance());
            transaction.commit();
        }catch (Exception e){}
    }


    public void showAnimateAlertMsg(String str){
        builder.withTitle("Alert")
                .isCancelable(false)
                .withMessage(str)
                .withDialogColor("#1c90ec")
                .withButton1Text("Settings")
                .withButton2Text("Quit")
                .withDuration(700)
                .withEffect(Effectstype.Fall)
                .setButton1Click(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        startActivity(new Intent(Settings.ACTION_SETTINGS));
                        builder.cancel(); // it is for cancelling is network enabled
                    }
                })
                .setButton2Click(new View.OnClickListener(){
                    @Override
                    public void onClick(View v){
                        finish();
                    }
                }).show();
    }


    public boolean checkConectivity(String str){
        ConnectivityManager connec = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if ( connec.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED ) {
            return true;
        }else {
            showAnimateAlertMsg(str);
            return false;
        }
    }

    public void extractSwaramList(){
        isSubListDownloadingStarted = true;
        subListPresentCount = 0;

        //geting text from list
        swaramListString =ReadUtils.readTextFileFromExternalStorage(SWRAM_ROOT_FOLDER, SWARAM_LIST_FOLDER, SWARAM_LIST);
        downloadMagazinSubList(subListPresentCount);
    }


    public void downloadMagazinSubList(int i){
            AsyncDownloadInBackGround downloadAsync = new AsyncDownloadInBackGround(SWRAM_ROOT_FOLDER,
                    "/"+swaramListString[i], "/"+swaramListString[i]+".txt");
            downloadAsync.execute();
    }

    public void downloadSubListContent(String subfolder,String filename){
        //downloadingSwaramList = true;//false;
        downloadingSwaramList = false;

        File direct = new File(Environment.getExternalStorageDirectory() + SWRAM_ROOT_FOLDER + "/" + subfolder);
        File file = new File(direct+"/"+filename);
        if(Utils.isDirectoryExisted(direct)){
            if (Utils.isFileExisted(file)){
                Toast.makeText(context, "Alredy downloaded", Toast.LENGTH_SHORT).show();
            }else {
                    AsyncDownloadInBackGround downloadAsync = new AsyncDownloadInBackGround(SWRAM_ROOT_FOLDER,
                            "/" + subfolder, "/" + filename + ".ogg");
                    downloadAsync.execute();
            }
        }else{
                AsyncDownloadInBackGround downloadAsync = new AsyncDownloadInBackGround(SWRAM_ROOT_FOLDER,
                        "/" + subfolder, "/" + filename + ".ogg");
                downloadAsync.execute();
        }
    }


    /** Showing Dialog **/
    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
            case pDialog_bar_type: // we set this to 0
                //pDialog = new ProgressDialog(this);
                pDialog = new ProgressDialog(MainActivity.this);
                pDialog.setMessage("Please wait. Downloading ...");
                pDialog.setIndeterminate(false);
                pDialog.setMax(100);
                pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                pDialog.setCancelable(false);
                pDialog.show();
                return pDialog;
            default:
                return null;
        }
    }


    //Inner class
    public class AsyncDownloadInBackGround extends AsyncTask<Void, String, Void>
    {
        String rootfolder, subfolder, filename;


        public  AsyncDownloadInBackGround(String rootfolder,String subfolder,String filename){
            this.rootfolder = rootfolder;
            this.subfolder = subfolder;
            this.filename = filename;

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            if(downloadingSwaramList == false){
                showDialog(pDialog_bar_type);
            }
        }

        @Override
        protected void onPostExecute(Void s) {
            super.onPostExecute(s);

            if(downloadingSwaramList == true){
                subListPresentCount = subListPresentCount + 1;

                if(!isSubListDownloadingStarted) {
                    extractSwaramList();
                }else if(subListPresentCount<swaramListString.length){
                    downloadMagazinSubList(subListPresentCount);
                }else{
                    customProgDialog.dismiss();
                    downloadingSwaramList = false;
                    loadHomeFragmentAsDefault(); //Displaying the Home fragment
                }
            }else {
                dismissDialog(pDialog_bar_type);
            }
        }


        @Override
        protected void onProgressUpdate(String... values)
        {
            super.onProgressUpdate(values);

            if(downloadingSwaramList == false){
                pDialog.setProgress(Integer.parseInt(values[0]));
            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            Utils.creatDirectory(rootfolder+subfolder);
            Utils.deleteFileIfAlreadyExist(rootfolder+subfolder,filename);

            int count;
            try {
                URL url = new URL(SWARAM_URL+subfolder+filename);
                URLConnection conection = url.openConnection();
                conection.connect();
                // getting file length
                int lenghtOfFile = conection.getContentLength();

                // input stream to read file - with 8k buffer
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                byte data[] = new byte[1024];
                long total = 0;

                ByteArrayOutputStream output = new ByteArrayOutputStream();

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called automatically
                    if(downloadingSwaramList == false){
                        publishProgress(""+(int)((total*100)/lenghtOfFile));
                    }

                    // writing data to output stream
                    output.write(data, 0, count);
                }

                String keystring = EncryptOrDecrypt.key2StringKey();

                // using Secure preference for saving the key in encripted form
                // for Initialisation
                SecurePreferences preferences = new SecurePreferences(context, "my-preferences", "PreferencesLock", true);
                // Putting the value (all puts are automatically committed)
                preferences.put(filename.replace("/",""), keystring);

                EncryptOrDecrypt.saveFile(rootfolder+subfolder,filename, keystring, output.toByteArray());

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();

            } catch (Exception e) {
                Log.e("Error: ", e.getMessage());
            }

            return null;
        }
    }

}
