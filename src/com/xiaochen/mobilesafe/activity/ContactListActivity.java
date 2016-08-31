package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.xiaochen.mobilesafe.R;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ContactListActivity extends StatusBarColorActivity {
	private ListView lv_contact;
	private List<HashMap<String, String>> mContacelist = new ArrayList<HashMap<String, String>>();
	private MyAdapter adapter;
	private AlertDialog dialog;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			adapter = new MyAdapter();
			lv_contact.setAdapter(adapter);
			if (mContacelist.size() > 0) {
				dialog.dismiss();
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contactlist);

		initUI();
		initData();
		showDialog();
	}

	private void showDialog() {
		// 自定义对话框 要用dialog.setView(view); 来设置
		Builder builder = new AlertDialog.Builder(this);
		dialog = builder.create();

		// 将一个xml转换成一个view对象
		View view = View.inflate(this, R.layout.dialog_progress, null);
		// 兼容低版本 去掉对话框的内边距
		dialog.setView(view, 0, 0, 0, 0);
		// dialog.setView(view);
		dialog.show();
	}

	private void initData() {
		new Thread() {
			public void run() {
				// 获取内容解析器对象 调它的查询方法 几个参数分别为uri地址 查询的内容 根据什么去查 根据查的具体值 排序方式
				Cursor cursor = getContentResolver()
						.query(Uri
								.parse("content://com.android.contacts/raw_contacts"),
								new String[] { "contact_id" }, null, null, null);
				mContacelist.clear();
				// 循环游标 直至没有数据
				while (cursor.moveToNext()) {
					String id = cursor.getString(0);
					Cursor indexCursor = getContentResolver().query(
							Uri.parse("content://com.android.contacts/data"),
							new String[] { "data1", "mimetype" },
							"raw_contact_id=?", new String[] { id }, null);
					HashMap<String, String> hashMap = new HashMap<String, String>();
					// 循环游标
					while (indexCursor.moveToNext()) {
						String data = indexCursor.getString(0);
						String type = indexCursor.getString(1);
						if ("vnd.android.cursor.item/name".equals(type)) {
							String name = data;
							if (name != null) {
								hashMap.put("name", name);
							}
						} else if ("vnd.android.cursor.item/phone_v2"
								.equals(type)) {
							String phone = data;
							if (phone != null) {
								hashMap.put("phone", phone);
							}
						}
					}
					indexCursor.close();
					mContacelist.add(hashMap);
				}
				cursor.close();
				// 发送一个空消息
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		lv_contact = (ListView) findViewById(R.id.lv_contact);
		// 设置每个条目的点击事件
		lv_contact.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (adapter != null) {
					// 获取索引指向集合中的对象
					HashMap<String, String> hashMap = adapter.getItem(position);
					// 通过intent将数据回传给开启者
					Intent intent = new Intent();
					intent.putExtra("phone", hashMap.get("phone"));
					setResult(0, intent);

					finish();
				}
			}
		});
	}

	class MyAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mContacelist.size();
		}

		@Override
		public HashMap<String, String> getItem(int position) {
			return mContacelist.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.contact_item, null);
			} else {
				view = convertView;
			}
			TextView tv_name = (TextView) view.findViewById(R.id.tv_name);
			TextView tv_phone = (TextView) view.findViewById(R.id.tv_phone);
			// 根据索引获得相应的hashMap 进而获得需要的姓名和电话
			HashMap<String, String> hashMap = getItem(position);
			tv_name.setText(hashMap.get("name"));
			tv_phone.setText(hashMap.get("phone"));
			return view;
		}

	}
}
