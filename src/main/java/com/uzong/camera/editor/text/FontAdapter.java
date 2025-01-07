package com.uzong.camera.editor.text;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;

/**
 * 字体选择适配器
 * 用于在RecyclerView中展示字体选项
 * 特点：
 * 1. 文本形式展示字体名称
 * 2. 选中状态改变文字颜色
 * 3. 点击选择功能
 */
public class FontAdapter extends RecyclerView.Adapter<FontAdapter.FontViewHolder> {
    private String[] fonts;              // 字体名称数组
    private int selectedPosition = 0;     // 当前选中的位置
    private OnFontSelectedListener listener;  // 字体选择监听器

    public FontAdapter(String[] fonts) {
        this.fonts = fonts;
    }

    /**
     * 创建字体项的视图
     * 使用系统默认的列表项布局
     */
    @NonNull
    @Override
    public FontViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(android.R.layout.simple_list_item_1, parent, false);
        return new FontViewHolder(view);
    }

    /**
     * 绑定字体数据到视图
     * 设置字体名称、点击事件和选中效果
     */
    @Override
    public void onBindViewHolder(@NonNull FontViewHolder holder, int position) {
        holder.textView.setText(fonts[position]);
        holder.textView.setOnClickListener(v -> {
            int oldPosition = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(oldPosition);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onFontSelected(fonts[position]);
            }
        });
        
        if (selectedPosition == position) {
            holder.textView.setTextColor(holder.textView.getContext()
                    .getResources().getColor(R.color.colorPrimary));
        } else {
            holder.textView.setTextColor(Color.BLACK);
        }
    }

    @Override
    public int getItemCount() {
        return fonts.length;
    }

    public void setOnFontSelectedListener(OnFontSelectedListener listener) {
        this.listener = listener;
    }

    static class FontViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        FontViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(android.R.id.text1);
        }
    }

    /**
     * 字体选择的回调接口
     */
    public interface OnFontSelectedListener {
        void onFontSelected(String font);
    }
} 