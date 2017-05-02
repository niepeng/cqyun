package com.baibutao.app.waibao.yun.android.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baibutao.app.waibao.yun.android.R;
import com.baibutao.app.waibao.yun.android.common.Constant;

public class TabFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    public static int int_items = 3;

    private int mShowType;
    private String mArea;
    private int mSelectedPosition = 0;

    protected OnTabChangeListener mCallback;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (OnTabChangeListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (getArguments() != null) {
            mShowType = getArguments().getInt(Constant.ARG_SHOW_TYPE, Constant.SHOW_TYPE_LINEAR);
            mArea = getArguments().getString(Constant.ARG_AREA, null);
            mSelectedPosition = getArguments().getInt(Constant.ARG_SELECTED_POSITION, 0);
        }
        /**
         *Inflate tab_layout and setup Views.
         */
        View x = inflater.inflate(R.layout.fragment_main_tab, null);
        tabLayout = (TabLayout) x.findViewById(R.id.tabs);
        viewPager = (ViewPager) x.findViewById(R.id.viewpager);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCallback.onTabSelected(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        /**
         *Set an Apater for the View Pager
         */
        viewPager.setAdapter(new MyAdapter(getChildFragmentManager()));
        viewPager.setCurrentItem(mSelectedPosition);

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
                tabLayout.getTabAt(0).setIcon(R.drawable.icon_tab_intime);
                tabLayout.getTabAt(1).setIcon(R.drawable.icon_tab_alarm);
                tabLayout.getTabAt(2).setIcon(R.drawable.icon_tab_more);
            }
        });

        return x;

    }

    class MyAdapter extends FragmentPagerAdapter {

        public MyAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Return fragment with respect to Position .
         */

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return InTimeFragment.newInstance(mShowType, mArea);
                case 1:
                    return new AlarmFragment();
                case 2:
                    return new MoreFragment();
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return "实时";
                case 1:
                    return "警报";
                case 2:
                    return "更多";
            }
            return null;
        }
    }

    public interface OnTabChangeListener {
        public void onTabSelected(int position);
    }

}
