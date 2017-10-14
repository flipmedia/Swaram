package com.example.insight.swaram;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by insight on 28/7/17.
 */

public class HomeFragment extends Fragment {
    public static HomeFragment newInstance() {
        HomeFragment fragment = new HomeFragment();
        return fragment;
    }

    ExpandableListAdapter listAdapter;
    ExpandableListView expListView;
    List<String> listDataHeader;
    HashMap<String, List<String>> listDataChild;
    View rootView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // preparing all list data
        File direct = new File(Environment.getExternalStorageDirectory() + MainActivity.SWRAM_ROOT_FOLDER);
        File file = new File(direct + MainActivity.SWARAM_LIST);

        if (Utils.isDirectoryExisted(direct)) {
            if (Utils.isFileExisted(file)) {
                prepareListData();
            } else {
                prepareListErrorData();
            }
        } else {
            prepareListErrorData();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.home_fragment, container, false);

        expListView = (ExpandableListView) rootView.findViewById(R.id.homelvExp);

        //Creating ExpandableListAdapter
        listAdapter = new ExpandableListAdapter(getActivity(), listDataHeader, listDataChild);

        // setting list adapter
        expListView.setAdapter(listAdapter);

        return  rootView;
    }

    /*
     * Preparing the list data
     */
    private void prepareListErrorData() {
        //preparing header
        listDataHeader = new ArrayList<String>();
        listDataHeader.add("Failed to load, Please check your internet connection");

        //preparing child
        listDataChild = new HashMap<String, List<String>>();
        List<String> errorChild = new ArrayList<String>();
        listDataChild.put(listDataHeader.get(0), errorChild); // Header, Child data
    }

    private void prepareListData(){
        listDataHeader = ReadUtils.getMagazinelistArray(MainActivity.SWRAM_ROOT_FOLDER, MainActivity.SWARAM_LIST_FOLDER, MainActivity.SWARAM_LIST);
        listDataChild = new HashMap<String, List<String>>();

        try{
            String lines[] = ReadUtils.readTextFileFromExternalStorage(MainActivity.SWRAM_ROOT_FOLDER,
                    MainActivity.SWARAM_LIST_FOLDER, MainActivity.SWARAM_LIST);

            //creating sub list
            for (int i = 0; i < lines.length; i++) {
                listDataChild.put(listDataHeader.get(i), ReadUtils.getMagazinelistArray(
                        MainActivity.SWRAM_ROOT_FOLDER, "/" + lines[i], "/" + lines[i] + ".txt")); // Header, Child data
            }
        }catch (Exception e){}
    }

}
