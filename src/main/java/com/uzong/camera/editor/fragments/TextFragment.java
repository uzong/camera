package com.uzong.camera.editor.fragments;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;
import com.uzong.camera.editor.base.BaseEditorFragment;
import com.uzong.camera.editor.adapters.ColorAdapter;
import com.uzong.camera.text.DraggableTextView;
import com.uzong.camera.text.TextOverlay;

public class TextFragment extends BaseEditorFragment {
    private EditText textInput;
    private RecyclerView colorRecyclerView;
    private TextOverlay textOverlay;
    private DraggableTextView textView;
    private FrameLayout textContainer;
    private Button btnAddText;
    private static final String TAG = "TextFragment";

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        textOverlay = new TextOverlay("");
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.fragment_text;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
        setupTextView();
        setupListeners();
    }

    private void initViews(View view) {
        textInput = view.findViewById(R.id.textInput);
        colorRecyclerView = view.findViewById(R.id.colorRecyclerView);
        textContainer = view.findViewById(R.id.textContainer);
        btnAddText = view.findViewById(R.id.btnAddText);

        setupColorRecyclerView();
    }

    private void setupTextView() {
        textView = new DraggableTextView(requireContext(), imagePreview);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        );
        textContainer.addView(textView, params);

        // 设置初始位置在中心
        textView.post(() -> {
            int centerX = textContainer.getWidth() / 2;
            int centerY = textContainer.getHeight() / 2;
            textView.setInitialPosition(centerX, centerY);
        });
    }

    private void setupListeners() {
        textInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                textView.setText(s.toString());
                btnAddText.setEnabled(!s.toString().isEmpty());
            }
        });

        btnAddText.setOnClickListener(v -> {
            applyTextOverlay();
            // 清空输入，准备下一次添加
            textInput.setText("");
            textView.setText("");
        });
    }

    private void setupColorRecyclerView() {
        int[] colors = new int[]{
            Color.WHITE, Color.BLACK, Color.RED, Color.GREEN,
            Color.BLUE, Color.YELLOW, Color.CYAN, Color.MAGENTA
        };

        ColorAdapter adapter = new ColorAdapter(colors);
        adapter.setOnColorSelectedListener(color -> {
            textView.setTextColor(color);
        });
        
        colorRecyclerView.setLayoutManager(new LinearLayoutManager(
            getContext(), LinearLayoutManager.HORIZONTAL, false));
        colorRecyclerView.setAdapter(adapter);
    }

    private void applyTextOverlay() {
        String inputText = textInput.getText().toString();
        if (editHistory != null && !inputText.isEmpty()) {
            TextOverlay.TextInfo textInfo = textView.getTextInfo();
            if (textInfo != null) {
                Log.d(TAG, String.format("Applying text: '%s' at (%.1f, %.1f) scale=%.2f rotation=%.1f",
                    textInfo.text, textInfo.x, textInfo.y, textInfo.scale, textInfo.rotation));

                // 创建新的TextOverlay实例，避免状态混淆
                TextOverlay overlay = new TextOverlay(inputText);
                overlay.updateTextInfo(textInfo);

                Bitmap current = editHistory.getCurrentEdit();
                Bitmap result = overlay.applyToImage(current);

                if (result != current) {
                    editHistory.pushEdit(result);
                    updatePreview(result);
                    updateUndoRedoState();
                    Log.d(TAG, "Text applied successfully");

                    // 清空输入，准备下一次添加
                    textInput.setText("");
                    textView.setText("");
                    btnAddText.setEnabled(false);
                } else {
                    Log.e(TAG, "Failed to apply text overlay");
                }
            } else {
                Log.e(TAG, "Failed to get text info");
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        textInput.removeTextChangedListener(null);
        colorRecyclerView.setAdapter(null);
        textContainer.removeAllViews();
    }
} 