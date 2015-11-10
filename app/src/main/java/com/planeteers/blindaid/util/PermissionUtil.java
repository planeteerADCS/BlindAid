package com.planeteers.blindaid.util;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v13.app.FragmentCompat;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;

import com.planeteers.blindaid.BuildConfig;


/**
 * Created by Jose on 11/9/15.
 */
public class PermissionUtil {

    public static class Camera {

        public static int PERMISSION_REQUEST_CODE = 201;


        public static boolean checkPermissionIfNotAsk(final Fragment fragment, String permission) {

            if(fragment instanceof FragmentCompat.OnRequestPermissionsResultCallback) {
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ContextCompat.checkSelfPermission(fragment.getContext(), permission) != PackageManager.PERMISSION_GRANTED){
                    // Should we show an explanation?
                    if (fragment.shouldShowRequestPermissionRationale(
                            Manifest.permission.CAMERA)) {

                        showMessageOKCancel(fragment, "You need to allow access to your Camera",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        fragment.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                                PERMISSION_REQUEST_CODE);
                                    }
                                });
                        return false;
                    }

                    // No explanation needed, we can request the permission.
                    fragment.requestPermissions(
                                new String[]{Manifest.permission.CAMERA},
                                PERMISSION_REQUEST_CODE);

                    return false;

                }

                return true;
            }else{
                throw new ClassCastException("Fragment does not implement OnRequestPermissionResultCallback");
            }
        }
    }

    private static void showMessageOKCancel(Fragment fragment, String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(fragment.getContext())
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
}
