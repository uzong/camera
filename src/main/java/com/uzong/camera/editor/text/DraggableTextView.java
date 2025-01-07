package com.uzong.camera.editor.text;

import android.content.Context;
import android.graphics.Color;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

public class DraggableTextView extends FrameLayout {
    private TextView textView;
    private float lastX;
    private float lastY;
    private boolean isMoving = false;
    private TextControlView controlView;
    private float rotation = 0;
    private float scale = 1.0f;

    public DraggableTextView(Context context, TextStyle style) {
        super(context);
        init(style);
    }

    private void init(TextStyle style) {
        // 设置布局参数
        LayoutParams params = new LayoutParams(
            LayoutParams.WRAP_CONTENT,
            LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(params);

        // 创建文本视图
        textView = new TextView(getContext());
        textView.setText(style.getText());
        textView.setTextSize(style.getSize() * 0.5f); // 将进度值转换为合适的文字大小
        textView.setTextColor(style.getColor());
        // TODO: 设置字体

        addView(textView, params);

        // 设置触摸监听
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        isMoving = false;
                        return true;

                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getRawX() - lastX;
                        float deltaY = event.getRawY() - lastY;
                        
                        // 移动视图
                        setX(getX() + deltaX);
                        setY(getY() + deltaY);
                        
                        lastX = event.getRawX();
                        lastY = event.getRawY();
                        isMoving = true;
                        return true;

                    case MotionEvent.ACTION_UP:
                        if (!isMoving) {
                            // 如果没有移动，可以处理点击事件
                            performClick();
                        }
                        return true;
                }
                return false;
            }
        });

        controlView = new TextControlView(getContext());
        addView(controlView, new LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        ));
        controlView.setVisibility(View.GONE);

        setupControlButtons();
    }

    private void setupControlButtons() {
        // 删除按钮
        controlView.getDeleteButton().setOnClickListener(v -> {
            ViewGroup parent = (ViewGroup) getParent();
            if (parent != null) {
                parent.removeView(this);
            }
        });

        // 旋转按钮
        controlView.getRotateButton().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float centerX = getX() + getWidth() / 2;
                    float centerY = getY() + getHeight() / 2;
                    float angle = (float) Math.toDegrees(Math.atan2(
                        event.getRawY() - centerY,
                        event.getRawX() - centerX
                    ));
                    setRotation(angle);
                    return true;
            }
            return false;
        });

        // 缩放按钮
        controlView.getScaleButton().setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_MOVE:
                    float centerX = getX() + getWidth() / 2;
                    float centerY = getY() + getHeight() / 2;
                    float distance = (float) Math.sqrt(
                        Math.pow(event.getRawX() - centerX, 2) +
                        Math.pow(event.getRawY() - centerY, 2)
                    );
                    setScaleX(distance / 100);
                    setScaleY(distance / 100);
                    return true;
            }
            return false;
        });
    }

    @Override
    public boolean performClick() {
        super.performClick();
        controlView.setVisibility(
            controlView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
        );
        return true;
    }
} 