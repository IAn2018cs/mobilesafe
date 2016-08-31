package com.xiaochen.mobilesafe.db.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private String packageName;
	private String name;
	private long cacheSize;
	private Drawable icon;
	private boolean isSystem;
	private boolean isSdCard;
	
	/**
	 * @return 应用缓存大小
	 */
	public long getCacheSize() {
		return cacheSize;
	}
	/**设置应用缓存大小
	 * @param cacheSize
	 */
	public void setCacheSize(long cacheSize) {
		this.cacheSize = cacheSize;
	}
	/**
	 * @return 是否是系统应用
	 */
	public boolean isSystem() {
		return isSystem;
	}
	/**设置是否是系统应用
	 * @param isSystem
	 */
	public void setSystem(boolean isSystem) {
		this.isSystem = isSystem;
	}
	/**
	 * @return 是否存安装在sd卡
	 */
	public boolean isSdCard() {
		return isSdCard;
	}
	/**设置是否存安装在sd卡
	 * @param isSdCard
	 */
	public void setSdCard(boolean isSdCard) {
		this.isSdCard = isSdCard;
	}
	/**
	 * @return 应用包名
	 */
	public String getPackageName() {
		return packageName;
	}
	/**设置应用包名
	 * @param packageName
	 */
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
	/**
	 * @return 应用名称
	 */
	public String getName() {
		return name;
	}
	/**设置应用名称
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}
	/**
	 * @return 应用图标
	 */
	public Drawable getIcon() {
		return icon;
	}
	/**设置应用图标
	 * @param icon
	 */
	public void setIcon(Drawable icon) {
		this.icon = icon;
	}
}
