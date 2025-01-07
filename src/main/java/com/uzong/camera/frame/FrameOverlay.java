package com.uzong.camera.frame;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.Drawable;
import android.util.Log;
import androidx.annotation.DrawableRes;
import androidx.core.content.ContextCompat;
import com.uzong.camera.R;

public class FrameOverlay {
    private static final String TAG = "FrameOverlay";
    private Context context;

    public enum FrameStyle {
        NONE("无边框", 0),
        CLASSIC("经典", R.drawable.frame_classic),
        MODERN("现代", R.drawable.frame_modern),
        VINTAGE("复古", R.drawable.frame_vintage);

        private final String displayName;
        private final int frameResId;

        FrameStyle(String name, @DrawableRes int resId) {
            this.displayName = name;
            this.frameResId = resId;
        }

        public String getDisplayName() {
            return displayName;
        }

        public int getFrameResId() {
            return frameResId;
        }
    }

    public FrameOverlay(Context context) {
        this.context = context;
    }

    public Bitmap applyFrame(Bitmap source, FrameStyle style) {
        if (source == null || style == FrameStyle.NONE) {
            return source;
        }

        try {
            int padding = 40;  // 边框宽度
            Bitmap output = Bitmap.createBitmap(
                source.getWidth() + padding * 2,
                source.getHeight() + padding * 2,
                source.getConfig()
            );

            Canvas canvas = new Canvas(output);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

            // 绘制背景
            paint.setColor(Color.WHITE);
            canvas.drawRect(0, 0, output.getWidth(), output.getHeight(), paint);

            // 绘制原图（在中间位置）
            canvas.drawBitmap(source, padding, padding, paint);

            // 获取边框drawable
            Drawable frameDrawable = ContextCompat.getDrawable(context, style.getFrameResId());
            if (frameDrawable != null) {
                frameDrawable.setBounds(0, 0, output.getWidth(), output.getHeight());
                frameDrawable.draw(canvas);
            }

            return output;
        } catch (Exception e) {
            Log.e(TAG, "Failed to apply frame", e);
            return source;
        }
    }
} 