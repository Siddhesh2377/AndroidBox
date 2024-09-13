package com.dark.androidbox.helper;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.activity.result.ActivityResultLauncher;
import androidx.core.content.ContextCompat;

public class StoragePermissionHelper {

    private final Context context;
    private final ActivityResultLauncher<String[]> requestPermissionsLauncher;
    private final ActivityResultLauncher<Intent> manageAllFilesAccessLauncher;

    private final PermissionCallback callback;

    public StoragePermissionHelper(Context context, PermissionCallback callback,
                                   ActivityResultLauncher<String[]> requestPermissionsLauncher,
                                   ActivityResultLauncher<Intent> manageAllFilesAccessLauncher) {
        this.context = context;
        this.callback = callback;
        this.requestPermissionsLauncher = requestPermissionsLauncher;
        this.manageAllFilesAccessLauncher = manageAllFilesAccessLauncher;
    }

    public void checkAndRequestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 11 and above
            if (!android.os.Environment.isExternalStorageManager()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                intent.setData(Uri.parse("package:" + context.getPackageName()));
                manageAllFilesAccessLauncher.launch(intent);
            } else {
                callback.onPermissionGranted();
            }
        } else {
            // For Android 10 and below
            requestPermissionsLauncher.launch(new String[]{
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            });
        }
    }

    public void handlePermissionsResult() {
        boolean readGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        boolean writeGranted = ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;

        if (readGranted && writeGranted) {
            callback.onPermissionGranted();
        } else {
            callback.onPermissionDenied();
        }
    }

    public void handleManageFilesPermissionResult() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            if (android.os.Environment.isExternalStorageManager()) {
                callback.onPermissionGranted();
            } else {
                callback.onPermissionDenied();
            }
        }
    }

    public interface PermissionCallback {
        void onPermissionGranted();

        void onPermissionDenied();
    }
}
