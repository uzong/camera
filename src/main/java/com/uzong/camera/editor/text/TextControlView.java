package com.uzong.camera.editor.text;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.uzong.camera.R;

public class TextControlView extends FrameLayout {
    private Paint borderPaint;
    private ImageView deleteButton;
    private ImageView rotateButton;
    private ImageView scaleButton;
    private RectF borderRect;

    public TextControlView(Context context) {
        super(context);
        init();
    }

    private void init() {
        setWillNotDraw(false);
        
        // 初始化边框画笔
        borderPaint = new Paint();
        borderPaint.setColor(Color.WHITE);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(2);
        
        // 初始化边框矩形
        borderRect = new RectF();

        // 添加控制按钮
        addDeleteButton();
        addRotateButton();
        addScaleButton();
    }

    private void addDeleteButton() {
        deleteButton = new ImageView(getContext());
        deleteButton.setImageResource(R.drawable.ic_delete);
        LayoutParams params = new LayoutParams(48, 48);
        params.leftMargin = -24;
        params.topMargin = -24;
        addView(deleteButton, params);
    }

    private void addRotateButton() {
        rotateButton = new ImageView(getContext());
        rotateButton.setImageResource(R.drawable.ic_rotate);
        LayoutParams params = new LayoutParams(48, 48);
        params.rightMargin = -24;
        params.topMargin = -24;
        addView(rotateButton, params);
    }

    private void addScaleButton() {
        scaleButton = new ImageView(getContext());
        scaleButton.setImageResource(R.drawable.ic_scale);
        LayoutParams params = new LayoutParams(48, 48);
        params.rightMargin = -24;
        params.bottomMargin = -24;
        addView(scaleButton, params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        // 更新边框矩形
        borderRect.set(24, 24, getWidth() - 24, getHeight() - 24);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制边框
        canvas.drawRect(borderRect, borderPaint);
    }

    public ImageView getDeleteButton() {
        return deleteButton;
    }

    public ImageView getRotateButton() {
        return rotateButton;
    }

    public ImageView getScaleButton() {
        return scaleButton;
    }
} 