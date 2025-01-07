package com.uzong.camera.editor.text;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 颜色选择适配器
 * 用于在RecyclerView中展示颜色选项
 * 特点：
 * 1. 圆形颜色展示
 * 2. 选中效果（阴影）
 * 3. 点击选择功能
 */
public class ColorAdapter extends RecyclerView.Adapter<ColorAdapter.ColorViewHolder> {
    private int[] colors;                // 颜色数组
    private int selectedPosition = 0;     // 当前选中的位置
    private OnColorSelectedListener listener;  // 颜色选择监听器

    public ColorAdapter(int[] colors) {
        this.colors = colors;
    }

    /**
     * 创建颜色项的视图
     * 设置固定大小和边距的圆形View
     */
    @NonNull
    @Override
    public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View colorView = new View(parent.getContext());
        int size = (int) (40 * parent.getContext().getResources().getDisplayMetrics().density);
        ViewGroup.MarginLayoutParams params = new ViewGroup.MarginLayoutParams(size, size);
        params.setMargins(8, 8, 8, 8);
        colorView.setLayoutParams(params);
        return new ColorViewHolder(colorView);
    }

    /**
     * 绑定颜色数据到视图
     * 设置背景颜色、点击事件和选中效果
     */
    @Override
    public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
        holder.colorView.setBackgroundColor(colors[position]);
        holder.colorView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onColorSelected(colors[position]);
            }
        });
        
        if (selectedPosition == position) {
            holder.colorView.setElevation(8f);
        } else {
            holder.colorView.setElevation(0f);
        }
    }

    @Override
    public int getItemCount() {
        return colors.length;
    }

    public void setOnColorSelectedListener(OnColorSelectedListener listener) {
        this.listener = listener;
    }

    static class ColorViewHolder extends RecyclerView.ViewHolder {
        View colorView;

        ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            colorView = itemView;
        }
    }

    /**
     * 颜色选择的回调接口
     */
    public interface OnColorSelectedListener {
        void onColorSelected(int color);
    }
} 