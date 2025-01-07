package com.uzong.camera.beauty;

import android.content.Context;
import android.graphics.Bitmap;
import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.filter.*;

public class BeautyFilter {
    private Context context;
    private GPUImage gpuImage;

    public BeautyFilter(Context context) {
        this.context = context;
        this.gpuImage = new GPUImage(context);
    }

    public Bitmap applyPresetBeauty(Bitmap sourceBitmap, BeautyPreset preset) {
        if (sourceBitmap == null) return null;

        try {
            gpuImage.setImage(sourceBitmap);
            gpuImage.setFilter(getPresetFilter(preset));
            return gpuImage.getBitmapWithFilterApplied();
        } catch (Exception e) {
            e.printStackTrace();
            return sourceBitmap;
        }
    }

    private GPUImageFilter getPresetFilter(BeautyPreset preset) {
        switch (preset) {
            case NATURAL:
                return createNaturalFilter();
            case FRESH:
                return createFreshFilter();
            case CUTE:
                return createCuteFilter();
            case GODDESS:
                return createGoddessFilter();
            default:
                return new GPUImageFilter();
        }
    }

    private GPUImageFilter createNaturalFilter() {
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        
        // 磨皮
        GPUImageBilateralBlurFilter bilateralFilter = new GPUImageBilateralBlurFilter();
        bilateralFilter.setDistanceNormalizationFactor(2.0f);
        filterGroup.addFilter(bilateralFilter);
        
        // 美白
        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
        brightnessFilter.setBrightness(0.1f);
        filterGroup.addFilter(brightnessFilter);
        
        // 调整对比度
        GPUImageContrastFilter contrastFilter = new GPUImageContrastFilter();
        contrastFilter.setContrast(1.1f);
        filterGroup.addFilter(contrastFilter);

        return filterGroup;
    }

    private GPUImageFilter createFreshFilter() {
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        
        // 磨皮
        GPUImageBilateralBlurFilter bilateralFilter = new GPUImageBilateralBlurFilter();
        bilateralFilter.setDistanceNormalizationFactor(3.0f);
        filterGroup.addFilter(bilateralFilter);
        
        // 美白
        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
        brightnessFilter.setBrightness(0.15f);
        filterGroup.addFilter(brightnessFilter);
        
        // 增加饱和度
        GPUImageSaturationFilter saturationFilter = new GPUImageSaturationFilter();
        saturationFilter.setSaturation(1.2f);
        filterGroup.addFilter(saturationFilter);
        
        // 调整对比度
        GPUImageContrastFilter contrastFilter = new GPUImageContrastFilter();
        contrastFilter.setContrast(1.2f);
        filterGroup.addFilter(contrastFilter);

        return filterGroup;
    }

    private GPUImageFilter createCuteFilter() {
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        
        // 磨皮
        GPUImageBilateralBlurFilter bilateralFilter = new GPUImageBilateralBlurFilter();
        bilateralFilter.setDistanceNormalizationFactor(4.0f);
        filterGroup.addFilter(bilateralFilter);
        
        // 美白
        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
        brightnessFilter.setBrightness(0.2f);
        filterGroup.addFilter(brightnessFilter);
        
        // 增加饱和度
        GPUImageSaturationFilter saturationFilter = new GPUImageSaturationFilter();
        saturationFilter.setSaturation(1.3f);
        filterGroup.addFilter(saturationFilter);
        
        // 调整色调
        GPUImageColorBalanceFilter colorBalanceFilter = new GPUImageColorBalanceFilter();
        colorBalanceFilter.setMidtones(new float[]{0.1f, 0.0f, 0.0f}); // 增加粉红色调
        filterGroup.addFilter(colorBalanceFilter);

        return filterGroup;
    }

    private GPUImageFilter createGoddessFilter() {
        GPUImageFilterGroup filterGroup = new GPUImageFilterGroup();
        
        // 强磨皮
        GPUImageBilateralBlurFilter bilateralFilter = new GPUImageBilateralBlurFilter();
        bilateralFilter.setDistanceNormalizationFactor(5.0f);
        filterGroup.addFilter(bilateralFilter);
        
        // 强美白
        GPUImageBrightnessFilter brightnessFilter = new GPUImageBrightnessFilter();
        brightnessFilter.setBrightness(0.25f);
        filterGroup.addFilter(brightnessFilter);
        
        // 增加饱和度
        GPUImageSaturationFilter saturationFilter = new GPUImageSaturationFilter();
        saturationFilter.setSaturation(1.4f);
        filterGroup.addFilter(saturationFilter);
        
        // 调整对比度
        GPUImageContrastFilter contrastFilter = new GPUImageContrastFilter();
        contrastFilter.setContrast(1.3f);
        filterGroup.addFilter(contrastFilter);
        
        // 调整色调
        GPUImageWhiteBalanceFilter whiteBalanceFilter = new GPUImageWhiteBalanceFilter();
        whiteBalanceFilter.setTemperature(5000);
        whiteBalanceFilter.setTint(0.5f);
        filterGroup.addFilter(whiteBalanceFilter);

        return filterGroup;
    }

    public enum BeautyPreset {
        NATURAL("自然"),    // 轻度美颜
        FRESH("清新"),      // 中度美颜
        CUTE("可爱"),       // 可爱效果，偏粉嫩
        GODDESS("女神");    // 女神效果，最强美颜
        
        private final String displayName;
        
        BeautyPreset(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
} 