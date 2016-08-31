package com.xiaochen.mobilesafe.activity;

import com.thinkland.sdk.android.JuheData;
import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.AddressDao;
import com.xiaochen.mobilesafe.utlis.ToastUtli;
import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**电话归属地查询
 */
public class MobileNoTrack extends StatusBarColorActivity {
	private EditText et_address_phone;
	private TextView tv_result_address;
	private Button bt_address;
	private Context mThis = this;
	private String address = "";
	
	/*private Handler mHandler = new Handler(){
		public void handleMessage(Message msg) {
			tv_result_address.setText(address);
			ShowDialog.dismiss();
		};
	};*/
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mobilenotrack);
		initUI();
	}
	
	private void initUI() {
		et_address_phone = (EditText) findViewById(R.id.et_address_phone);
		tv_result_address = (TextView) findViewById(R.id.tv_result_address);
		bt_address = (Button) findViewById(R.id.bt_address);
		// 给按钮设置点击事件
		bt_address.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_address_phone.getText().toString().trim();
				phone = phone.replace(" ", "").replace("+86", "").replace("-", "");
				if(!TextUtils.isEmpty(phone)){
					// 查询归属地的方法
					address = AddressDao.getAddress(phone, mThis);
					tv_result_address.setText(address);
				}else{
					// 抖动动画
					Animation animation = AnimationUtils.loadAnimation(mThis, R.anim.shake);
					et_address_phone.startAnimation(animation);
					// 手机震动
					Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
					// 震动时间
					vibrator.vibrate(900);
					// 设置有规律的震动  long数组里分别是 不震动时间  震动时间。。。  第二个参数是重复的次数   -1为不重复
//					vibrator.vibrate(new long[]{}, -1);
					ToastUtli.show(getApplicationContext(), "手机号码不能为空");
				}
			}
		});
		
		// 实时查询  给TextView设置文字改变的监听
		et_address_phone.addTextChangedListener(new TextWatcher() {
			// 变化时候
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			// 变化前
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			
			// 变化后
			@Override
			public void afterTextChanged(Editable s) {
				String phone = et_address_phone.getText().toString().trim();
				phone = phone.replace(" ", "").replace("+86", "").replace("-", "");
				if(!TextUtils.isEmpty(phone)){
					// 查询归属地的方法
					address = AddressDao.getAddress(phone, mThis);
					tv_result_address.setText(address);
				}else{
					tv_result_address.setText("");
				}
			}
		});
	}
	
	/*// 查询归属地的方法   耗时操作 放在子线程里
	protected void queryAddress(final String phone) {
		ShowDialog.showDialog(mThis);
		new Thread(){
			public void run() {
				address = AddressDao.getAddress(phone, mThis);
				mHandler.sendEmptyMessage(0);
			};
		}.start();
		
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		JuheData.cancelRequests(this);
	}
}
