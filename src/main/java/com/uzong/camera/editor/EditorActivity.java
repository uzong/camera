package com.uzong.camera.editor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.uzong.camera.R;
import com.uzong.camera.editor.base.BaseEditorFragment;
import com.uzong.camera.editor.fragments.BeautyFragment;
import com.uzong.camera.editor.fragments.FilterFragment;
import com.uzong.camera.editor.fragments.FrameFragment;
import com.uzong.camera.editor.fragments.TextFragment;
import android.widget.ImageView;
import android.view.View;

public class EditorActivity extends AppCompatActivity implements BaseEditorFragment.ImageChangeCallback {
    private static final String EXTRA_IMAGE_URI = "image_uri";
    private ImageView imagePreview;
    private EditHistory editHistory;
    private BottomNavigationView bottomNav;
    private View btnUndo;
    private View btnRedo;

    public static void start(Context context, Uri imageUri) {
        Intent intent = new Intent(context, EditorActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        imagePreview = findViewById(R.id.imagePreview);
        bottomNav = findViewById(R.id.bottomNavigation);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        if (imageUri != null) {
            try {
                Bitmap bitmap = BitmapFactory.decodeStream(
                    getContentResolver().openInputStream(imageUri));
                if (bitmap != null) {
                    editHistory = new EditHistory(bitmap, imageUri);
                    imagePreview.setImageBitmap(bitmap);
                    setupNavigation();
                    updateUndoRedoState(false, false);
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }

    private void setupNavigation() {
        bottomNav.setOnNavigationItemSelectedListener(item -> {
            Fragment fragment = null;
            int itemId = item.getItemId();

            if (itemId == R.id.nav_beauty) {
                fragment = new BeautyFragment();
            } else if (itemId == R.id.nav_filter) {
                fragment = new FilterFragment();
            } else if (itemId == R.id.nav_frame) {
                fragment = new FrameFragment();
            }

            if (fragment != null) {
                setupFragment(fragment);
                return true;
            }
            return false;
        });

        // 默认选中美颜
        bottomNav.setSelectedItemId(R.id.nav_beauty);
    }

    private void setupFragment(Fragment fragment) {
        if (fragment instanceof BaseEditorFragment) {
            BaseEditorFragment editorFragment = (BaseEditorFragment) fragment;
            editorFragment.setImagePreview(imagePreview);
            editorFragment.setEditHistory(editHistory);
            editorFragment.setCallback(this);
        }

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    public ImageView getImagePreview() {
        return imagePreview;
    }

    @Override
    public void onImageChanged(Bitmap bitmap) {
        if (imagePreview != null) {
            imagePreview.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onHistoryStateChanged(boolean canUndo, boolean canRedo) {
        updateUndoRedoState(canUndo, canRedo);
    }

    private void updateUndoRedoState(boolean canUndo, boolean canRedo) {
        if (btnUndo != null) {
            btnUndo.setEnabled(canUndo);
        }
        if (btnRedo != null) {
            btnRedo.setEnabled(canRedo);
        }
    }
} 