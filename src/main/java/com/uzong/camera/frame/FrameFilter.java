package com.uzong.camera.frame;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import androidx.core.content.ContextCompat;
import com.uzong.camera.R;

public class FrameFilter {
    private Context context;
    private static final float FRAME_PADDING = 0.1f; // 相框内边距占比

    public FrameFilter(Context context) {
        this.context = context;
    }

    public Bitmap applyFrame(Bitmap sourceBitmap, FrameStyle style) {
        if (sourceBitmap == null || style == FrameStyle.NONE) return sourceBitmap;

        try {
            // 创建新的位图，包含边框空间
            int frameWidth = (int) (sourceBitmap.getWidth() * (1 + FRAME_PADDING * 2));
            int frameHeight = (int) (sourceBitmap.getHeight() * (1 + FRAME_PADDING * 2));
            Bitmap framedBitmap = Bitmap.createBitmap(frameWidth, frameHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(framedBitmap);

            // 绘制相框背景
            drawFrameBackground(canvas, frameWidth, frameHeight, style);

            // 计算图片在相框中的位置
            int imageLeft = (int) (frameWidth * FRAME_PADDING);
            int imageTop = (int) (frameHeight * FRAME_PADDING);
            RectF imageRect = new RectF(imageLeft, imageTop, 
                imageLeft + sourceBitmap.getWidth(), 
                imageTop + sourceBitmap.getHeight());

            // 绘制原图
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            canvas.drawBitmap(sourceBitmap, null, imageRect, paint);

            // 绘制相框装饰
            drawFrameDecoration(canvas, imageRect, style);

            return framedBitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return sourceBitmap;
        }
    }

    private void drawFrameBackground(Canvas canvas, int width, int height, FrameStyle style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);

        switch (style) {
            case CLASSIC:
                paint.setColor(0xFFF5F5DC); // 米色背景
                canvas.drawRect(0, 0, width, height, paint);
                break;
            case MODERN:
                paint.setColor(0xFFFFFFFF); // 白色背景
                canvas.drawRect(0, 0, width, height, paint);
                break;
            case VINTAGE:
                paint.setColor(0xFFE6D5AC); // 复古米黄色
                canvas.drawRect(0, 0, width, height, paint);
                break;
            case POLAROID:
                paint.setColor(0xFFFFFFFF); // 白色背景
                canvas.drawRect(0, 0, width, height, paint);
                break;
            case WOOD:
                Drawable woodTexture = ContextCompat.getDrawable(context, R.drawable.frame_wood);
                if (woodTexture != null) {
                    woodTexture.setBounds(0, 0, width, height);
                    woodTexture.draw(canvas);
                }
                break;
        }
    }

    private void drawFrameDecoration(Canvas canvas, RectF imageRect, FrameStyle style) {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.STROKE);

        switch (style) {
            case CLASSIC:
                // 经典双线条边框
                paint.setStrokeWidth(4);
                paint.setColor(0xFF8B4513); // 深棕色
                canvas.drawRect(imageRect, paint);
                
                RectF outerRect = new RectF(
                    imageRect.left - 8,
                    imageRect.top - 8,
                    imageRect.right + 8,
                    imageRect.bottom + 8
                );
                canvas.drawRect(outerRect, paint);
                break;

            case MODERN:
                // 现代简约边框
                paint.setStrokeWidth(3);
                paint.setColor(0xFF333333);
                canvas.drawRect(imageRect, paint);
                break;

            case VINTAGE:
                // 复古做旧效果
                paint.setStrokeWidth(5);
                paint.setColor(0xFF8B4513);
                paint.setStyle(Paint.Style.STROKE);
                canvas.drawRect(imageRect, paint);
                
                // 添加做旧效果
                Paint spotPaint = new Paint();
                spotPaint.setColor(0x33000000);
                for (int i = 0; i < 10; i++) {
                    float x = (float) (Math.random() * canvas.getWidth());
                    float y = (float) (Math.random() * canvas.getHeight());
                    canvas.drawCircle(x, y, 5, spotPaint);
                }
                break;

            case POLAROID:
                // 拍立得风格
                paint.setStrokeWidth(2);
                paint.setColor(0xFFCCCCCC);
                canvas.drawRect(imageRect, paint);
                
                // 底部留白区域
                Paint textBgPaint = new Paint();
                textBgPaint.setColor(0xFFFFFFFF);
                canvas.drawRect(
                    imageRect.left,
                    imageRect.bottom,
                    imageRect.right,
                    imageRect.bottom + 60,
                    textBgPaint
                );
                break;

            case WOOD:
                // 木质相框效果
                paint.setStrokeWidth(6);
                paint.setColor(0xFF4A2F1B);
                canvas.drawRect(imageRect, paint);
                
                // 内层装饰线
                paint.setStrokeWidth(2);
                paint.setColor(0xFF8B4513);
                RectF innerRect = new RectF(
                    imageRect.left + 4,
                    imageRect.top + 4,
                    imageRect.right - 4,
                    imageRect.bottom - 4
                );
                canvas.drawRect(innerRect, paint);
                break;
        }
    }

    public enum FrameStyle {
        NONE("无边框"),
        CLASSIC("经典"),
        MODERN("现代"),
        VINTAGE("复古"),
        POLAROID("拍立得"),
        WOOD("木质");

        private final String displayName;

        FrameStyle(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
} 