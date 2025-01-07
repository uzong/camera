package com.uzong.camera.frame;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;
import com.uzong.camera.frame.FrameOverlay.FrameStyle;

public class FramePresetAdapter extends RecyclerView.Adapter<FramePresetAdapter.ViewHolder> {
    private static final String TAG = "FramePresetAdapter";
    private FrameStyle[] styles;
    private int selectedPosition = 0;
    private OnFilterSelectedListener listener;

    public FramePresetAdapter(FrameStyle[] styles) {
        this.styles = styles;
        Log.d(TAG, "FramePresetAdapter: Created with " + styles.length + " styles");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "onCreateViewHolder");
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_filter_preset, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (holder.textView != null) {
            FrameStyle style = styles[position];
            holder.textView.setText(style.getDisplayName());
            holder.itemView.setSelected(position == selectedPosition);
            Log.d(TAG, "onBindViewHolder: Binding style " + style.getDisplayName() + " at position " + position);
            
            holder.itemView.setOnClickListener(v -> {
                int previousSelected = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousSelected);
                notifyItemChanged(selectedPosition);
                Log.d(TAG, "onClick: Selected " + style.getDisplayName());
                if (listener != null) {
                    listener.onFilterSelected(style);
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
        Log.d(TAG, "getItemCount: " + styles.length);
        return styles.length;
    }

    public void setOnFilterSelectedListener(OnFilterSelectedListener listener) {
        this.listener = listener;
        Log.d(TAG, "setOnFilterSelectedListener: " + (listener != null ? "Set" : "Cleared"));
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.filterText);
        }
    }

    public interface OnFilterSelectedListener {
        void onFilterSelected(FrameStyle style);
    }
} 