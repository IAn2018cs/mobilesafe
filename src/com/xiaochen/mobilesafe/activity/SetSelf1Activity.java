package com.xiaochen.mobilesafe.activity;

import com.xiaochen.mobilesafe.R;
import android.content.Intent;
import android.os.Bundle;

public class SetSelf1Activity extends SetSelfPagerActivity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_setself1);
	}

	@Override
	public void showPrePage() {
		//由于没有上一页   所以这里为空的
	}

	@Override
	public void showNextPage() {
		Intent intent = new Intent(this, SetSelf2Activity.class);
		startActivity(intent);
		finish();
		// 设置进入 退出 平移动画
		overridePendingTransition(R.anim.next_in_anim, R.anim.next_out_anim);
	}
}
