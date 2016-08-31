package com.xiaochen.mobilesafe.view;

import com.xiaochen.mobilesafe.R;
import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingClickItemView extends RelativeLayout {
	private TextView tv_dsc;
	private TextView tv_title;

	//使构造方法无论是执行哪个都走第三个(带三个参数的)
	
	public SettingClickItemView(Context context) {
		this(context,null);
	}

	public SettingClickItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SettingClickItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//将xml转换成一个view对象    直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.view_setting_click_item, this);
		
		//找到对应的控件
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_dsc = (TextView) findViewById(R.id.tv_dsc);
	}
	
	public void setTitle(String titleName){
		tv_title.setText(titleName);
	}
	
	public void setDsc(String dsc){
		tv_dsc.setText(dsc);
	}
}
