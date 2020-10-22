package com.android.adclikmi;

import android.app.Activity;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.List;

/**
 * Marcel 2019 *
 **/

public class uploadHelper {
    private Context context;

    public uploadHelper(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public String getExternalStoragepath(){
        String externalPath=Environment.getExternalStorageDirectory().getAbsolutePath();
        String EMULATED_STORAGE_TARGET = System.getenv("EMULATED_STORAGE_TARGET");
        if(TextUtils.isEmpty(EMULATED_STORAGE_TARGET)){
            File fileList[] = new File("/storage/").listFiles();
            for (File file : fileList)
            { if(!file.getAbsolutePath().equalsIgnoreCase(Environment.getExternalStorageDirectory().getAbsolutePath()) && file.isDirectory() && file.canRead())
                externalPath = file.getAbsolutePath(); }
        }
        Log.d("external path",externalPath);
        return externalPath;
    }

    /**
     * Returns the actual path of the file in the file system
     *
     * @param cUri
     * @param activity
     * @return
     */
    public String getRealPathFromURIPath(Uri cUri, Activity activity) {
        Cursor cursor = activity.getContentResolver().query(cUri, null, null, null, null);
        String realPath = null, selection=null, selectionArgs[]=null;
        Uri contentUri=null;
        if (cursor == null) {
            realPath = cUri.getPath();
        } else {
            if("com.android.externalstorage.documents".equals(cUri.getAuthority())){
                cursor.moveToFirst();
                String document_id0 = cursor.getString(0);
                String document_id = document_id0.substring(document_id0.lastIndexOf(":") + 1);
                Log.d("uripath",cUri.getPath());
                Log.d("first cursor",document_id0);
                realPath = getExternalStoragepath() + "/" + document_id;
            }else if("com.android.providers.downloads.documents".equals(cUri.getAuthority())){
                try {
                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
                        cursor = context.getContentResolver().query(cUri, new String[]{MediaStore.MediaColumns.DISPLAY_NAME}, null, null, null);
                        if (cursor != null && cursor.moveToFirst()) {
                            String document_id = cursor.getString(0);
                            Log.d("uripath", cUri.getPath());
                            Log.d("first cursor", document_id);
                            realPath = Environment.getExternalStorageDirectory().toString() + "/Download/" + document_id;
                            Log.d("real path", realPath);
                        }
                    }else {
                        cursor.moveToFirst();
                        String document_id = cursor.getString(0);
                        Log.d("uripath", cUri.getPath());
                        Log.d("first cursor", document_id);
                        contentUri = ContentUris.withAppendedId(
                                Uri.parse("content://downloads/public_downloads"), Long.valueOf(document_id));
                        realPath = getColumnData(cursor, contentUri, selection, selectionArgs);
                        Log.d("real path", realPath);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    List<String> segments = cUri.getPathSegments();
                    if(segments.size() > 1) {
                        String rawPath = segments.get(1);
                        Log.d("raw path",rawPath);
                        if(!rawPath.startsWith("/")){
                            realPath = rawPath.substring(rawPath.indexOf("/"));
                        }else {
                            realPath = rawPath;
                        }
                        Log.d("raw path",rawPath);
                        Log.d("real path",realPath);
                    }
                }
            }else{
                cursor.moveToFirst();
                String document_id = cursor.getString(0);
                document_id = document_id.substring(document_id.lastIndexOf(":") + 1);
                cursor.close();

                contentUri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                selection = MediaStore.Images.Media._ID + " = ? ";
                selectionArgs = new String[]{document_id};

                realPath = getColumnData(cursor, contentUri, selection, selectionArgs);
            }
        }
        if (cursor != null) {
            cursor.close();
        }

        return realPath;
    }

    public String getColumnData(Cursor cursor, Uri uri, String selection, String[] selectionArgs){
        final String column = "_data";
        final String[] projection = {
                column
        };
        String path;
        cursor = context.getContentResolver().query(uri,
                null, selection, selectionArgs, null);
        cursor.moveToFirst();
        path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
        return path;
    }
}
