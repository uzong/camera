package com.uzong.camera.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;

public class ImageFilter {
    private Context context;
    private RenderScript rs;

    public enum FilterStyle {
        NONE("原图"),
        WARM("暖色"),
        COOL("冷色"),
        VINTAGE("复古"),
        FRESH("清新"),
        ELEGANT("优雅"),
        FILM("胶片"),
        PORTRAIT("人像"),
        NATURE("自然");

        private String displayName;
        FilterStyle(String name) {
            this.displayName = name;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public ImageFilter(Context context) {
        this.context = context;
        rs = RenderScript.create(context);
    }

    public Bitmap applyFilter(Bitmap source, FilterStyle style) {
        if (source == null) return null;
        
        try {
            Bitmap output = Bitmap.createBitmap(
                source.getWidth(),
                source.getHeight(),
                source.getConfig()
            );

            switch (style) {
                case NONE:
                    return source.copy(source.getConfig(), true);
                case WARM:
                    return applyWarmFilter(source, output);
                case COOL:
                    return applyCoolFilter(source, output);
                case VINTAGE:
                    return applyVintageFilter(source, output);
                case FRESH:
                    return applyFreshFilter(source, output);
                case ELEGANT:
                    return applyElegantFilter(source, output);
                case FILM:
                    return applyFilmFilter(source, output);
                case PORTRAIT:
                    return applyPortraitFilter(source, output);
                case NATURE:
                    return applyNatureFilter(source, output);
                default:
                    return source.copy(source.getConfig(), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return source;
        }
    }

    private Bitmap applyWarmFilter(Bitmap input, Bitmap output) {
        // 暖色调滤镜：增加红色和黄色，轻微提高对比度
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.1f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.9f, 0.0f,
            0.1f, 0.1f, 0.0f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyCoolFilter(Bitmap input, Bitmap output) {
        // 冷色调滤镜：增加蓝色，降低饱和度
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            0.9f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.9f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.1f, 0.0f,
            0.0f, 0.0f, 0.1f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyVintageFilter(Bitmap input, Bitmap output) {
        // 复古滤镜：降低饱和度，增加褐色调
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 0.9f, 0.0f, 0.0f,
            0.0f, 0.0f, 0.8f, 0.0f,
            0.1f, 0.1f, 0.0f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        // 添加轻微的褐色调
        ScriptIntrinsicBlend blend = ScriptIntrinsicBlend.create(rs, Element.U8_4(rs));
        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyFreshFilter(Bitmap input, Bitmap output) {
        // 清新滤镜：提高亮度和饱和度，增加一些青色调
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.1f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.1f, 0.0f,
            0.0f, 0.1f, 0.1f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyElegantFilter(Bitmap input, Bitmap output) {
        // 优雅滤镜：轻微提高对比度，增加紫色调
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.1f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.0f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.1f, 0.0f,
            0.05f, 0.0f, 0.05f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyFilmFilter(Bitmap input, Bitmap output) {
        // 胶片滤镜：增加对比度，轻微降低饱和度
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.2f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.1f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.0f, 0.0f, 0.0f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyPortraitFilter(Bitmap input, Bitmap output) {
        // 人像滤镜：柔化效果，轻微提高暖色调
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.1f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.05f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.0f, 0.0f,
            0.05f, 0.05f, 0.0f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        // 应用柔化效果
        ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        blur.setRadius(1.0f);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        
        // 先应用颜色矩阵
        colorMatrix.forEach(inputAlloc, outputAlloc);
        // 再应用轻微模糊
        blur.setInput(outputAlloc);
        blur.forEach(outputAlloc);
        
        outputAlloc.copyTo(output);
        return output;
    }

    private Bitmap applyNatureFilter(Bitmap input, Bitmap output) {
        // 自然滤镜：增强绿色和蓝色，提高饱和度
        ScriptIntrinsicColorMatrix colorMatrix = ScriptIntrinsicColorMatrix.create(rs);
        Matrix4f matrix = new Matrix4f(new float[]{
            1.0f, 0.0f, 0.0f, 0.0f,
            0.0f, 1.1f, 0.0f, 0.0f,
            0.0f, 0.0f, 1.1f, 0.0f,
            0.0f, 0.05f, 0.05f, 1.0f
        });
        colorMatrix.setColorMatrix(matrix);

        Allocation inputAlloc = Allocation.createFromBitmap(rs, input);
        Allocation outputAlloc = Allocation.createFromBitmap(rs, output);
        colorMatrix.forEach(inputAlloc, outputAlloc);
        outputAlloc.copyTo(output);
        return output;
    }

    public void release() {
        if (rs != null) {
            rs.destroy();
            rs = null;
        }
    }
} 