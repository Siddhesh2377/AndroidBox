package com.dark.androidbox;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.dark.androidbox.databinding.ActivityMainBinding;
import com.dark.androidbox.fragments.EditorFragment;
import com.dark.androidbox.fragments.TerminalFragment;
import com.dark.androidbox.helper.StoragePermissionHelper;

@SuppressLint("SetTextI18n")
public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private StoragePermissionHelper storagePermissionHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate layout using ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Check and manage storage permissions
        handleStoragePermissions();
    }

    /**
     * Method to manage storage permissions and initialize storage-related operations.
     */
    private void handleStoragePermissions() {
        // Launcher for requesting multiple permissions
        ActivityResultLauncher<String[]> requestPermissionsLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    boolean[] grantResults = new boolean[result.size()];
                    int i = 0;
                    for (Boolean granted : result.values()) {
                        grantResults[i++] = granted;
                    }
                    storagePermissionHelper.handlePermissionsResult();
                });

        // Launcher for managing all files access (Android 11+)
        ActivityResultLauncher<Intent> manageAllFilesAccessLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> storagePermissionHelper.handleManageFilesPermissionResult()
        );

        // Initialize StoragePermissionHelper with appropriate callbacks
        storagePermissionHelper = new StoragePermissionHelper(
                this,
                new StoragePermissionHelper.PermissionCallback() {
                    @Override
                    public void onPermissionGranted() {
                        processFiles();
                    }

                    @Override
                    public void onPermissionDenied() {
                        Toast.makeText(MainActivity.this, "Allow permission for storage access!", Toast.LENGTH_SHORT).show();
                    }
                },
                requestPermissionsLauncher,
                manageAllFilesAccessLauncher
        );

        // Check and request permissions
        storagePermissionHelper.checkAndRequestPermission();
    }

    /**
     * Initialize EditorFragment after permission handling.
     */
    private void initEditorFragment() {
        getSupportFragmentManager().beginTransaction()
                .add(R.id.frame, new EditorFragment())
                .commit();
    }

    /**
     * Process files after storage permission is granted.
     */
    private void processFiles() {
        initEditorFragment();
    }
}
