package com.uzong.camera.editor.fragments;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;
import com.uzong.camera.editor.base.BaseEditorFragment;
import com.uzong.camera.beauty.BeautyFilter;
import com.uzong.camera.beauty.BeautyPresetAdapter;

public class BeautyFragment extends BaseEditorFragment {
    private static final String TAG = "BeautyFragment";
    private RecyclerView beautyRecyclerView;
    private BeautyFilter beautyFilter;
    private BeautyPresetAdapter beautyAdapter;
    private Bitmap originalBitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        beautyFilter = new BeautyFilter(requireContext());
        beautyAdapter = new BeautyPresetAdapter(BeautyFilter.BeautyPreset.values());
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_beauty;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupBeautyList();
        if (editHistory != null) {
            originalBitmap = editHistory.getCurrentEdit();
        }
    }

    private void initViews(View view) {
        beautyRecyclerView = view.findViewById(R.id.beautyRecyclerView);
    }

    private void setupBeautyList() {
        if (beautyRecyclerView == null) return;

        beautyRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(), 
            LinearLayoutManager.HORIZONTAL,
            false
        );
        layoutManager.setInitialPrefetchItemCount(5);
        beautyRecyclerView.setLayoutManager(layoutManager);

        beautyAdapter.setOnBeautySelectedListener(preset -> {
            if (editHistory != null && originalBitmap != null) {
                Bitmap result = beautyFilter.applyPresetBeauty(originalBitmap, preset);
                editHistory.pushEdit(result);
                updatePreview(result);
                updateUndoRedoState();
            }
        });

        beautyRecyclerView.setAdapter(beautyAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        beautyRecyclerView.setAdapter(null);
        if (originalBitmap != null) {
            originalBitmap = null;
        }
    }
} 