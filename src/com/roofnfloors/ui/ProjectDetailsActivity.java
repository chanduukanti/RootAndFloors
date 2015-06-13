package com.roofnfloors.ui;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.mapslist.R;
import com.roofnfloors.adapter.ImagePagerAdapter;
import com.roofnfloors.adapter.ImagePagerAdapter.HolderView;
import com.roofnfloors.db.DataBaseHandler;
import com.roofnfloors.db.SaveProjectInfoToDB;
import com.roofnfloors.loader.ImageLoader;
import com.roofnfloors.ui.ProjectInfo.Documents;
import com.roofnfloors.util.Utility;

public class ProjectDetailsActivity extends Activity {

    private ViewPager viewPager;
    private PagerAdapter adapter;
    private String detailUrl;
    private String projectName;
    private TextView projectNametext;
    private TextView addressDetails;
    private TextView propertytypevalue;
    private TextView cityValue;
    private TextView blocksValue;
    private TextView availableunitsvalue;
    private TextView statusValue;
    private TextView possesionDate;
    private TextView priceperunitvalue;
    private TextView coveredAreaValue;
    private TextView descriptiontext;
    private TextView builderdescriptiontext;
    private TextView buildernametext;
    private Button builderurlBtn;// Btn
    private ActionBar actionbar;
    public String json;
    private Drawable[] imageArray;
    private Handler mHandler = new Handler();

    public Drawable[] getImageArray() {
        return imageArray;
    }

    public Drawable getImageAt(int pos) {
        if (getImageArray() != null) {
            return getImageArray()[pos];
        } else {
            return null;
        }
    }

    public void setImageAtPos(int pos, Drawable d) {
        if (getImageArray() != null) {
            getImageArray()[pos] = d;
        }
    }

    public void setImageArray(Drawable[] imageArray) {
        this.imageArray = imageArray;
    }

    private Drawable[] defaultArray;
    private String[] imageLinks;
    private ImageLoader mImageLoader;
    private GetProjectInfoTask mProjectInfoTask;
    private TextView amenitiesText;
    private String projectId;
    private static final String TAG = "ProjectDetails";
    private ProjectInfo projectInfofromDB = null;

    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "OnCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.project_item_details);
        Intent detailIntent = getIntent();
        detailUrl = detailIntent.getStringExtra("Idurl");
        projectName = detailIntent.getStringExtra("ProjectName");
        projectId = detailIntent.getStringExtra("projectId");
        actionbar = getActionBar();
        actionbar.setTitle(projectName);
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setDisplayShowHomeEnabled(true);

