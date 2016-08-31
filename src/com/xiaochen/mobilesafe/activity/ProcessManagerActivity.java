package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;
import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.domain.ProcessInfo;
import com.xiaochen.mobilesafe.engine.ProcessInfoProvider;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.ShowDialog;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ProcessManagerActivity extends StatusBarColorActivity implements OnClickListener {
	private ListView lv_processlist;
	private List<ProcessInfo> mProcessInfoList;
	private List<ProcessInfo> mProcessSystemList;
	private List<ProcessInfo> mProcessUserList;
	private MyAdapter mProcessAdapter;
	private TextView tv_title;
	private ProcessInfo mProcessInfo;
	private TextView tv_count;
	private TextView tv_use;
	private int mProcessCount = 0;
	private String mTotalMem;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_processmanager);
		// 初始化listview
		initList();
		// 初始化按钮
		initButton();
	}
	
	@Override
	protected void onResume() {
		// 可多次调用
		super.onResume();
		// 初始化内容栏(进程总数，可用内存)
		initTitle();
		// 初始化集合数据
		initDate();
	}
	
	private void initButton() {
		Button bt_all = (Button) findViewById(R.id.bt_all);
		Button bt_unall = (Button) findViewById(R.id.bt_unall);
		Button bt_clear = (Button) findViewById(R.id.bt_clear);
		Button bt_setting = (Button) findViewById(R.id.bt_setting);
		
		// 通过接口的方式设置点击事件
		bt_all.setOnClickListener(this);
		bt_unall.setOnClickListener(this);
		bt_clear.setOnClickListener(this);
		bt_setting.setOnClickListener(this);
	}

	private void initList() {
		lv_processlist = (ListView) findViewById(R.id.lv_processlist);
		// 常驻悬浮框的控件
		tv_title = (TextView) findViewById(R.id.tv_title);
		
		// 给listview设置滑动监听事件
		lv_processlist.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(mProcessUserList!=null && mProcessSystemList!=null){
					// 当第一个可见条目的索引大于用户进程集合的大小时  说明滑动到了系统进程里  就要去修改悬浮框的文字
					if(firstVisibleItem > mProcessUserList.size()){
						tv_title.setText("系统进程("+mProcessSystemList.size()+")");
					}else{
						tv_title.setText("用户进程("+mProcessUserList.size()+")");
					}
				}
			}
		});
		
		// 给listview设置点击事件
		lv_processlist.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 如果索引值对应的是悬浮框  就结束这个方法
				if(position == 0 || position == mProcessUserList.size()+1){
					return;
				}else{
					// 否则  得到相应的javabean对象 赋给mProcessInfo
					if(position < mProcessUserList.size()+1){
						mProcessInfo = mProcessUserList.get(position-1);
					}else{
						mProcessInfo = mProcessSystemList.get(position-mProcessUserList.size()-2);
					}
					// 如果mProcessInfo不为空 并且对应的javabean信息不是本应用
					if(mProcessInfo!=null){
						if(!mProcessInfo.getPackageName().equals("com.xiaochen.mobilesafe")){
							// 将checkbox的状态取反
							mProcessInfo.setCheck(!mProcessInfo.isCheck());
							CheckBox cb_process = (CheckBox) view.findViewById(R.id.cb_process);
							cb_process.setChecked(mProcessInfo.isCheck());
						}
					}
				}
			}
		});
	}

	private void initDate() {
		// 展示一个对话框
		ShowDialog.showDialog(this);
		// 开子线程去获取数据
		new Thread(){
			public void run() {
				// 获取正在运行的应用信息集合
				mProcessInfoList = ProcessInfoProvider.getProcessInfo(getApplicationContext());
				// 创建两个集合 用来存用户进程和系统进程
				mProcessSystemList = new ArrayList<ProcessInfo>();
				mProcessUserList = new ArrayList<ProcessInfo>();
				// 遍历mProcessInfoList
				for (ProcessInfo info : mProcessInfoList) {
					// 如果是系统进程就添加到系统集合中去   否则添加到用户集合中去
					if(info.isSystem()){
						mProcessSystemList.add(info);
					}else{
						mProcessUserList.add(info);
					}
				}
				// 在主线程上运行的
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 创建数据适配器对象  并设置给listview
						mProcessAdapter = new MyAdapter();
						lv_processlist.setAdapter(mProcessAdapter);
						// 关闭对话框
						ShowDialog.dismiss();
					}
				});
			};
		}.start();
	}
	
	private void initTitle() {
		tv_count = (TextView) findViewById(R.id.tv_count);
		tv_use = (TextView) findViewById(R.id.tv_use);
		// 获取运行的进程总数
		mProcessCount = ProcessInfoProvider.getProcessCount(getApplicationContext());
		tv_count.setText("进程总数:"+mProcessCount);
		// 将  可用内存  字节转换成存储单位(KB,MB,G)的字符串
		String availableMem = Formatter.formatFileSize(this, ProcessInfoProvider.getAvailable(getApplicationContext()));
		// 将  总内存  字节转换成单位存储单位(KB,MB,G)的字符串
		mTotalMem = Formatter.formatFileSize(this, ProcessInfoProvider.getTotalMemory(getApplicationContext()));
		tv_use.setText(availableMem+"可用/"+mTotalMem);
	}
	
	// 数据适配器
	class MyAdapter extends BaseAdapter{
		@Override
		public int getCount() {
			// 判断是否显示系统进程
			if(SpUtils.getBoolSp(getApplicationContext(), ConstantValue.PROCESS_SYSTEM_SHOW, false)){
				// 条目数量   用户进程集合的大小  加  系统进程集合的大小  加  两个悬浮框
				return mProcessUserList.size()+mProcessSystemList.size()+2;
			}else{
				// 条目数量   用户进程集合的大小  加  一个悬浮框
				return mProcessUserList.size()+1;
			}
		}
		
		// 条目的种类个数
		@Override
		public int getViewTypeCount() {
			return 2;
		}
		
		// 条目的类型
		@Override
		public int getItemViewType(int position) {
			if(position == 0 || position == mProcessUserList.size()+1){
				return 0;
			}else{
				return 1;
			}
		}

		@Override
		public ProcessInfo getItem(int position) {
			if(position == 0 || position == mProcessUserList.size()+1){
				return null;
			}else{
				if(position < mProcessUserList.size()+1){
					return mProcessUserList.get(position-1);
				}else{
					return mProcessSystemList.get(position-mProcessUserList.size()-2);
				}
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// 根据不同条目的类型  去做处理
			int type = getItemViewType(position);
			switch (type) {
			// 悬浮框类型
			case 0:
				TitleHolder titleHolder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.appinfo_title_item, null);
					titleHolder = new TitleHolder();
					titleHolder.tv_title = (TextView) convertView.findViewById(R.id.tv_title);
					convertView.setTag(titleHolder);
				}else{
					titleHolder = (TitleHolder) convertView.getTag();
				}
				if(position == 0){
					titleHolder.tv_title.setText("用户进程("+mProcessUserList.size()+")");
				}else{
					titleHolder.tv_title.setText("系统进程("+mProcessSystemList.size()+")");
				}
				
				break;
			// 进程信息类型
			case 1:
				ViewHolder holder = null;
				if(convertView == null){
					convertView = View.inflate(getApplicationContext(), R.layout.process_appinfo_item, null);
					holder = new ViewHolder();
					holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
					holder.tv_usemem = (TextView) convertView.findViewById(R.id.tv_usemem);
					holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
					holder.cb_process = (CheckBox) convertView.findViewById(R.id.cb_process);
					convertView.setTag(holder);
				}else{
					holder = (ViewHolder) convertView.getTag();
				}
				ProcessInfo processInfo = getItem(position);
				if(processInfo!=null){
					holder.tv_name.setText(processInfo.getName());
					// 将总内存字节转换成单位为G的字符串
					String usemem = Formatter.formatFileSize(getApplicationContext(), processInfo.getOccupyMemory());
					holder.tv_usemem.setText("占用内存:"+usemem);
					holder.iv_icon.setImageDrawable(processInfo.getIcon());
					// 如果是本应用  就把checkbox隐藏掉
					if(processInfo.getPackageName().equals("com.xiaochen.mobilesafe")){
						holder.cb_process.setVisibility(View.GONE);
					}else{
						holder.cb_process.setVisibility(View.VISIBLE);
					}
					// 设置checkbox的选中状态
					holder.cb_process.setChecked(processInfo.isCheck());
				}
				
				break;
			}
			
			return convertView;
		}
	}
	
	static class ViewHolder{
		TextView tv_name;
		TextView tv_usemem;
		ImageView iv_icon;
		CheckBox cb_process;
	}
	
	static class TitleHolder{
		TextView tv_title;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// 全选
		case R.id.bt_all:
			setAll();
			break;
		// 反选
		case R.id.bt_unall:
			unSetCheck();
			break;
		// 一键清理
		case R.id.bt_clear:
			clearMem();
			break;
		// 设置
		case R.id.bt_setting:
			setting();
			break;
		}
	}

	private void setting() {
		startActivity(new Intent(this, ProcessSettingActivity.class));
	}

	private void clearMem() {
		// 创建一个集合  用来存储要清理的javabean对象ProcessInfo
		List<ProcessInfo> clearList = new ArrayList<ProcessInfo>();
		
		// 遍历用户进程集合
		for (ProcessInfo info : mProcessUserList) {
			// 如果是本应用就跳出本次循环
			if(info.getPackageName().equals("com.xiaochen.mobilesafe")){
				continue;
			}
			// 如果是选中状态  就添加到要清理集合中
			if(info.isCheck()){
				clearList.add(info);
			}
		}
		// 遍历系统进程集合
		for (ProcessInfo info : mProcessSystemList) {
			// 如果是选中状态  就添加到要清理集合中
			if(info.isCheck()){
				clearList.add(info);
			}
		}
		
		// 释放的内存大小(单位byte)
		long clearMem = 0;
		
		// 遍历要清理集合
		for (ProcessInfo processInfo : clearList) {
			// 相应集合中如果包含对应的javabean对象   就从相应集合中移除
			if(mProcessUserList.contains(processInfo)){
				mProcessUserList.remove(processInfo);
			}
			if(mProcessSystemList.contains(processInfo)){
				mProcessSystemList.remove(processInfo);
			}
			// 获得对应进程占用内存的大小  并累加到释放的内存中
			clearMem += processInfo.getOccupyMemory();
			// 调用杀死后台进程的方法   将对应的javabean对象传进去
			ProcessInfoProvider.clearMem(getApplicationContext(), processInfo);
		}
		// 通知数据适配器更新
		if(mProcessAdapter!=null){
			mProcessAdapter.notifyDataSetChanged();
		}
		
		// 更新进程总数:进程总数减去要清理集合的大小
		mProcessCount -= clearList.size();
		// 更新可用内存大小:获得可用内存大小 加上  释放的内存大小
		long available = ProcessInfoProvider.getAvailable(getApplicationContext())+clearMem;
		// 将  当前可用内存available  字节转换成存储单位(KB,MB,G)的字符串
		String strAvailable = Formatter.formatFileSize(getApplicationContext(), available);
		// 更新内容栏(进程总数，可用内存)信息
		tv_count.setText("进程总数:"+mProcessCount);
		tv_use.setText(strAvailable+"可用/"+mTotalMem);

		if(clearList.size()>0){
			// 弹出吐司
			String strClearMem = Formatter.formatFileSize(getApplicationContext(), clearMem);
			ToastUtli.show(getApplicationContext(), "清理了"+clearList.size()+"个进程，释放了"+strClearMem+"内存");
		}else{
			ToastUtli.show(getApplicationContext(), "您没有选择要清理的进程");
		}
	}

	private void unSetCheck() {
		for (ProcessInfo info : mProcessUserList) {
			// 如果是本应用就跳出本次循环
			if(info.getPackageName().equals("com.xiaochen.mobilesafe")){
				continue;
			}
			// 将javabean对象的选中状态取反设置
			info.setCheck(!info.isCheck());
		}
		for (ProcessInfo info : mProcessSystemList) {
			// 将javabean对象的选中状态取反设置
			info.setCheck(!info.isCheck());
		}
		// 通知数据适配器刷新
		if(mProcessAdapter!=null){
			mProcessAdapter.notifyDataSetChanged();
		}
	}

	private void setAll() {
		for (ProcessInfo info : mProcessUserList) {
			// 如果是本应用就跳出本次循环
			if(info.getPackageName().equals("com.xiaochen.mobilesafe")){
				continue;
			}
			// 将javabean对象的选中状态设置为选中
			info.setCheck(true);
		}
		for (ProcessInfo info : mProcessSystemList) {
			// 将javabean对象的选中状态设置为选中
			info.setCheck(true);
		}
		// 通知数据适配器刷新
		if(mProcessAdapter!=null){
			mProcessAdapter.notifyDataSetChanged();
		}
	}
}
