package com.xiaochen.mobilesafe.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewDebug.ExportedProperty;
import android.widget.TextView;

public class MarqueeTextView extends TextView {

	//通过java代码创建控件  new 对象
	public MarqueeTextView(Context context) {
		super(context);
	}

	//由系统调用(带属性+上下文环境构造方法)
	public MarqueeTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	//由系统调用(带属性+上下文环境+布局文件中定义样式文件构造方法)
	public MarqueeTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	//重写获取焦点的方法
	@Override
	@ExportedProperty(category = "focus")
	public boolean isFocused() {
		return true;
	}
}
