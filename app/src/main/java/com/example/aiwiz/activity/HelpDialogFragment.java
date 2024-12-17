package com.example.aiwiz.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.aiwiz.R;
import com.example.aiwiz.adapter.HelpPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class HelpDialogFragment extends DialogFragment {

    private TabLayout tabLayout;
    private ViewPager2 viewPager;

    public HelpDialogFragment() {
        // Required empty public constructor
    }

    public static HelpDialogFragment newInstance() {
        return new HelpDialogFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 다이얼로그의 레이아웃을 설정
        return inflater.inflate(R.layout.dialog_help, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tabLayout = view.findViewById(R.id.helpTabLayout);
        viewPager = view.findViewById(R.id.helpViewPager);

        // 탭별 레이아웃 리소스 ID 배열
        int[] tabLayouts = {
                R.layout.tab_help_1,
                R.layout.tab_help_2,
                R.layout.tab_help_3,
                R.layout.tab_help_4,
                R.layout.tab_help_5,
                R.layout.tab_help_6
        };

        // 어댑터 설정
        HelpPagerAdapter pagerAdapter = new HelpPagerAdapter(requireActivity(), tabLayouts);
        viewPager.setAdapter(pagerAdapter);

        // TabLayout과 ViewPager2 연결
        String[] tabTitles = {
                "🔎", "❤️", "📸",
                "📋", "🎥", "🧙‍♂️"
        };

        new TabLayoutMediator(tabLayout, viewPager,
                (tab, position) -> tab.setText(tabTitles[position])
        ).attach();
    }

    @Override
    public void onStart() {
        super.onStart();
        // 다이얼로그의 크기 설정 (선택 사항)
        if (getDialog() != null && getDialog().getWindow() != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            getDialog().getWindow().setLayout(width, height);
        }
    }
}
