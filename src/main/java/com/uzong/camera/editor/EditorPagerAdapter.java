package com.uzong.camera.editor;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.uzong.camera.editor.fragments.BeautyFragment;
import com.uzong.camera.editor.fragments.FilterFragment;
import com.uzong.camera.editor.fragments.FrameFragment;
import com.uzong.camera.editor.fragments.TextFragment;

public class EditorPagerAdapter extends FragmentStateAdapter {
    private static final int NUM_PAGES = 4;
    private static final String KEY_FRAGMENT_TAG = "fragment_tag";
    private final FragmentManager fragmentManager;

    public EditorPagerAdapter(FragmentManager fragmentManager, Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String tag = "f" + position;

        // 检查是否已存在
        Fragment existing = fragmentManager.findFragmentByTag(tag);
        if (existing != null) {
            return existing;
        }

        // 创建新的 Fragment
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new BeautyFragment();
                break;
            case 1:
                fragment = new FilterFragment();
                break;
            case 2:
                fragment = new FrameFragment();
                break;
            case 3:
                fragment = new TextFragment();
                break;
            default:
                fragment = new BeautyFragment();
                break;
        }

        // 使用 Bundle 存储 tag
        Bundle args = new Bundle();
        args.putString(KEY_FRAGMENT_TAG, tag);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public int getItemCount() {
        return NUM_PAGES;
    }
} 