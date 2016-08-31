package com.xiaochen.mobilesafe.view;

import com.xiaochen.mobilesafe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class SettingCheckItemView extends RelativeLayout {
	private static final String NAMESPACE = "http://schemas.android.com/apk/res/com.xiaochen.mobilesafe";
	private CheckBox cb;
	private TextView tv_dsc;
	private String destitle;
	private String desoff;
	private String deson;

	//使构造方法无论是执行哪个都走第三个(带三个参数的)
	
	public SettingCheckItemView(Context context) {
		this(context,null);
	}

	public SettingCheckItemView(Context context, AttributeSet attrs) {
		this(context, attrs,0);
	}

	public SettingCheckItemView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		//将xml转换成一个view对象    直接添加到了当前SettingItemView对应的view中
		View.inflate(context, R.layout.view_setting_item, this);
		
		//找到对应的控件
		TextView tv_title = (TextView) findViewById(R.id.tv_title);
		tv_dsc = (TextView) findViewById(R.id.tv_dsc);
		cb = (CheckBox) findViewById(R.id.cb);
		
		//在这个构造方法里对属性集合attrs操作
		initAttrs(attrs);
		
		tv_title.setText(destitle);
		tv_dsc.setText(desoff);
	}
	
	//对自定义的属性进行操作
	private void initAttrs(AttributeSet attrs) {
		/*//获取当前对象的属性个数
		int count = attrs.getAttributeCount();
		for(int i=0;i<count;i++){
			//获取属性名称
			attrs.getAttributeName(i);
			//获取属性值
			attrs.getAttributeValue(i);
		}*/
		
		destitle = attrs.getAttributeValue(NAMESPACE, "destitle");
		desoff = attrs.getAttributeValue(NAMESPACE, "desoff");
		deson = attrs.getAttributeValue(NAMESPACE, "deson");
		
		
	}

	//获取当前item的选中状态
	public boolean isCheck(){
		return cb.isChecked();
	}
	
	//设置item的选中状态
	public void setCheck(boolean check){
		cb.setChecked(check);
		if(check){
			tv_dsc.setText(deson);
		}else{
			tv_dsc.setText(desoff);
		}
	}

}
