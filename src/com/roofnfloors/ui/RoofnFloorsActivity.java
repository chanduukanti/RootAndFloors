package com.roofnfloors.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;

import com.example.mapslist.R;
import com.google.android.gms.maps.model.LatLng;
import com.roofnfloors.adapter.TabsPagerAdapter;
import com.roofnfloors.util.Constants;

@SuppressWarnings("deprecation")
public class RoofnFloorsActivity extends FragmentActivity implements
        ActionBar.TabListener {
    private ViewPager viewPager;
    private TabsPagerAdapter mAdapter;
    private ActionBar actionBar;
    // Tab titles

    public static HashMap<LatLng, Project> myProjectsMap = new HashMap<LatLng, Project>();
    boolean isNetworkAvailable = false;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.roof_floors_main);
        // Initilization
        viewPager = (ViewPager) findViewById(R.id.pager);
        actionBar = getActionBar();
        mAdapter = new TabsPagerAdapter(getSupportFragmentManager());
        mContext = this;
        viewPager.setAdapter(mAdapter);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Adding Tabs
        for (String tab_name : Constants.TABS) {
            actionBar.addTab(actionBar.newTab().setText(tab_name)
                    .setTabListener(this));
        }

    }

    public static void makeMyprojects(ArrayList<Project> projectList) {
        if (myProjectsMap.size() == 0) {
            if (projectList != null) {
                for (int i = 0; i < projectList.size(); i++) {
                    LatLng latlang = new LatLng(projectList.get(i).latitude,
                            projectList.get(i).longitude);
                    myProjectsMap.put(latlang, projectList.get(i));
                }
            }
        }
    }

    @Override
    public void onTabReselected(Tab tab, FragmentTransaction ft) {
    }

    @Override
    public void onTabSelected(Tab tab, FragmentTransaction ft) {
        // on tab selected
        // show respected fragment view
        viewPager.setCurrentItem(tab.getPosition());
    }

    @Override
    public void onTabUnselected(Tab tab, FragmentTransaction ft) {
    }

    public static class Project {

        public Project(String idd, String pName, double latt, double longt) {
            id = idd;
            projectName = pName;
            latitude = latt;
            longitude = longt;
        }

        public String id;
        public String projectName;
        public Double latitude;
        public Double longitude;
    }
}
