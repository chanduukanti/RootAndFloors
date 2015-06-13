package com.roofnfloors.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.text.Html;

import com.roofnfloors.ui.ProjectInfo;
import com.roofnfloors.ui.RoofnFloorsActivity.Project;
import com.roofnfloors.util.Utility;

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    // Database Name
    private static final String DATABASE_NAME = "ProjectsManager";

    // projects table name
    private static final String TABLE_PROJECTS = "Projects";

    // projects details table name
    private static final String TABLE_PROJECT_DETAILS = "ProjectDetails";

    // project details Table Columns names
    private static final String KEY_LISTING_ID = "id";
    private static final String KEY_BUILDER_NAME = "builderName";
    private static final String KEY_ADDRESS1 = "address1";
    private static final String KEY_ADDRESS2 = "address2";
    private static final String KEY_CITY = "city";
    private static final String KEY_AVAILABLEUNITS = "availableUnits";
    private static final String KEY_NOOFBLOCKS = "noblocks";
    private static final String KEY_STATUS = "status";
    private static final String KEY_PROJECTTYPE = "projectType";
    private static final String KEY_POSSESION_DATE = "posessionDate";
    private static final String KEY_MINPRICE_SQFT = "minPricePerSqft";
    private static final String KEY_MAXPRICE_SQFT = "maxPricePerSqft";
    private static final String KEY_MAXAREA = "maxArea";
    private static final String KEY_MINAREA = "minArea";
    private static final String KEY_DESCRIPTION = "description";
    private static final String KEY_SPECIFICATION = "specification";
    private static final String KEY_BUILDER_SPECIFICATION = "builderDescription";
    private static final String KEY_BUILDER_URL = "builderUrl";
    private static final String KEY_AMENITIES = "amenities";
    private static final String KEY_OTHER_AMENITIES = "otherAmenities";
    private static final String KEY_IMAGE_URLS = "imageUrls";
    // projects Table Columns names
    private static final String KEY_PROJECT_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LON = "longitude";

    String CREATE_PROJECTS_TABLE = "CREATE TABLE " + TABLE_PROJECTS + "("
            + KEY_PROJECT_ID + " TEXT PRIMARY KEY," + KEY_NAME + " TEXT,"
            + KEY_LAT + " DOUBLE," + KEY_LON + " DOUBLE" + ")";

    String CREATE_PROJECT_DETAILS_TABLE = "CREATE TABLE "
            + TABLE_PROJECT_DETAILS + "(" + KEY_LISTING_ID
            + " TEXT PRIMARY KEY," + KEY_BUILDER_NAME + " TEXT," + KEY_CITY
            + " TEXT," + KEY_ADDRESS1 + " TEXT," + KEY_ADDRESS2 + " TEXT,"
            + KEY_AVAILABLEUNITS + " INTEGER," + KEY_NOOFBLOCKS + " INTEGER,"
            + KEY_STATUS + " TEXT," + KEY_PROJECTTYPE + " TEXT,"
            + KEY_POSSESION_DATE + " DOUBLE," + KEY_MINPRICE_SQFT + " INTEGER,"
            + KEY_MAXPRICE_SQFT + " INTEGER," + KEY_MAXAREA + " INTEGER,"
            + KEY_MINAREA + " TEXT," + KEY_DESCRIPTION + " TEXT,"
            + KEY_SPECIFICATION + " TEXT," + KEY_BUILDER_SPECIFICATION
            + " TEXT," + KEY_BUILDER_URL + " TEXT," + KEY_AMENITIES + " TEXT,"
            + KEY_OTHER_AMENITIES + " TEXT," + KEY_IMAGE_URLS + " TEXT" + ")";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_PROJECTS_TABLE);
        db.execSQL(CREATE_PROJECT_DETAILS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECTS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PROJECT_DETAILS);
        // Create tables again
        onCreate(db);
    }

    // Adding new contact
    void addProj(Project project, Context context) {
        SQLiteDatabase db = new DataBaseHandler(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_PROJECT_ID, project.id);
        values.put(KEY_NAME, project.projectName);
        values.put(KEY_LAT, project.latitude);
        values.put(KEY_LON, project.longitude);
        // Inserting Row
        db.insert(TABLE_PROJECTS, null, values);
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    void addProjectDetails(ProjectInfo projectInfo, Context context) {
        if (projectInfo == null)
            return;
        SQLiteDatabase db = new DataBaseHandler(context).getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ADDRESS1, projectInfo.addressLine1);
        values.put(KEY_ADDRESS2, projectInfo.addressLine2);
        values.put(KEY_CITY, projectInfo.city);
        values.put(KEY_DESCRIPTION, projectInfo.description);
        values.put(KEY_LISTING_ID, projectInfo.listingId);
        values.put(KEY_MAXAREA, projectInfo.maxArea);
        values.put(KEY_MINAREA, projectInfo.minArea);
        values.put(KEY_MAXPRICE_SQFT, projectInfo.maxPricePerSqft);
        values.put(KEY_MINPRICE_SQFT, projectInfo.minPricePerSqft);
        values.put(KEY_AVAILABLEUNITS, projectInfo.noOfAvailableUnits);
        values.put(KEY_NOOFBLOCKS, projectInfo.noOfBlocks);
        values.put(KEY_POSSESION_DATE, projectInfo.posessionDate);
        values.put(KEY_PROJECTTYPE, projectInfo.projectType);
        // String amenities =
        // Arrays.toString(projectInfo.amenities);Html.fromHtml(projectInfo.specification)
        values.put(KEY_STATUS, projectInfo.status);
        values.put(KEY_AMENITIES, projectInfo.amenities);
        if (Utility.isValidString(projectInfo.builderDescription)) {
            values.put(KEY_BUILDER_SPECIFICATION,
                    "" + Html.fromHtml(projectInfo.builderDescription));
        }
        values.put(KEY_BUILDER_NAME, projectInfo.builderName);
        values.put(KEY_BUILDER_URL, projectInfo.builderUrl);
        values.put(KEY_OTHER_AMENITIES, projectInfo.otherAmenities);
        if (Utility.isValidString(projectInfo.specification)) {
            values.put(KEY_SPECIFICATION,
                    "" + Html.fromHtml(projectInfo.specification));
        }
        values.put(KEY_IMAGE_URLS, projectInfo.ImageUrls);

        db.insert(TABLE_PROJECT_DETAILS, null, values);
        if (db != null && db.isOpen()) {
            db.close();
        }
    }

    // Getting All Contacts
    public List<Project> getAllProjects(Context context) {
        List<Project> projectList = new ArrayList<Project>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_PROJECTS;
        Cursor cursor = null;
        SQLiteDatabase db = null;
        try {
            db = new DataBaseHandler(context).getWritableDatabase();
            cursor = db.rawQuery(selectQuery, new String[] {});

            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    Project project = new Project(cursor.getString(0),
                            cursor.getString(1), cursor.getDouble(2),
                            cursor.getDouble(3));
                    // Adding contact to list
                    projectList.add(project);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            if (e != null)
                e.printStackTrace();
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        // return contact list
        return projectList;
    }

    public ProjectInfo getProjectDetails(String id, Context context) {

        SQLiteDatabase db = null;
        Cursor cursor = null;
        ProjectInfo projectInfo = null;
        try {
            db = new DataBaseHandler(context).getReadableDatabase();

            cursor = db.query(TABLE_PROJECT_DETAILS, new String[] {
                    KEY_ADDRESS1, KEY_ADDRESS2, KEY_PROJECTTYPE, KEY_CITY,
                    KEY_AVAILABLEUNITS, KEY_NOOFBLOCKS, KEY_STATUS,
                    KEY_POSSESION_DATE,

                    KEY_MINPRICE_SQFT, KEY_MAXPRICE_SQFT, KEY_MINAREA,
                    KEY_MAXAREA, KEY_AMENITIES, KEY_DESCRIPTION,
                    KEY_SPECIFICATION, KEY_BUILDER_SPECIFICATION,
                    KEY_BUILDER_NAME, KEY_BUILDER_URL, KEY_OTHER_AMENITIES,
                    KEY_IMAGE_URLS }, KEY_LISTING_ID + "=?",
                    new String[] { id }, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String addressLine1 = cursor.getString(cursor
                            .getColumnIndex(KEY_ADDRESS1));
                    String addressLine2 = cursor.getString(cursor
                            .getColumnIndex(KEY_ADDRESS2));
                    String city = cursor.getString(cursor
                            .getColumnIndex(KEY_CITY));
                    String description = cursor.getString(cursor
                            .getColumnIndex(KEY_DESCRIPTION));
                    String listingId = id;
                    String maxArea = cursor.getString(cursor
                            .getColumnIndex(KEY_MAXAREA));
                    String minArea = cursor.getString(cursor
                            .getColumnIndex(KEY_MINAREA));
                    // String maxPrice =
                    // cursor.getString(cursor.getColumnIndex());
                    // String minPrice =
                    // cursor.getString(cursor.getColumnIndex(KEY_MINPRICE_SQFT));
                    String maxPricePerSqft = cursor.getString(cursor
                            .getColumnIndex(KEY_MAXPRICE_SQFT));
                    String minPricePerSqft = cursor.getString(cursor
                            .getColumnIndex(KEY_MINPRICE_SQFT));
                    String noOfAvailableUnits = cursor.getString(cursor
                            .getColumnIndex(KEY_AVAILABLEUNITS));
                    String noOfBlocks = cursor.getString(cursor
                            .getColumnIndex(KEY_NOOFBLOCKS));
                    String posessionDate = cursor.getString(cursor
                            .getColumnIndex(KEY_POSSESION_DATE));
                    String projectType = cursor.getString(cursor
                            .getColumnIndex(KEY_PROJECTTYPE));
                    String status = cursor.getString(cursor
                            .getColumnIndex(KEY_STATUS));
                    String amenities = cursor.getString(cursor
                            .getColumnIndex(KEY_AMENITIES));
                    String builderDescription = cursor.getString(cursor
                            .getColumnIndex(KEY_BUILDER_SPECIFICATION));
                    String builderName = cursor.getString(cursor
                            .getColumnIndex(KEY_BUILDER_NAME));
                    String builderUrl = cursor.getString(cursor
                            .getColumnIndex(KEY_BUILDER_URL));
                    String otherAmenities = cursor.getString(cursor
                            .getColumnIndex(KEY_OTHER_AMENITIES));
                    String specification = cursor.getString(cursor
                            .getColumnIndex(KEY_SPECIFICATION));
                    String imageUrls = cursor.getString(cursor
                            .getColumnIndex(KEY_IMAGE_URLS));
                    projectInfo = new ProjectInfo(addressLine1, addressLine2,
                            city, description, listingId, maxArea, minArea,
                            maxPricePerSqft, minPricePerSqft,
                            noOfAvailableUnits, noOfBlocks, posessionDate,
                            projectType, status, amenities, builderDescription,
                            builderName, builderUrl, otherAmenities,
                            specification, imageUrls);
                }
            }
            // return contact
            // return projectInfo;
        } catch (Exception e) {
            if (e != null) {
                e.printStackTrace();
            }

        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
        return projectInfo;
    }

    // Getting contacts Count
    public int getProjectsCount(Context context) {
        String countQuery = "SELECT  * FROM " + TABLE_PROJECTS;
        SQLiteDatabase db = new DataBaseHandler(context).getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        if (cursor != null && !cursor.isClosed()) {
            cursor.close();
        }
        if (db != null && db.isOpen()) {
            db.close();
        }
        // return count
        return cursor.getCount();
    }

}
