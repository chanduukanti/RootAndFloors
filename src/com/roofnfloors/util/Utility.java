package com.roofnfloors.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.roofnfloors.loader.ImageLoader;

public class Utility {
    private static final String TAG = Utility.class.getSimpleName();

    // Suppresses default constructor, ensuring non-instantiability.
    private Utility() {
    }

    public static boolean isValidString(String str) {
        return !(str == null || str.equals("") || str.equals("null"));
    }

    public static Drawable bitmapToDrawable(Context _context, Bitmap _bmap) {
        return (_context != null && _bmap != null) ? new BitmapDrawable(
                _context.getResources(), _bmap) : null;
    }

    public static void CopyStream(InputStream is, OutputStream os) {
        final int buffer_size = 1024;
        try {
            byte[] bytes = new byte[buffer_size];
            for (;;) {
                int count = is.read(bytes, 0, buffer_size);
                if (count == -1)
                    break;
                os.write(bytes, 0, count);
            }
        } catch (Exception ex) {
        }
    }

    public static Drawable[] downLoadImages(String[] URLs, Context _context,
            int length) {
        Log.d(TAG, "downloadFirstImageAndSet" + URLs);
        Drawable[] imageArray = new Drawable[length];
        for (int i = 0; i < URLs.length; i++) {
            imageArray[i] = bitmapToDrawable(_context,
                    downloadImage(URLs[i], _context));
        }
        Log.d(TAG, "downloadFirstImageAndSet:completed" + URLs);
        return imageArray;
    }

    public static Bitmap downloadImage(String url, Context context) {
        File f = ImageLoader.getFileCache(context).getFile(url);

        // from SD cache
        Bitmap b = decodeFile(f);
        if (b != null)
            return b;
        InputStream in = null;
        Bitmap bmap = null;
        OutputStream os = null;
        try {
            in = OpenHttpConnection(url);
            bmap = BitmapFactory.decodeStream(in);
            os = new FileOutputStream(f);
            Utility.CopyStream(in, os);
            return decodeFile(f);
        } catch (IOException e1) {
            e1.printStackTrace();
        } finally {
            if (os != null)
                try {
                    os.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bmap;
    }

    private static InputStream OpenHttpConnection(String urlString)
            throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            throw new IOException("Error connecting");
        }
        return in;
    }

    // decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f) {
        try {
            // decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(new FileInputStream(f), null, o);

            // Find the correct scale value. It should be the power of 2.
            final int REQUIRED_SIZE = 70;
            int width_tmp = o.outWidth, height_tmp = o.outHeight;
            int scale = 1;
            while (true) {
                if (width_tmp / 2 < REQUIRED_SIZE
                        || height_tmp / 2 < REQUIRED_SIZE)
                    break;
                width_tmp /= 2;
                height_tmp /= 2;
                scale *= 2;
            }

            // decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
        } catch (FileNotFoundException e) {
        }
        return null;
    }

    public static StringBuilder httpGet(String url) {
        StringBuilder builder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) {
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    builder.append(line);
                }
            } else {
                Log.e(TAG, "Failed to download file");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return builder;
    }

    public static boolean isConnectionAvailable(Context context) {

        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo netInfo = connectivityManager.getActiveNetworkInfo();
            if (netInfo != null && netInfo.isConnected()
                    && netInfo.isConnectedOrConnecting()
                    && netInfo.isAvailable()) {
                return true;
            }
        }
        return false;
    }
}
