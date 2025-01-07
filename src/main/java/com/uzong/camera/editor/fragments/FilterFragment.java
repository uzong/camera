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
import com.uzong.camera.filter.ImageFilter;
import com.uzong.camera.filter.FilterPresetAdapter;

public class FilterFragment extends BaseEditorFragment {
    private static final String TAG = "FilterFragment";
    private RecyclerView filterRecyclerView;
    private ImageFilter imageFilter;
    private FilterPresetAdapter filterAdapter;
    private Bitmap originalBitmap;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        imageFilter = new ImageFilter(requireContext());
        filterAdapter = new FilterPresetAdapter(ImageFilter.FilterStyle.values());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
            @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_filter, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupFilterList();
        if (editHistory != null) {
            originalBitmap = editHistory.getCurrentEdit();
        }
    }

    private void initViews(View view) {
        filterRecyclerView = view.findViewById(R.id.filterRecyclerView);
    }

    private void setupFilterList() {
        if (filterRecyclerView == null) return;

        filterRecyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(
            getContext(), 
            LinearLayoutManager.HORIZONTAL,
            false
        );
        layoutManager.setInitialPrefetchItemCount(5);
        filterRecyclerView.setLayoutManager(layoutManager);

        filterAdapter.setOnFilterSelectedListener(style -> {
            if (editHistory != null && originalBitmap != null) {
                Bitmap result = imageFilter.applyFilter(originalBitmap, style);
                editHistory.pushEdit(result);
                updatePreview(result);
                updateUndoRedoState();
            }
        });

        filterRecyclerView.setAdapter(filterAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        filterRecyclerView.setAdapter(null);
        if (originalBitmap != null) {
            originalBitmap = null;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_filter;
    }
} 