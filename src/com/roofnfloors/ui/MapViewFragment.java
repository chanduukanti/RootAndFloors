package com.roofnfloors.ui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.mapslist.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMapLongClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.roofnfloors.tasks.ProjectDataFetcherTask;
import com.roofnfloors.tasks.ProjectDataFetcherTask.CallBack;
import com.roofnfloors.ui.RoofnFloorsActivity.Project;
import com.roofnfloors.util.Constants;

public class MapViewFragment extends Fragment implements OnMapClickListener,
        OnMapLongClickListener, OnMarkerClickListener, OnCameraChangeListener,
        CallBack {
    private AlertDialog mLocationDisabledMsgDialog;

    private LatLng mCurrentLatLng;
    private Marker mCurrentMarker;
    private GoogleMap mGoogleMap;
    private LocationManager mLocationManager;
    private boolean mIsGpsOn;
    private boolean mIsNetworKOn;
    private Context mContext = null;
    private boolean isCreateFail = false;
    private float mMapZoomLevel = Constants.DEFAULT_MAP_ZOOM_LEVEL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.mapview_fragment, container,
                false);

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        isCreateFail = !checkGooglePlayServices();
        ActionBar ab = getActivity().getActionBar();
        ab.setDisplayShowHomeEnabled(true);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle("Projects");
        if (!isCreateFail) {
            init();
        }
        if (mGoogleMap != null) {
            new ProjectDataFetcherTask(this.getActivity(), this, Boolean.TRUE)
                    .execute(Constants.PROJECT_LIST_URL);
        }
    }

    private boolean checkGooglePlayServices() {
        boolean isGooglePlayServicesAvailable = false;
        int errorCode = GooglePlayServicesUtil
                .isGooglePlayServicesAvailable(getActivity());
        switch (errorCode) {
        case ConnectionResult.SUCCESS:
            isGooglePlayServicesAvailable = true;
            break;
        case ConnectionResult.SERVICE_VERSION_UPDATE_REQUIRED:
            isGooglePlayServicesAvailable = false;
            break;
        }

        return isGooglePlayServicesAvailable;
    }

    public void onDestroy() {

        if (mGoogleMap != null) {
            mGoogleMap.setOnMapClickListener(null);
            mGoogleMap.setOnMapLongClickListener(null);
            mGoogleMap.setOnMarkerClickListener(null);
            mGoogleMap.setOnCameraChangeListener(null);
        }
        if (mGoogleMap != null) {
            mGoogleMap.clear();
            mGoogleMap = null;
        }
        System.gc();
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    private boolean checkSetting() {

        boolean isShowDialog = false;

        StringBuilder dialogString = new StringBuilder();

        ConnectivityManager connectivityManager = (ConnectivityManager) mContext
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager
                .getActiveNetworkInfo();

        mIsGpsOn = mLocationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        mIsNetworKOn = mLocationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        dialogString
                .append("string_not_all_location_sources_are_currently_enabled_desc");

        if (!(mIsGpsOn && mIsNetworKOn)) {
            dialogString.append("\n\n");
            dialogString
                    .append("string_turn_on_gps_and_enable_wireless_networks_in_location_settings");
            isShowDialog = true;
        }
        if (activeNetworkInfo == null || !activeNetworkInfo.isAvailable()) {
            dialogString.append("\n\n");
            dialogString.append("string_turn_on_wifi");
            isShowDialog = true;
        }

        if (isShowDialog) {
            return false;
        }
        return true;
    }

    private void showDialog() {
        AlertDialog.Builder ab = new AlertDialog.Builder(mContext);
        ab.setTitle("GPS diabled");
        ab.setMessage("GPS diabled");
        ab.setPositiveButton("OK", new OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Intent intent = new Intent(
                        Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });
        ab.setNegativeButton("CANCEL", new AlertDialog.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                arg0.dismiss();
            }
        });

        if (!getActivity().isFinishing()) {
            mLocationDisabledMsgDialog = ab.show();
        }
    }

    private boolean setUpView() {

        MapFragment mapFragment = (MapFragment) getActivity()
                .getFragmentManager().findFragmentById(R.id.mapview_select);
        if (mapFragment == null) {
            return false;
        }
        mGoogleMap = mapFragment.getMap();

        if (mGoogleMap == null) {
            return false;
        }
        System.gc();
        mGoogleMap.clear();

        UiSettings uiSettings = mGoogleMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(false);
        uiSettings.setMyLocationButtonEnabled(false);

        mGoogleMap.setOnMapClickListener(this);
        mGoogleMap.setOnMapLongClickListener(this);
        mGoogleMap.setOnMarkerClickListener(this);
        mGoogleMap.setOnCameraChangeListener(this);

        return true;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isCreateFail) {
            isCreateFail = !checkGooglePlayServices();
            if (isCreateFail) {
            }
        }
    }

    private void init() {

        mMapZoomLevel = 15;
        if (!setUpView()) {
            return;
        }

        // Check the GPS/Network option value in the Settings
        // application
        @SuppressWarnings("deprecation")
        boolean gpsEnabled = Settings.Secure.isLocationProviderEnabled(
                mContext.getContentResolver(), LocationManager.GPS_PROVIDER);
        @SuppressWarnings("deprecation")
        boolean networkEnabled = Settings.Secure
                .isLocationProviderEnabled(mContext.getContentResolver(),
                        LocationManager.NETWORK_PROVIDER);

        if (!gpsEnabled && !networkEnabled) {
            if (mLocationDisabledMsgDialog == null) {
                showDialog();
            } else {
                mLocationDisabledMsgDialog.show();
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition pos) {
        mCurrentLatLng = null;
        mCurrentLatLng = new LatLng(pos.target.latitude, pos.target.longitude);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        LatLng latlong = marker.getPosition();
        Project proj = RoofnFloorsActivity.myProjectsMap.get(latlong);
        Intent in = new Intent("roomandfloors.view.projectDetails");
        in.putExtra("projectId", proj.id);
        in.putExtra("Idurl", Constants.PROJECT_LIST_URL + proj.id);
        in.putExtra("ProjectName", proj.projectName);
        startActivity(in);
        return true;
    }

    @Override
    public void onMapLongClick(LatLng arg0) {

    }

    @Override
    public void onMapClick(LatLng arg0) {

    }

    private void setLatLngAndMarker(Project p) {
        mCurrentLatLng = new LatLng(p.latitude, p.longitude);
        if (mGoogleMap != null) {
            CameraUpdate cu = CameraUpdateFactory.newLatLngZoom(mCurrentLatLng,
                    mMapZoomLevel);
            if (cu != null)
                mGoogleMap.moveCamera(cu);
        } else {
            return;
        }
        MarkerOptions mo = new MarkerOptions().position(mCurrentLatLng).title(
                p.projectName);

        if (mo != null && mGoogleMap != null)
            mCurrentMarker = mGoogleMap.addMarker(mo);

        if (mCurrentMarker != null) {
            mCurrentMarker.setPosition(mCurrentLatLng);
        }

    }

    @Override
    public void onProjectListTaskCompleted(ArrayList<Project> projList) {
        if (projList != null) {
            for (int i = 0; i < projList.size(); i++) {
                setLatLngAndMarker(projList.get(i));
                // Moving CameraPosition to last clicked position
                if (mGoogleMap != null) {
                    mGoogleMap.moveCamera(CameraUpdateFactory
                            .newLatLng(new LatLng(projList.get(i).latitude,
                                    projList.get(i).longitude)));
                    // Setting the zoom level in the map on last position is
                    // clicked
                    mGoogleMap.animateCamera(CameraUpdateFactory
                            .zoomTo(mMapZoomLevel));
                }
            }
        }
    }

    @Override
    public void onProjectDetailsTaskCompleted(ProjectInfo pinfo) {

    }
}