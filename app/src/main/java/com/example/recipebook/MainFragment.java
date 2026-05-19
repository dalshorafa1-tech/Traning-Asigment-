package com.example.recipebook;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment {
    ViewPager2 viewPager2;
    TabLayout t;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<String> tabs;
    ArrayList<Fragment> fragments;
    private final String[] tabColors = {
            "#FF5722", // Deep Orange
            "#4CAF50", // Green
            "#2196F3", // Blue
            "#E91E63", // Pink
            "#9C27B0", // Purple
            "#FFC107", // Amber
            "#00BCD4"  // Cyan
    };

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *

     * @return A new instance of fragment MainFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        tabs = new ArrayList<>();
        tabs.add("Main Category");
        fragments = new ArrayList<>();
       // fragments.add(Categoryfragment.newInstance());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return null;
    }
        /*
    }
        // Inflate the layout for this fragment
        viewPager2 = ;
        t = root.findViewById(R.id.caegories_tab);

        // Remove indicator and ripple for better look with custom backgrounds
        t.setSelectedTabIndicatorHeight(0);
        t.setTabRippleColor(null);


        .getAllCategories().observe(getViewLifecycleOwner(), categories -> {
            // Clear to avoid duplication on re-observation
            tabs.clear();
            fragments.clear();

            tabs.add("Main Category");
            fragments.add(Categoryfragment.newInstance());

            for (Category c : categories) {
                tabs.add(c.getCategoryName());
                fragments.add(CategoryFragment.newInstance(c.getCategoryid()));
            }

            return inflater.inflate(R.layout.fragment_main, container, false);


    }

         */

}