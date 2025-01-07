package com.uzong.camera.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.ImageView;
import androidx.core.view.GestureDetectorCompat;
import com.uzong.camera.R;

public class DraggableTextView extends View {
    private String text = "";
    private float x, y;
    private float scale = 1.0f;
    private float rotation = 0f;
    private Paint textPaint;
    private GestureDetectorCompat gestureDetector;
    private ScaleGestureDetector scaleGestureDetector;
    private Matrix transformMatrix = new Matrix();
    private boolean isEditing = true;
    private ImageView imageView;

    // 用于计算旋转角度
    private float lastAngle = 0f;
    private float pivotX = 0f;
    private float pivotY = 0f;

    public DraggableTextView(Context context, ImageView imageView) {
        super(context);
        this.imageView = imageView;
        init();
    }

    private void init() {
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(40f);
        textPaint.setColor(0xFFFFFFFF);  // 默认白色

        gestureDetector = new GestureDetectorCompat(getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                if (isEditing) {
                    x -= distanceX;
                    y -= distanceY;
                    invalidate();
                    return true;
                }
                return false;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return true;
            }
        });

        scaleGestureDetector = new ScaleGestureDetector(getContext(), new ScaleGestureDetector.SimpleOnScaleGestureListener() {
            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                pivotX = detector.getFocusX();
                pivotY = detector.getFocusY();
                lastAngle = (float) Math.toDegrees(Math.atan2(
                    detector.getCurrentSpanY(), 
                    detector.getCurrentSpanX()
                ));
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (isEditing) {
                    // 处理缩放
                    scale *= detector.getScaleFactor();
                    scale = Math.max(0.5f, Math.min(scale, 3.0f));

                    // 处理旋转
                    float currentAngle = (float) Math.toDegrees(Math.atan2(
                        detector.getCurrentSpanY(),
                        detector.getCurrentSpanX()
                    ));
                    float deltaAngle = currentAngle - lastAngle;
                    rotation += deltaAngle;
                    lastAngle = currentAngle;

                    invalidate();
                    return true;
                }
                return false;
            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        // 确保触摸事件在有效区域内
        if (event.getAction() == MotionEvent.ACTION_DOWN && !isValidTouchArea(event.getX(), event.getY())) {
            return false;
        }
        
        boolean handled = scaleGestureDetector.onTouchEvent(event);
        if (!scaleGestureDetector.isInProgress()) {
            handled |= gestureDetector.onTouchEvent(event);
        }
        return handled;
    }

    private boolean isValidTouchArea(float touchX, float touchY) {
        // 检查触摸点是否在文本区域内
        float textWidth = textPaint.measureText(text);
        float textHeight = -textPaint.getFontMetrics().ascent + textPaint.getFontMetrics().descent;
        
        // 考虑缩放和旋转后的区域
        float left = x - textWidth / 2 * scale;
        float top = y - textHeight / 2 * scale;
        float right = x + textWidth / 2 * scale;
        float bottom = y + textHeight / 2 * scale;
        
        return touchX >= left && touchX <= right && touchY >= top && touchY <= bottom;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (text.isEmpty()) return;

        canvas.save();

        // 计算文本尺寸
        float textWidth = textPaint.measureText(text);
        Paint.FontMetrics metrics = textPaint.getFontMetrics();
        float textHeight = metrics.descent - metrics.ascent;

        // 应用变换
        transformMatrix.reset();
        transformMatrix.postTranslate(
            x - textWidth / 2,
            y - textHeight / 2 - metrics.ascent
        );
        transformMatrix.postScale(scale, scale, x, y);
        transformMatrix.postRotate(rotation, x, y);
        canvas.setMatrix(transformMatrix);

        // 绘制文本
        canvas.drawText(text, x, y, textPaint);

        canvas.restore();
    }

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextColor(int color) {
        textPaint.setColor(color);
        invalidate();
    }

    public void setTextSize(float size) {
        textPaint.setTextSize(size);
        invalidate();
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
    }

    public TextOverlay.TextInfo getTextInfo() {
        if (imageView == null || imageView.getDrawable() == null || text.isEmpty()) {
            return null;
        }

        // 获取当前视图和图片的位置
        int[] viewLocation = new int[2];
        int[] imageLocation = new int[2];
        getLocationOnScreen(viewLocation);
        imageView.getLocationOnScreen(imageLocation);

        // 计算相对于ImageView的坐标
        float relativeX = x + viewLocation[0] - imageLocation[0];
        float relativeY = y + viewLocation[1] - imageLocation[1];

        // 将坐标转换为相对于图片的比例
        float percentX = relativeX / imageView.getWidth();
        float percentY = relativeY / imageView.getHeight();

        // 计算在实际图片上的坐标
        float imageWidth = imageView.getDrawable().getIntrinsicWidth();
        float imageHeight = imageView.getDrawable().getIntrinsicHeight();
        float imageX = percentX * imageWidth;
        float imageY = percentY * imageHeight;

        Log.d("DraggableTextView", String.format(
            "Text position: view(%.1f, %.1f) -> image(%.1f, %.1f)",
            x, y, imageX, imageY
        ));

        return new TextOverlay.TextInfo(
            text,
            imageX,
            imageY,
            scale,
            rotation,
            textPaint.getColor(),
            textPaint.getTextSize() * (imageWidth / imageView.getWidth())
        );
    }

    private float[] getImageRect(ImageView imageView) {
        if (imageView.getDrawable() == null) return null;

        float[] rect = new float[4];
        float viewWidth = imageView.getWidth();
        float viewHeight = imageView.getHeight();
        float drawableWidth = imageView.getDrawable().getIntrinsicWidth();
        float drawableHeight = imageView.getDrawable().getIntrinsicHeight();

        float scale = Math.min(viewWidth / drawableWidth, viewHeight / drawableHeight);
        float scaledWidth = drawableWidth * scale;
        float scaledHeight = drawableHeight * scale;

        rect[0] = (viewWidth - scaledWidth) / 2;  // left
        rect[1] = (viewHeight - scaledHeight) / 2;  // top
        rect[2] = rect[0] + scaledWidth;  // right
        rect[3] = rect[1] + scaledHeight;  // bottom

        // 转换为全局坐标
        int[] location = new int[2];
        imageView.getLocationInWindow(location);
        for (int i = 0; i < 4; i += 2) {
            rect[i] += location[0];
        }
        for (int i = 1; i < 4; i += 2) {
            rect[i] += location[1];
        }

        return rect;
    }

    public void setTextInfo(TextOverlay.TextInfo info) {
        this.text = info.text;
        this.x = info.x;
        this.y = info.y;
        this.scale = info.scale;
        this.rotation = info.rotation;
        textPaint.setColor(info.color);
        textPaint.setTextSize(info.textSize);
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        if (x == 0 && y == 0) {
            // 初始位置设置在中心
            x = w / 2f;
            y = h / 2f;
        }
    }

    public void setInitialPosition(float x, float y) {
        this.x = x;
        this.y = y;
        invalidate();
    }
} 