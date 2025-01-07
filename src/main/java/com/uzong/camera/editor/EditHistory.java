package com.uzong.camera.editor;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;
import java.util.ArrayList;
import java.util.List;

public class EditHistory {
    private static final String TAG = "EditHistory";
    private final List<Bitmap> edits = new ArrayList<>();
    private int currentIndex = -1;
    private Uri originalUri;

    public EditHistory(Bitmap original, Uri uri) {
        if (original != null) {
            edits.add(original);
            currentIndex = 0;
        }
        this.originalUri = uri;
    }

    public Uri getOriginalUri() {
        return originalUri;
    }

    public void pushEdit(Bitmap newEdit) {
        if (newEdit == null) {
            Log.e(TAG, "Attempted to push null edit");
            return;
        }
        Log.d(TAG, "Pushing new edit to history");
        edits.add(newEdit);
        currentIndex = edits.size() - 1;
    }

    public Bitmap undo() {
        if (canUndo()) {
            Log.d(TAG, "Performing undo operation");
            currentIndex--;
            return edits.get(currentIndex).copy(edits.get(currentIndex).getConfig(), true);
        }
        Log.d(TAG, "Cannot undo - returning current edit");
        return edits.get(currentIndex).copy(edits.get(currentIndex).getConfig(), true);
    }

    public Bitmap redo() {
        if (canRedo()) {
            Log.d(TAG, "Performing redo operation");
            currentIndex++;
            return edits.get(currentIndex).copy(edits.get(currentIndex).getConfig(), true);
        }
        Log.d(TAG, "Cannot redo - returning current edit");
        return edits.get(currentIndex).copy(edits.get(currentIndex).getConfig(), true);
    }

    public boolean canUndo() {
        return currentIndex > 0;
    }

    public boolean canRedo() {
        return currentIndex < edits.size() - 1;
    }

    public void reset() {
        Log.d(TAG, "Resetting edit history");
        edits.clear();
        currentIndex = -1;
    }

    public Bitmap getCurrentEdit() {
        if (edits.isEmpty()) {
            Log.w(TAG, "Edit history is empty - returning original bitmap");
            return null;
        }
        return edits.get(currentIndex).copy(edits.get(currentIndex).getConfig(), true);
    }

    public void release() {
        Log.d(TAG, "Releasing resources");
        edits.clear();
        currentIndex = -1;
        originalUri = null;
    }
} 