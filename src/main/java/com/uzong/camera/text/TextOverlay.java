package com.uzong.camera.text;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;

public class TextOverlay {
    public static class TextInfo {
        public String text;
        public float x, y;
        public float scale;
        public float rotation;
        public int color;
        public float textSize;

        public TextInfo(String text, float x, float y, float scale, float rotation,
                       int color, float textSize) {
            this.text = text;
            this.x = x;
            this.y = y;
            this.scale = scale;
            this.rotation = rotation;
            this.color = color;
            this.textSize = textSize;
        }
    }

    private TextInfo textInfo;

    public TextOverlay(String text) {
        this.textInfo = new TextInfo(text, 0, 0, 1.0f, 0f, 0xFFFFFFFF, 40f);
    }

    public void updateTextInfo(TextInfo info) {
        this.textInfo = info;
    }

    public Bitmap applyToImage(Bitmap sourceBitmap) {
        if (sourceBitmap == null || textInfo == null || textInfo.text.isEmpty()) {
            return sourceBitmap;
        }

        try {
            Bitmap resultBitmap = sourceBitmap.copy(sourceBitmap.getConfig(), true);
            Canvas canvas = new Canvas(resultBitmap);

            Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            textPaint.setColor(textInfo.color);
            textPaint.setTextSize(textInfo.textSize);
            textPaint.setAntiAlias(true);

            // 计算文本尺寸
            float textWidth = textPaint.measureText(textInfo.text);
            Paint.FontMetrics metrics = textPaint.getFontMetrics();
            float textHeight = metrics.descent - metrics.ascent;

            canvas.save();

            // 设置变换矩阵
            Matrix matrix = new Matrix();
            matrix.postTranslate(
                textInfo.x - textWidth / 2,
                textInfo.y - textHeight / 2 - metrics.ascent
            );
            matrix.postScale(textInfo.scale, textInfo.scale, textInfo.x, textInfo.y);
            matrix.postRotate(textInfo.rotation, textInfo.x, textInfo.y);
            canvas.setMatrix(matrix);

            // 绘制文本
            canvas.drawText(textInfo.text, textInfo.x, textInfo.y, textPaint);

            canvas.restore();
            return resultBitmap;
        } catch (Exception e) {
            Log.e("TextOverlay", "Failed to apply text: " + e.getMessage());
            e.printStackTrace();
            return sourceBitmap;
        }
    }
} 