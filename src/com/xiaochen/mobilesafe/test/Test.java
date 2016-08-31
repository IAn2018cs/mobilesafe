package com.xiaochen.mobilesafe.test;

import com.xiaochen.mobilesafe.db.dao.BlackNumberDao;
import com.xiaochen.mobilesafe.engine.AppInfoProvider;

import android.test.AndroidTestCase;

public class Test extends AndroidTestCase {
	public void insert(){
		
		BlackNumberDao blackNumberDao = BlackNumberDao.getInstance(getContext());
		for(int i=0;i<100;i++){
			blackNumberDao.insert("15511112"+i, 1);
		}
	}
	
	public void appInfo(){
		AppInfoProvider.getAppInfo(getContext());
	}
}
