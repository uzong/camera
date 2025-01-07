package com.uzong.camera.editor.base;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.uzong.camera.R;
import com.uzong.camera.editor.EditHistory;
import java.io.OutputStream;

public abstract class BaseEditorFragment extends Fragment {
    protected static final String TAG = "BaseEditorFragment";
    protected ImageChangeCallback callback;
    protected EditHistory editHistory;
    protected ImageView imagePreview;
    protected View btnUndo;
    protected View btnRedo;
    protected View btnConfirm;
    protected View btnCancel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 子类必须包含 fragment_editor_base 布局
        return inflater.inflate(getLayoutResId(), container, false);
    }

    protected abstract int getLayoutResId();

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initBaseViews(view);
        setupBaseButtons();
    }

    private void initBaseViews(View view) {
        View baseView = view.findViewById(R.id.editorBaseLayout);
        if (baseView != null) {
            btnUndo = baseView.findViewById(R.id.btnUndo);
            btnRedo = baseView.findViewById(R.id.btnRedo);
            btnConfirm = baseView.findViewById(R.id.btnConfirm);
            btnCancel = baseView.findViewById(R.id.btnCancel);
        }
    }

    private void setupBaseButtons() {
        if (btnUndo != null) btnUndo.setOnClickListener(v -> onUndo());
        if (btnRedo != null) btnRedo.setOnClickListener(v -> onRedo());
        if (btnConfirm != null) btnConfirm.setOnClickListener(v -> onConfirm());
        if (btnCancel != null) btnCancel.setOnClickListener(v -> onCancel());
        
        updateUndoRedoState();
    }

    protected void onUndo() {
        if (editHistory != null && editHistory.canUndo()) {
            Bitmap previous = editHistory.undo();
            updatePreview(previous);
            updateUndoRedoState();
        }
    }

    protected void onRedo() {
        if (editHistory != null && editHistory.canRedo()) {
            Bitmap next = editHistory.redo();
            updatePreview(next);
            updateUndoRedoState();
        }
    }

    protected void onConfirm() {
        if (callback != null && editHistory != null) {
            Bitmap currentEdit = editHistory.getCurrentEdit();
            if (currentEdit != null) {
                saveEditedImage(currentEdit);
                callback.onImageChanged(currentEdit);
            }
        }
        requireActivity().onBackPressed();
    }

    protected void onCancel() {
        if (callback != null && editHistory != null) {
            callback.onImageChanged(editHistory.getCurrentEdit());
        }
        requireActivity().onBackPressed();
    }

    protected void updatePreview(Bitmap bitmap) {
        if (imagePreview != null) {
            imagePreview.setImageBitmap(bitmap);
        }
    }

    private void saveEditedImage(Bitmap bitmap) {
        if (bitmap == null) return;

        try {
            Uri originalUri = editHistory.getOriginalUri();
            if (originalUri == null) {
                Log.e(TAG, "Original URI is null");
                return;
            }

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.Images.Media.IS_PENDING, 1);
                values.put(MediaStore.Images.Media.RELATIVE_PATH, "DCIM/Camera");
            }

            Uri uri = requireContext().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            
            if (uri != null) {
                try (OutputStream os = requireContext().getContentResolver().openOutputStream(uri)) {
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, os);
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        values.clear();
                        values.put(MediaStore.Images.Media.IS_PENDING, 0);
                        requireContext().getContentResolver().update(uri, values, null, null);
                    }

                    requireContext().sendBroadcast(new Intent(
                        Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri));
                    
                    Log.d(TAG, "Image saved successfully: " + uri);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "Error saving edited image", e);
        }
    }

    protected void updateUndoRedoState() {
        if (callback != null) {
            callback.onHistoryStateChanged(
                editHistory != null && editHistory.canUndo(),
                editHistory != null && editHistory.canRedo()
            );
        }
    }

    public void setCallback(ImageChangeCallback callback) {
        this.callback = callback;
    }

    public void setImagePreview(ImageView imagePreview) {
        this.imagePreview = imagePreview;
    }

    public void setEditHistory(EditHistory editHistory) {
        this.editHistory = editHistory;
        updateUndoRedoState();
    }

    public interface ImageChangeCallback {
        void onImageChanged(Bitmap bitmap);
        void onHistoryStateChanged(boolean canUndo, boolean canRedo);
    }
} 