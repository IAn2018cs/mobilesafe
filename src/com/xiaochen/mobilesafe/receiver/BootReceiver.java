package com.xiaochen.mobilesafe.receiver;

import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		//获取重启后的SIM卡序列号
		TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		String simSerialNumber = manager.getSimSerialNumber();
		
		//获取存在sp中的SIM卡序列号
		String sim_number = SpUtils.getStringSp(context, ConstantValue.SIM_NUMBER, "");
		
		//两者进行比较  如果不一样就发送报警短信
		if(!simSerialNumber.equals(sim_number)){
			//获取sp中的安全号码
			String phone_number = SpUtils.getStringSp(context, ConstantValue.CONTACT_PHONE_NUMBER, null);
			SmsManager smsManager = SmsManager.getDefault();
			smsManager.sendTextMessage(phone_number, null, "检测到您另一个手机更换SIM卡", null, null);
		}
	}

}
