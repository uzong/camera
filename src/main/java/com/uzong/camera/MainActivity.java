package com.uzong.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.uzong.camera.editor.BeautyEditorActivity;
import com.uzong.camera.editor.EditorActivity;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.ContentValues;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private ActivityResultLauncher<String[]> permissionLauncher;
    private ActivityResultLauncher<Intent> cameraLauncher;
    private ActivityResultLauncher<String> galleryLauncher;
    private Uri photoUri;
    private String currentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initLaunchers();
        initButtons();
    }

    private void initLaunchers() {
        permissionLauncher = registerForActivityResult(
            new ActivityResultContracts.RequestMultiplePermissions(),
            permissions -> {
                boolean allGranted = true;
                for (Boolean granted : permissions.values()) {
                    allGranted = allGranted && granted;
                }
                if (allGranted) {
                    startCamera();
                } else {
                    Toast.makeText(this, R.string.permission_required, Toast.LENGTH_SHORT).show();
                }
            }
        );

        cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (photoUri != null) {
                        Log.d(TAG, "Camera result received with URI: " + photoUri);
                        saveImageToGallery(photoUri);
                    }
                }
            }
        );

        galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    BeautyEditorActivity.start(this, uri);
                }
            }
        );
    }

    private void initButtons() {
        findViewById(R.id.btnTakePhoto).setOnClickListener(v -> checkPermissionsAndStartCamera());
        findViewById(R.id.btnPickImage).setOnClickListener(v -> checkPermissionsAndOpenGallery());
    }

    private void checkPermissionsAndStartCamera() {
        Log.d(TAG, "checkPermissionsAndStartCamera: 开始检查权限");
        String[] permissions = getRequiredPermissions();
        if (hasPermissions(permissions)) {
            Log.d(TAG, "checkPermissionsAndStartCamera: 已有权限，启动相机");
            startCamera();
        } else {
            Log.d(TAG, "checkPermissionsAndStartCamera: 请求权限");
            permissionLauncher.launch(permissions);
        }
    }

    private void checkPermissionsAndOpenGallery() {
        String[] permissions = getStoragePermissions();
        if (hasPermissions(permissions)) {
            galleryLauncher.launch("image/*");
        } else {
            permissionLauncher.launch(permissions);
        }
    }

    private String[] getRequiredPermissions() {
        List<String> permissions = new ArrayList<>();
        permissions.add(Manifest.permission.CAMERA);
        
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissions.add(Manifest.permission.READ_MEDIA_IMAGES);
        } else {
            permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        
        return permissions.toArray(new String[0]);
    }

    private String[] getStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return new String[]{Manifest.permission.READ_MEDIA_IMAGES};
        } else {
            return new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            };
        }
    }

    private boolean hasPermissions(String[] permissions) {
        for (String permission : permissions) {
            boolean granted = ContextCompat.checkSelfPermission(this, permission) 
                == PackageManager.PERMISSION_GRANTED;
            Log.d(TAG, "hasPermissions: " + permission + " is " + (granted ? "granted" : "denied"));
            if (!granted) {
                return false;
            }
        }
        return true;
    }

    private void startCamera() {
        try {
            if (photoUri != null) {
                revokeUriPermission(photoUri, 
                    Intent.FLAG_GRANT_WRITE_URI_PERMISSION | 
                    Intent.FLAG_GRANT_READ_URI_PERMISSION);
            }

            File photoFile = createImageFile();
            if (photoFile != null) {
                photoUri = FileProvider.getUriForFile(this,
                    "com.uzong.camera.fileprovider",
                    photoFile);

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);

                // 优化权限授予
                grantCameraPermissions(intent);
                cameraLauncher.launch(intent);
            }
        } catch (IOException ex) {
            Log.e(TAG, "Error creating image file", ex);
            Toast.makeText(this, "创建图片文件失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void grantCameraPermissions(Intent intent) {
        List<ResolveInfo> resInfoList = getPackageManager()
            .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            grantUriPermission(packageName, photoUri, 
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | 
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 清理权限和资源
        if (photoUri != null) {
            revokeUriPermission(photoUri, 
                Intent.FLAG_GRANT_WRITE_URI_PERMISSION | 
                Intent.FLAG_GRANT_READ_URI_PERMISSION);
            photoUri = null;
        }
        currentPhotoPath = null;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp;

        // 使用标准的相机目录
        File storageDir = new File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM), 
            "Camera"
        );
        
        if (!storageDir.exists()) {
            storageDir.mkdirs();
        }

        File image = new File(storageDir, imageFileName + ".jpg");
        currentPhotoPath = image.getAbsolutePath();

        // 如果是 Android 10 及以上，添加到 MediaStore
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
            photoUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        }

        return image;
    }

    private void saveImageToGallery(Uri uri) {
        if (uri == null) {
            Log.e(TAG, "saveImageToGallery: uri is null");
            return;
        }

        try {
            updateMediaStore(uri);
            notifyGallery();
            startEditor(uri);
        } catch (Exception e) {
            Log.e(TAG, "Error saving image", e);
            Toast.makeText(this, "保存图片失败", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateMediaStore(Uri uri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && 
            MediaStore.AUTHORITY.equals(uri.getAuthority())) {
            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.IS_PENDING, 0);
            getContentResolver().update(uri, values, null, null);
        }
    }

    private void notifyGallery() {
        if (currentPhotoPath != null) {
            Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            mediaScanIntent.setData(Uri.fromFile(new File(currentPhotoPath)));
            sendBroadcast(mediaScanIntent);
        }
    }

    private void startEditor(Uri uri) {
        EditorActivity.start(this, uri);
        Log.d(TAG, "Started editor with image: " + uri);
    }
} 