package com.uzong.camera.editor.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;

public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ViewHolder> {
    private final int[] colors;
    private int selectedPosition = 0;
    private OnColorSelectedListener listener;

    public ColorAdapter(int[] colors) {
        this.colors = colors;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_color, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.colorView.setBackgroundColor(colors[position]);
        holder.itemView.setSelected(position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onColorSelected(colors[position]);
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        ViewHolder(View itemView) {
            super(itemView);
            colorView = itemView.findViewById(R.id.colorView);
        }
    }

    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
} 