package com.uzong.camera.editor.adapters;

import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.uzong.camera.R;

public class FontAdapter extends RecyclerView.Adapter<FontAdapter.ViewHolder> {
    private String[] fonts;
    private OnFontSelectedListener listener;
    private int selectedPosition = 0;

    public FontAdapter(String[] fonts, OnFontSelectedListener listener) {
        this.fonts = fonts;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_font, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String font = fonts[position];
        holder.textView.setTypeface(Typeface.create(font, Typeface.NORMAL));
        holder.textView.setText("Aa");
        holder.itemView.setSelected(position == selectedPosition);
        
        holder.itemView.setOnClickListener(v -> {
            int previousSelected = selectedPosition;
            selectedPosition = position;
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);
            if (listener != null) {
                listener.onFontSelected(font);
            }
        });
    }

    @Override
    public int getItemCount() {
        return fonts.length;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;

        ViewHolder(View view) {
            super(view);
            textView = view.findViewById(R.id.fontText);
        }
    }

    public interface OnFontSelectedListener {
        void onFontSelected(String font);
    }
} 