        inItViews();
        DataBaseHandler db = new DataBaseHandler(this);
        viewPager.setOnPageChangeListener(new ImagePagerHandler());
        projectInfofromDB = db.getProjectDetails(projectId,
                getApplicationContext());
        if (projectInfofromDB != null) {
            handleImageAdapter(projectInfofromDB);
            populateDataOnUI(projectInfofromDB);
        } else {
            mProjectInfoTask = new GetProjectInfoTask();
            mProjectInfoTask.execute(detailUrl);
            defaultArray = new Drawable[1];
            defaultArray[0] = getResources().getDrawable(
                    R.drawable.roofnfloor_default);
            setImageAdapter(defaultArray);

        }
    }

    private void setImageAdapter(Drawable[] defaultArray2) {

        adapter = new ImagePagerAdapter(ProjectDetailsActivity.this,
                defaultArray);
        if (viewPager != null)
            viewPager.setAdapter(adapter);
    }

    private void inItViews() {
        viewPager = (ViewPager) findViewById(R.id.image_pager);
        projectNametext = (TextView) findViewById(R.id.project_name1);
        addressDetails = (TextView) findViewById(R.id.add_details);
        propertytypevalue = (TextView) findViewById(R.id.property_type_value);
        cityValue = (TextView) findViewById(R.id.city_value);
        availableunitsvalue = (TextView) findViewById(R.id.availableunitsvalue);
        blocksValue = (TextView) findViewById(R.id.blocks_value);
        statusValue = (TextView) findViewById(R.id.statusValue);
        possesionDate = (TextView) findViewById(R.id.possesionDate);
        priceperunitvalue = (TextView) findViewById(R.id.priceperunitvalue);
        coveredAreaValue = (TextView) findViewById(R.id.coveredAreaValue);

        amenitiesText = (TextView) findViewById(R.id.amenitiesText);

        descriptiontext = (TextView) findViewById(R.id.descriptiontext);
        builderdescriptiontext = (TextView) findViewById(R.id.builderdescriptiontext);
        buildernametext = (TextView) findViewById(R.id.buildernametext);
        builderurlBtn = (Button) findViewById(R.id.builderurltext);

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (null != mProjectInfoTask)
            mProjectInfoTask.cancel(Boolean.TRUE);
        if (mImageLoader != null) {
            mImageLoader.stopImageLoader();
        }
        mProjectInfoTask = null;
        viewPager = null;
        adapter = null;
        imageArray = null;
        defaultArray = null;
        imageLinks = null;
    }

    private void fetchImage(String URL, int pos) {
        mImageLoader = new ImageLoader(this, mHandler);
        boolean check = viewPager != null
                && viewPager.getAdapter() != null
                && ((ImagePagerAdapter) viewPager.getAdapter())
                        .getHolderViews() != null;
        if (check) {
            HolderView view = ((ImagePagerAdapter) viewPager.getAdapter())
                    .getHolderViews()[pos];
            if (view != null) {
                ImageView v = ((ImagePagerAdapter) viewPager.getAdapter())
                        .getHolderViews()[pos].getView();
                if (v != null) {
                    mImageLoader.displayImage(URL, v);
                    setImageAtPos(pos, v.getDrawable());
                }
            }
        }
    }

    class GetProjectInfoTask extends AsyncTask<String, Drawable[], ProjectInfo> {

        protected ProjectInfo doInBackground(String... params) {
            Log.d(TAG, "GetProjectInfoTask");
            ProjectInfo projectInfo = doJsonParse(Utility.httpGet(params[0])
                    .toString());
            if (projectInfo == null) {
                return projectInfo;
            } else {
                DataBaseHandler db = new DataBaseHandler(
                        getApplicationContext());
                SaveProjectInfoToDB.saveProjectInfo(projectInfo, db,
                        getApplicationContext());
            }
            imageLinks = new String[projectInfo.documents.length];
            for (int i = 0; i < projectInfo.documents.length; i++) {
                imageLinks[i] = projectInfo.documents[i].reference;
            }
            return projectInfo;
        }

        @Override
        protected void onProgressUpdate(Drawable[]... values) {
            super.onProgressUpdate(values);
            if (values[0] != null) {
                Log.d(TAG, "onProgressUpdate" + values[0]);
                adapter = new ImagePagerAdapter(ProjectDetailsActivity.this,
                        values[0]);
                if (viewPager != null)
                    viewPager.setAdapter(adapter);
            }
        }

        @Override
        protected void onPostExecute(ProjectInfo info) {
            Log.d(TAG, "onPostExecute" + info);
            if (info != null)
                populateDataOnUI(info);
        }

        private ProjectInfo doJsonParse(String string) {
            Log.d(TAG, "doJsonParse");
            ProjectInfo proj = new ProjectInfo();
            try {
                JSONObject projObj = new JSONObject(string);
                if (projObj.has("documents")) {
                    JSONArray documents = projObj.getJSONArray("documents");
                    if (null != documents) {
                        ProjectInfo.Documents docs[] = new ProjectInfo.Documents[documents
                                .length()];
                        JSONObject docu1 = documents.getJSONObject(0);
                        if (docu1 != null) {
                            String url = docu1.getString("reference");
                            if (url != null) {
                                downloadFirstImageAndSet(url, docs.length);
                            }
                        }
                        proj.documents = docs;
                        for (int i = 0; i < documents.length(); i++) {
                            String[] array = new String[documents.length()];
                            JSONObject docu = documents.getJSONObject(i);
                            docs[i] = proj.new Documents();
                            if (docu.has("reference")) {
                                String imageUrl = docu.getString("reference");
                                if (null != imageUrl && !imageUrl.equals("")) {
                                    docs[i].reference = imageUrl;
                                    array[i] = imageUrl;
                                }
                            }
                        }
                        StringBuilder sb = new StringBuilder();
                        for (Documents document : docs) {
                            sb.append(document.toString() + ";");
                        }
                        proj.ImageUrls = sb.toString();
                    }
                    if (projObj.has("addressLine1")) {
                        String add = projObj.getString("addressLine1");
                        if (null != add && !add.equals("")) {
                            proj.addressLine1 = add;
                        }
                    }
                    if (projObj.has("addressLine2")) {
                        String add = projObj.getString("addressLine2");
                        if (null != add && !add.equals("")) {
                            proj.addressLine2 = add;
                        }
                    }

                    if (projObj.has("city")) {
                        String add = projObj.getString("city");
                        if (null != add && !add.equals("")) {
                            proj.city = add;
                        }
                    }
                    if (projObj.has("description")) {
                        String add = projObj.getString("description");
                        if (null != add && !add.equals("")) {
                            proj.description = add;
                        }
                    }

                }
                if (projObj.has("listingId")) {
                    String add = projObj.getString("listingId");
                    if (null != add && !add.equals("")) {
                        proj.listingId = add;
                    }
                }
                if (projObj.has("maxArea")) {
                    String add = projObj.getString("maxArea");
                    if (null != add && !add.equals("")) {
                        proj.maxArea = add;
                    }
                }
                if (projObj.has("maxPrice")) {
                    String add = projObj.getString("maxPrice");
                    if (null != add && !add.equals("")) {
                        proj.maxPrice = add;
                    }
                }
                if (projObj.has("maxPricePerSqft")) {
                    String add = projObj.getString("maxPricePerSqft");
                    if (null != add && !add.equals("")) {
                        proj.maxPricePerSqft = add;
                    }
                }
                if (projObj.has("minArea")) {
                    String add = projObj.getString("minArea");
                    if (null != add && !add.equals("")) {
                        proj.minArea = add;
                    }
                }
                if (projObj.has("minPrice")) {
                    String add = projObj.getString("minPrice");
                    if (null != add && !add.equals("")) {
                        proj.minPrice = add;
                    }
                }
                if (projObj.has("minPricePerSqft")) {
                    String add = projObj.getString("minPricePerSqft");
                    if (null != add && !add.equals("")) {
                        proj.minPricePerSqft = add;
                    }
                }
                if (projObj.has("noOfAvailableUnits")) {
                    String add = projObj.getString("noOfAvailableUnits");
                    if (null != add && !add.equals("") && "null".equals(add)) {
                        proj.noOfAvailableUnits = add;
                    } else {
                        proj.noOfAvailableUnits = "-";
                    }
                }
                if (projObj.has("noOfBlocks")) {
                    String add = projObj.getString("noOfBlocks");
                    if (null != add && !add.equals("")) {
                        if (!add.equals("null")) {
                            proj.noOfBlocks = add;
                        }

                    } else {
                        proj.noOfBlocks = "-";
                    }
                }
                if (projObj.has("noOfUnits")) {
                    String add = projObj.getString("noOfUnits");
                    if (null != add && !add.equals("")) {
                        if (!add.equals("null")) {
                            proj.noOfUnits = add;
                        }
                    } else {
                        proj.noOfUnits = "-";
                    }
                }
                if (projObj.has("posessionDate")) {
                    String add = projObj.getString("posessionDate");
                    if (null != add && !add.equals("")) {
                        if (!add.equals("null")) {
                            long date = Long.parseLong(add);
                            Date itemDate = new Date(date);
                            String itemDateStr = new SimpleDateFormat(
                                    "dd-MM-yyyy").format(itemDate);
                            proj.posessionDate = itemDateStr;
                        } else {
                            proj.posessionDate = "---";
                        }
                    }
                }
                if (projObj.has("projectType")) {
                    String add = projObj.getString("projectType");
                    if (null != add && !add.equals("")) {
                        proj.projectType = add;
                    }
                }
                if (projObj.has("status")) {
                    String add = projObj.getString("status");
                    if (null != add && !add.equals("")) {
                        proj.status = add;
                    }
                }

                JSONArray amenitiesArray = projObj.getJSONArray("amenities");
                if (null != amenitiesArray) {
                    String amenties[] = new String[amenitiesArray.length()];
                    for (int i = 0; i < amenitiesArray.length(); i++) {
                        amenties[i] = amenitiesArray.getString(i);
                    }
                    proj.amenities = Arrays.toString(amenties);
                }

                if (projObj.has("builderDescription")) {
                    String add = projObj.getString("builderDescription");
                    if (null != add && !add.equals("")) {
                        add = "" + Html.fromHtml(add);
                        proj.builderDescription = add;
                    }
                }

                if (projObj.has("builderLogo")) {
                    String add = projObj.getString("builderLogo");
                    if (null != add && !add.equals("")) {
                        proj.builderLogo = add;
                    }
                }
                if (projObj.has("builderName")) {
                    String add = projObj.getString("builderName");
                    if (null != add && !add.equals("")) {
                        proj.builderName = add;
                    }
                }
                if (projObj.has("builderUrl")) {
                    String add = projObj.getString("builderUrl");
                    if (null != add && !add.equals("")) {
                        proj.builderUrl = add;
                    }
                }
                if (projObj.has("otherAmenities")) {
                    String add = projObj.getString("otherAmenities");
                    if (null != add && !add.equals("")) {
                        proj.otherAmenities = add;
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.v(TAG, "" + proj);

            return proj;
        }

        private void downloadFirstImageAndSet(String url, int length) {
            Log.d(TAG, "downloadFirstImageAndSet");
            setImageArray(new Drawable[length]);
            publishProgress(getImageArray());
            fetchImage(url, 0);
        }
    }

    public void populateDataOnUI(final ProjectInfo info) {

        projectNametext.setText(projectName);
        String builderName = info.builderName == null ? "" : info.builderName;

        addressDetails.setText("By " + builderName + "\n" + info.addressLine1);

        propertytypevalue.setText(info.projectType);
        if (Utility.isValidString(info.city)) {
            cityValue.setText(info.city);
        } else {
            cityValue.setText("-");
        }
        if (Utility.isValidString(info.noOfAvailableUnits))
            availableunitsvalue.setText(info.noOfAvailableUnits);
        else {
            availableunitsvalue.setText("-");
        }
        if (Utility.isValidString(info.noOfBlocks))
            blocksValue.setText(info.noOfBlocks);
        else {
            blocksValue.setText("-");
        }

        statusValue.setText(info.status);

        possesionDate.setText(info.posessionDate);

        priceperunitvalue.setText(info.minPricePerSqft + "-"
                + info.maxPricePerSqft);

        coveredAreaValue.setText(info.minArea + "-" + info.maxArea);

        descriptiontext.setText(info.description);

        builderdescriptiontext.setText(info.builderDescription);

        buildernametext.setText(builderName);

        builderurlBtn.setText(info.builderUrl);
        StringBuilder amentiestext = new StringBuilder();
        if (Utility.isValidString(info.amenities)) {
            amentiestext.append(info.amenities);
        }
        if (Utility.isValidString(info.otherAmenities)) {
            amentiestext.append(info.otherAmenities);
        }
        if (Utility.isValidString(amentiestext.toString())) {
            amenitiesText.setText(amentiestext.toString());
        } else {
            amenitiesText.setText("-");
        }

    }

    private void handleImageAdapter(ProjectInfo info) {
        if (info != null && info.ImageUrls != null) {
            final String[] imagearray = info.ImageUrls.split(";");
            defaultArray = new Drawable[imagearray.length];
            for (int i = 0; i < imagearray.length; i++) {
                defaultArray[i] = getResources().getDrawable(
                        R.drawable.roofnfloor_default);
            }
            setImageAdapter(defaultArray);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    fetchImage(imagearray[0], 0);
                }
            }, 100);
        }
    }

    @SuppressWarnings("unused")
    private Drawable[] downLoadImages(String[] URLs, int length) {
        Log.d(TAG, "downloadFirstImageAndSet" + URLs);
        imageArray = new Drawable[length];
        InputStream in = null;
        try {
            for (int i = 0; i < URLs.length; i++) {
                // in = OpenHttpConnection(URLs[i]);
                imageArray[i] = new BitmapDrawable(ProjectDetailsActivity.this
                        .getApplicationContext().getResources(),
                        BitmapFactory.decodeStream(in));
            }
            for (int i = URLs.length; i < length; i++) {
                imageArray[i] = ProjectDetailsActivity.this
                        .getApplicationContext().getResources()
                        .getDrawable(R.drawable.roofnfloor_default);
            }
        } catch (Exception e1) {
            e1.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        Log.d(TAG, "downloadFirstImageAndSet:completed" + URLs);
        return imageArray;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (null != mProjectInfoTask)
            mProjectInfoTask.cancel(Boolean.TRUE);
        if (mImageLoader != null) {
            mImageLoader.stopImageLoader();
        }
        mProjectInfoTask = null;
        viewPager = null;
        adapter = null;
        imageArray = null;
        defaultArray = null;
        imageLinks = null;
    }

    private class ImagePagerHandler implements OnPageChangeListener {

        @Override
        public void onPageSelected(int position) {
            Log.d(TAG, "onPageSelected:" + position);
            if (imageLinks != null) {
                fetchImage(imageLinks[position], position);
            } else if (null != projectInfofromDB) {// fetch from db
                String imageUrls = projectInfofromDB.ImageUrls;
                if (null != imageUrls) {
                    String[] array = imageUrls.split(";");
                    if (array[position] != null) {
                        fetchImage(array[position], position);
                    }
                }
            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    }
}
