package com.uzong.camera.beauty;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;

public class BeautyPresetAdapter extends RecyclerView.Adapter<BeautyPresetAdapter.ViewHolder> {
    private static final String TAG = "BeautyPresetAdapter";
    private BeautyFilter.BeautyPreset[] presets;
    private int selectedPosition = 0;
    private OnBeautySelectedListener listener;

    public BeautyPresetAdapter(BeautyFilter.BeautyPreset[] presets) {
        this.presets = presets;
        Log.d(TAG, "BeautyPresetAdapter: Created with " + presets.length + " presets");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_beauty_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.textView != null) {
            BeautyFilter.BeautyPreset preset = presets[position];
            holder.textView.setText(preset.getDisplayName());
            holder.itemView.setSelected(position == selectedPosition);
            Log.d(TAG, "onBindViewHolder: Binding preset " + preset.getDisplayName() + " at position " + position);
            
            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                Log.d(TAG, "onClick: Selected " + preset.getDisplayName());
                if (listener != null) {
                    listener.onBeautySelected(preset);
                } else {
                    Log.e(TAG, "onClick: Listener is null");
                }
            });
        } else {
            Log.e(TAG, "onBindViewHolder: textView is null");
        }
    }

    @Override
    public int getItemCount() {
        return presets.length;
    }

    public void setOnBeautySelectedListener(OnBeautySelectedListener listener) {
        this.listener = listener;
        Log.d(TAG, "setOnBeautySelectedListener: " + (listener != null ? "Set" : "Cleared"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(android.R.id.text1);
        }
    }

    public interface OnBeautySelectedListener {
        void onBeautySelected(BeautyFilter.BeautyPreset preset);
    }
} 