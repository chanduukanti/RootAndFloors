package com.roofnfloors.parser;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;

import com.roofnfloors.db.DataBaseHandler;
import com.roofnfloors.db.SaveProjectInfoToDB;
import com.roofnfloors.ui.RoofnFloorsActivity;
import com.roofnfloors.ui.RoofnFloorsActivity.Project;

public class JSONParser {
    private static final String TAG = JSONParser.class.getCanonicalName();

    public static ArrayList<Project> parseProjectList(String string,
            Context context) {
        ArrayList<Project> pList = null;
        try {
            JSONArray jarr = new JSONArray(string);
            pList = new ArrayList<Project>();
            for (int i = 0; i < jarr.length(); i++) {
                JSONObject c = jarr.getJSONObject(i);
                String id = c.getString("id");
                String pname = c.getString("projectName");
                Double lat = c.getDouble("lat");
                Double longg = c.getDouble("lon");
                pList.add(new Project(id, pname, lat, longg));
            }
            if (pList != null && pList.size() != 0) {
                DataBaseHandler db = new DataBaseHandler(context);
                SaveProjectInfoToDB.saveProjectList(pList, db, context);
            }
        } catch (JSONException e) {
            Log.v(TAG, "JSON Exception error occured");
            e.printStackTrace();
        }
        RoofnFloorsActivity.makeMyprojects(pList);
        return pList;
    }
}
