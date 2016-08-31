package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import com.xiaochen.mobilesafe.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import com.xiaochen.mobilesafe.utlis.ViewPagerCompat;
import com.xiaochen.mobilesafe.utlis.DepthPageTransformer;

public class HelloActivity extends Activity {
	private RelativeLayout viewpager_3;
	private LinearLayout viewpager_1;
	private LinearLayout viewpager_2;
	private List<View> mViews = new ArrayList<View>();
	private PagerAdapter mAdapter;
	private ViewPagerCompat mViewPager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_hello);
		viewpager_1 = (LinearLayout) View.inflate(getApplicationContext(),
				R.layout.viewpager_1, null).findViewById(R.id.viewpager_1);
		viewpager_2 = (LinearLayout) View.inflate(getApplicationContext(),
				R.layout.viewpager_2, null).findViewById(R.id.viewpager_2);
		viewpager_3 = (RelativeLayout) View.inflate(getApplicationContext(),
				R.layout.viewpager_3, null).findViewById(R.id.viewpager_3);

		// 为按钮设置点击事件
		viewpager_3.findViewById(R.id.bt_in).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(HelloActivity.this,SplashActivity.class);
				startActivity(intent);
				finish();
			}
		});
		

		mViewPager = (ViewPagerCompat) findViewById(R.id.viewpager);
		mViewPager.setPageTransformer(true, new DepthPageTransformer());
		initPageAdapter();

	}

	private void initPageAdapter() {
		mViews.add(viewpager_1);
		mViews.add(viewpager_2);
		mViews.add(viewpager_3);

		mAdapter = new PagerAdapter() {
			@Override
			public void destroyItem(ViewGroup container, int position,
					Object object) {
				container.removeView(mViews.get(position));
			}

			@Override
			public Object instantiateItem(ViewGroup container, int position) {
				View view = mViews.get(position);
				container.addView(view);
				return view;
			}

			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}

			@Override
			public int getCount() {
				return mViews.size();
			}
		};

		mViewPager.setAdapter(mAdapter);

	}
}
