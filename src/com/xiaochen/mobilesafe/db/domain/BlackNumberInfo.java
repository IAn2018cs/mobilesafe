package com.xiaochen.mobilesafe.db.domain;

public class BlackNumberInfo {
	private String phone;
	private int mode;

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	@Override
	public String toString() {
		return "BlackNumberInfo [phone=" + phone + ", mode=" + mode + "]";
	}

}
