package com.accenture.dansmarue.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.accenture.dansmarue.R;
import com.accenture.dansmarue.di.components.DaggerPresenterComponent;
import com.accenture.dansmarue.di.modules.PresenterModule;
import com.accenture.dansmarue.mvp.presenters.SliderPresenter;
import com.accenture.dansmarue.mvp.views.SliderView;

import javax.inject.Inject;

import butterknife.BindView;

public class SliderActivity extends BaseActivity implements SliderView {


    @BindView(value = R.id.layoutDots)
    protected LinearLayout dotsLayout;
    @BindView(value = R.id.btn_begin)
    protected Button buttonBegin;
    @BindView(value = R.id.view_pager)
    protected ViewPager viewPager;

    private int[] layouts;

    @Inject
    protected SliderPresenter presenter;


    @Override
    protected void onViewReady(Bundle savedInstanceState, Intent intent) {
        super.onViewReady(savedInstanceState, intent);

        // layouts of all welcome sliders
        layouts = new int[]{
                R.layout.slider_page1_layout,
                R.layout.slider_page2_layout,
                R.layout.slider_page3_layout,
        };

        // adding bottom dots
        addBottomDots(0);

        ViewPagerAdapterForSlider myViewPagerAdapter = new ViewPagerAdapterForSlider();
        assert viewPager != null;
        viewPager.setAdapter(myViewPagerAdapter);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);

        buttonBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onBeginButtonPressed();
            }
        });


    }


    @Override
    public void launchRuntimeGeolocPermissionRequestActivity() {
        final Intent i = new Intent(SliderActivity.this, RuntimeGeolocPermissionRequestActivity.class);
        startActivity(i);
        // close this activity
        finish();
    }

    @Override
    protected void resolveDaggerDependency() {
        DaggerPresenterComponent.builder()
                .applicationComponent(getApplicationComponent())
                .presenterModule(new PresenterModule(this))
                .build()
                .inject(this);
    }

    @Override
    protected int getContentView() {
        return R.layout.slider_activity_layout;
    }


    // Add dots dynamically and change its size in function of screen selection
    private void addBottomDots(int currentPage) {

        final TextView[] dots = new TextView[layouts.length];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("&#8226;"));
            dots[i].setTextSize(20);
            dots[i].setTextColor(Color.WHITE);
            dotsLayout.addView(dots[i]);
        }

        dots[currentPage].setTextSize(45);

    }

    // Viewpager change listener
    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
        }

        @Override
        public void onPageScrollStateChanged(int arg0) {
        }
    };


    /**
     * View pager adapter
     */
    public class ViewPagerAdapterForSlider extends PagerAdapter {
        private LayoutInflater layoutInflater;

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(layouts[position], container, false);
            container.addView(view);
            return view;
        }

        @Override
        public int getCount() {
            return layouts.length;
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
    }


}
