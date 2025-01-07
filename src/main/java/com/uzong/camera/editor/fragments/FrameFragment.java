package com.uzong.camera.editor.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;
import com.uzong.camera.editor.base.BaseEditorFragment;
import com.uzong.camera.frame.FrameOverlay;
import com.uzong.camera.frame.FramePresetAdapter;

public class FrameFragment extends BaseEditorFragment {
    private RecyclerView frameRecyclerView;
    private FrameOverlay frameOverlay;
    private FramePresetAdapter frameAdapter;
    private Bitmap originalBitmap;  // 保存原始图片

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        frameOverlay = new FrameOverlay(requireContext());
        frameAdapter = new FramePresetAdapter(FrameOverlay.FrameStyle.values());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_frame;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupFrameList();
        // 保存原始图片
        if (editHistory != null) {
            originalBitmap = editHistory.getCurrentEdit();
        }
    }

    private void initViews(View view) {
        frameRecyclerView = view.findViewById(R.id.frameRecyclerView);
    }

    private void setupFrameList() {
        if (frameRecyclerView == null) return;

        frameRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(), 
            LinearLayoutManager.HORIZONTAL,
            false
        );
        layoutManager.setInitialPrefetchItemCount(5);
        frameRecyclerView.setLayoutManager(layoutManager);

        frameAdapter.setOnFilterSelectedListener(style -> {
            if (editHistory != null && originalBitmap != null) {
                // 每次都基于原始图片应用相框效果
                Bitmap result = frameOverlay.applyFrame(originalBitmap, style);
                editHistory.pushEdit(result);
                updatePreview(result);
                updateUndoRedoState();
            }
        });

        frameRecyclerView.setAdapter(frameAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        frameRecyclerView.setAdapter(null);
        if (originalBitmap != null) {
            originalBitmap = null;
        }
    }
} 