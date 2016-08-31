package com.xiaochen.mobilesafe.receiver;

import com.xiaochen.mobilesafe.service.AlarmMusicService;
import com.xiaochen.mobilesafe.service.LocationService;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.SpUtils;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsMessage;

public class SmsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 看是否开启手机防盗
		boolean open = SpUtils.getBoolSp(context, ConstantValue.OPEN_SELF,
				false);
		
		//获取设备管理者对象
		DevicePolicyManager devicePolicyManager = (DevicePolicyManager) context.getSystemService(Context.DEVICE_POLICY_SERVICE);
		
		//创建一个组件对象   传进上下文环境和继承DeviceAdminReceiver广播的字节码文件
		ComponentName mDeviceAdminSample = new ComponentName(context, DeviceAdmin.class);
		
		if (open) {
			//获取短信内容  可能是多条短信  以数组的形式
			Object[] objects = (Object[]) intent.getExtras().get("pdus");
			for (Object object : objects) {
				//获取短信对象
				SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) object);
				//获取短信内容
				String body = smsMessage.getMessageBody();
				//获取短信发送者    并存起来
				String address = smsMessage.getOriginatingAddress();
				SpUtils.putStringSp(context, ConstantValue.SEND_SMS_PHONE_NUMBER, address);
				System.out.println(address);
				
				//判断短信中是否包含指定字符
				//播放报警音乐
				if(body.contains("#*alarm*#")){
					Intent intent2 = new Intent(context,AlarmMusicService.class);
					context.startService(intent2);
				}
				
				//GPS追踪
				if(body.contains("#*location*#")){
					Intent intent2 = new Intent(context,LocationService.class);
					context.startService(intent2);
				}
				
				//远程删除数据
				if(body.contains("#*wipedata*#")){
					//判断是否激活设备管理器
					if(devicePolicyManager.isAdminActive(mDeviceAdminSample)){
						devicePolicyManager.wipeData(0);
					}
				}
				
				//远程锁屏
				if(body.contains("#*lockscreen*#")){
					//判断是否激活设备管理器
					if(devicePolicyManager.isAdminActive(mDeviceAdminSample)){
						devicePolicyManager.lockNow();
						//获取存在sp中的锁屏密码
						String password = SpUtils.getStringSp(context, ConstantValue.REMOTE_LOCK_PASWORD, "");
						//设置密码
						devicePolicyManager.resetPassword(password, 0);
					}
				}
			}
		}
	}

}
