package com.example.aiwiz.activity;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class HelpTabFragment extends Fragment {

    private static final String ARG_LAYOUT_ID = "layout_id";

    public HelpTabFragment() {
        // Required empty public constructor
    }

    /**
     * 새 인스턴스 생성 메서드
     *
     * @param layoutId 레이아웃 리소스 ID
     * @return 새 프래그먼트 인스턴스
     */
    public static HelpTabFragment newInstance(int layoutId) {
        HelpTabFragment fragment = new HelpTabFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_LAYOUT_ID, layoutId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (getArguments() != null) {
            int layoutId = getArguments().getInt(ARG_LAYOUT_ID, -1);
            if (layoutId != -1) {
                return inflater.inflate(layoutId, container, false);
            }
        }
        // 기본 레이아웃 또는 에러 처리
        return inflater.inflate(android.R.layout.simple_list_item_1, container, false);
    }
}
