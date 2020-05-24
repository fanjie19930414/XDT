package com.kapok.apps.maple.xdt.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kapok.apps.maple.xdt.R;
import com.kotlin.baselibrary.activity.BaseActivity;
import com.luck.picture.lib.photoview.PhotoView;

import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * 图片展示
 */
@SuppressLint("SetTextI18n")
public class PhotoShowActivity extends BaseActivity {

    private List<String> urlList;
    private TextView tv_count;
    private int index;
    private boolean isUrlList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_show);
        isUrlList = getIntent().getBooleanExtra("isUrlList", false);
        if (isUrlList) urlList = (List<String>) getIntent().getSerializableExtra("showUrlList");
        index = getIntent().getIntExtra("index", 0);
        initView();
    }

    private void initView() {
        ViewPager viewPager = findViewById(R.id.viewPager);
        tv_count = findViewById(R.id.tv_count);
        viewPager.addOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                if (isUrlList) {
                    tv_count.setText(arg0 + 1 + "/" + urlList.size());
                }
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        viewPager.setAdapter(new MyAdapter(this));
        viewPager.setCurrentItem(index);
        if (isUrlList) {
            tv_count.setText(index + 1 + "/" + urlList.size());
        }
    }

    class MyAdapter extends PagerAdapter {

        private Context mContext;

        MyAdapter(Context context) {
            mContext = context;
        }

        @Override
        public int getCount() {
            int count;
            if (isUrlList)
                count = urlList == null ? 0 : urlList.size();
            else
                count = 0;
            return count;
        }

        @NotNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            PhotoView imageView = new PhotoView(mContext);
            if (isUrlList) {
                Glide.with(mContext).load(urlList.get(position)).into(imageView);
            }
            container.addView(imageView);
            return imageView;
        }

        @Override
        public boolean isViewFromObject(@NonNull View arg0, @NonNull Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            if (object instanceof View)
                container.removeView((View) object);
        }
    }
}
