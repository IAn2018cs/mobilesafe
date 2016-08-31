package com.xiaochen.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class ProcessInfo {
	private String packageName;
	private String name;
	private Drawable icon;
	private long occupyMemory;
	private boolean isSystem;
	private boolean isCheck;
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Drawable getIcon() {
		return icon;
	}
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
	public long getOccupyMemory() {
		return occupyMemory;
	}
	public void setOccupyMemory(long occupyMemory) {
		this.occupyMemory = occupyMemory;
	}
	public boolean isSystem() {
		return isSystem;
	}
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	public boolean isCheck() {
		return isCheck;
	}
	public void setCheck(boolean isCheck) {
		this.isCheck = isCheck;
	}
}
