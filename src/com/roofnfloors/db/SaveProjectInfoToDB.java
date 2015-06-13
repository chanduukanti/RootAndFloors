package com.roofnfloors.db;

import java.util.ArrayList;

import android.content.Context;

import com.roofnfloors.ui.ProjectInfo;
import com.roofnfloors.ui.RoofnFloorsActivity.Project;

public class SaveProjectInfoToDB {

    DataBaseHandler db;

    public static void saveProjectInfo(final ProjectInfo p,
            final DataBaseHandler db, final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                db.addProjectDetails(p, context);
            }
        }).start();
    }

    public static void saveProjectList(final ArrayList<Project> plist,
            final DataBaseHandler db, final Context context) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                for (Project project : plist) {
                    db.addProj(project, context);
                }

            }
        }).start();
    }
}
