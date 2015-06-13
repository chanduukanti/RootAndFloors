package com.roofnfloors.tasks;

import java.util.ArrayList;

import android.content.Context;
import android.os.AsyncTask;

import com.roofnfloors.db.DataBaseHandler;
import com.roofnfloors.net.ServerDataFetcher;
import com.roofnfloors.parser.JSONParser;
import com.roofnfloors.ui.ProjectInfo;
import com.roofnfloors.ui.RoofnFloorsActivity;
import com.roofnfloors.ui.RoofnFloorsActivity.Project;

public class ProjectDataFetcherTask extends AsyncTask<String, String, Void> {

    private Context mContext;
    private CallBack mCallBack;
    private boolean mIsList;
    private ArrayList<Project> plist;
    private ProjectInfo pinfo;

    public ProjectDataFetcherTask(Context context, CallBack callBack,
            Boolean isList) {
        super();
        this.mContext = context;
        this.mCallBack = callBack;
        this.mIsList = isList;
    }

    public interface CallBack {
        public void onProjectListTaskCompleted(ArrayList<Project> plist);

        public void onProjectDetailsTaskCompleted(ProjectInfo pinfo);
    }

    protected Void doInBackground(String... urls) {
        DataBaseHandler db = new DataBaseHandler(mContext);
        plist = (ArrayList<Project>) db.getAllProjects(mContext);
        if (plist != null && plist.size() != 0) {
            RoofnFloorsActivity.makeMyprojects(plist);
        } else {
            String response = ServerDataFetcher.getServerResult(urls[0]);
            plist = JSONParser.parseProjectList(response, mContext);
        }

        return null;
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
        if (mIsList) {
            mCallBack.onProjectListTaskCompleted(plist);
        } else {
            mCallBack.onProjectDetailsTaskCompleted(pinfo);
        }

    }
}
