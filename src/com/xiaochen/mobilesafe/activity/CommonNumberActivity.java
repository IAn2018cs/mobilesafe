package com.xiaochen.mobilesafe.activity;

import java.util.List;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.engine.CommonNumberDao;
import com.xiaochen.mobilesafe.engine.CommonNumberDao.Child;
import com.xiaochen.mobilesafe.engine.CommonNumberDao.Group;
import com.xiaochen.mobilesafe.utlis.ShowDialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;

public class CommonNumberActivity extends StatusBarColorActivity {
	private ExpandableListView elv_common;
	private List<Group> mGroupList;
	private MyAdapter mAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_common_number);
		initUI();
		initDate();
	}

	private void initDate() {
		ShowDialog.showDialog(this);
		new Thread(){
			public void run() {
				// 获取获得组的集合
				mGroupList = CommonNumberDao.getGroup();
				
				runOnUiThread(new Runnable() {
					public void run() {
						// 创建数据适配器
						mAdapter = new MyAdapter();
						// 设置数据适配器
						elv_common.setAdapter(mAdapter);
						
						ShowDialog.dismiss();
					}
				});
			};
		}.start();
		
		// 设置点击事件
		elv_common.setOnChildClickListener(new OnChildClickListener() {
			@Override
			public boolean onChildClick(ExpandableListView parent, View v,
					int groupPosition, int childPosition, long id) {
				// 跳转到拨号界面
				String number = mAdapter.getChild(groupPosition, childPosition).number;
				Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+number));
				startActivity(intent);  
				return false;
			}
		});
	}

	private void initUI() {
		elv_common = (ExpandableListView) findViewById(R.id.elv_common);
	}
	
	// 数据适配器
	class MyAdapter extends BaseExpandableListAdapter{
		// 返回组的大小
		@Override
		public int getGroupCount() {
			return mGroupList.size();
		}

		// 返回组的孩子的大小
		@Override
		public int getChildrenCount(int groupPosition) {
			return mGroupList.get(groupPosition).childList.size();
		}

		// 返回组的对象
		@Override
		public Group getGroup(int groupPosition) {
			return mGroupList.get(groupPosition);
		}

		// 返回组的孩子的对象
		@Override
		public Child getChild(int groupPosition, int childPosition) {
			return mGroupList.get(groupPosition).childList.get(childPosition);
		}

		// 返回组的id
		@Override
		public long getGroupId(int groupPosition) {
			return groupPosition;
		}

		// 返回组的孩子的id
		@Override
		public long getChildId(int groupPosition, int childPosition) {
			return childPosition;
		}

		// 固定写法  不需要修改
		@Override
		public boolean hasStableIds() {
			return false;
		}

		// 获取组的view对象
		@Override
		public View getGroupView(int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// convertView的复用
			GroupHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.common_group_item, null);
				holder = new GroupHolder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder = (GroupHolder) convertView.getTag();
			}
			
			// 设置文字
			holder.tv_name.setText(getGroup(groupPosition).name);
			
			return convertView;
		}

		// 获取组的孩子的view对象
		@Override
		public View getChildView(int groupPosition, int childPosition,
				boolean isLastChild, View convertView, ViewGroup parent) {
			// convertView的复用
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.common_child_item, null);
				holder = new ViewHolder();
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				holder.tv_number = (TextView) convertView.findViewById(R.id.tv_number);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			
			// 设置文字
			holder.tv_name.setText(getChild(groupPosition, childPosition).name);
			holder.tv_number.setText(getChild(groupPosition, childPosition).number);
			
			return convertView;
		}

		// 孩子条目是否可以选中
		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			return true;
		}
		
	}
	
	static class ViewHolder{
		TextView tv_name;
		TextView tv_number;
	}
	static class GroupHolder{
		TextView tv_name;
	}
}
