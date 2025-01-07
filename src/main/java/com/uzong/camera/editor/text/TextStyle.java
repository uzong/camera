package com.uzong.camera.editor.text;

public class TextStyle {
    private String text;
    private int size;
    private int color;
    private String fontName;

    public TextStyle(String text, int size, int color, String fontName) {
        this.text = text;
        this.size = size;
        this.color = color;
        this.fontName = fontName;
    }

    public String getText() {
        return text;
    }

    public int getSize() {
        return size;
    }

    public int getColor() {
        return color;
    }

    public String getFontName() {
        return fontName;
    }
} 