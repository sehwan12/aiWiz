// com/example/aiwiz/adapter/HelpPagerAdapter.java

package com.example.aiwiz.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.aiwiz.activity.HelpTabFragment;

public class HelpPagerAdapter extends FragmentStateAdapter {

    private final int[] tabLayouts;

    public HelpPagerAdapter(@NonNull FragmentActivity fragmentActivity, int[] tabLayouts) {
        super(fragmentActivity);
        this.tabLayouts = tabLayouts;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return HelpTabFragment.newInstance(tabLayouts[position]);
    }

    @Override
    public int getItemCount() {
        return tabLayouts.length;
    }
}