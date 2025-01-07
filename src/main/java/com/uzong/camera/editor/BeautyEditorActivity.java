package com.uzong.camera.editor;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.uzong.camera.R;
import com.uzong.camera.editor.base.BaseEditorFragment;
import com.uzong.camera.editor.fragments.BeautyFragment;
import java.io.InputStream;

public class BeautyEditorActivity extends AppCompatActivity {
    private static final String EXTRA_IMAGE_URI = "image_uri";
    private ImageView imagePreview;
    private EditHistory editHistory;
    private View btnUndo;
    private View btnRedo;

    public static void start(Context context, Uri imageUri) {
        Intent intent = new Intent(context, BeautyEditorActivity.class);
        intent.putExtra(EXTRA_IMAGE_URI, imageUri);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_beauty_editor);

        imagePreview = findViewById(R.id.imagePreview);
        btnUndo = findViewById(R.id.btnUndo);
        btnRedo = findViewById(R.id.btnRedo);

        Uri imageUri = getIntent().getParcelableExtra(EXTRA_IMAGE_URI);
        
        if (imageUri != null) {
            try {
                InputStream is = getContentResolver().openInputStream(imageUri);
                Bitmap bitmap = BitmapFactory.decodeStream(is);
                if (bitmap != null) {
                    editHistory = new EditHistory(bitmap, imageUri);
                    imagePreview.setImageBitmap(bitmap);
                    setupFragment();
                }
            } catch (Exception e) {
                e.printStackTrace();
                finish();
            }
        } else {
            finish();
        }
    }

    private void setupFragment() {
        BeautyFragment fragment = new BeautyFragment();
        fragment.setImagePreview(imagePreview);
        fragment.setEditHistory(editHistory);
        fragment.setCallback(new BaseEditorFragment.ImageChangeCallback() {
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
        });

        getSupportFragmentManager()
            .beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit();
    }

    public ImageView getImagePreview() {
        return imagePreview;
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