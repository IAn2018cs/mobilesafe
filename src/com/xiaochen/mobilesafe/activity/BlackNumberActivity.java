package com.xiaochen.mobilesafe.activity;

import java.util.List;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.dao.BlackNumberDao;
import com.xiaochen.mobilesafe.db.domain.BlackNumberInfo;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

public class BlackNumberActivity extends StatusBarColorActivity {
	private ImageView iv_addblack;
	private ListView lv_blacknumber;
	private BlackNumberDao mDao;
	private List<BlackNumberInfo> mBlackNumberList;
	private MyAdapter adapter;
	private int mode = 1;
	private boolean isLoad = false;
	private int mCount = 0;
	private TranslateAnimation mTranslateAnim;
	
	private Handler mHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(adapter == null){
				// 给listview设置数据适配器
				adapter = new MyAdapter();
				lv_blacknumber.setAdapter(adapter);
			}else{
				// 通知数据适配器去刷新数据
				adapter.notifyDataSetChanged();
				isLoad = false;
			}
			
		};
	};
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_number);
		initUI();
		initDate();
		initAnim();
	}

	private void initAnim() {
		mTranslateAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		mTranslateAnim.setDuration(400);
	}

	// 为数据数据适配器初始化数据
	private void initDate() {
		// 需要遍历数据库  耗时操作 开子线程
		new Thread(){
			public void run() {
				// 查询数据库中的所有数据   结果保存到集合中
				mDao = BlackNumberDao.getInstance(getApplicationContext());
				mBlackNumberList = mDao.queryLimit(0);
				// 获取数据库中的数据总个数
				mCount = mDao.getCount();
				// 发送一条空消息  告诉ui数据已经查询完毕
				mHandler.sendEmptyMessage(0);
			};
		}.start();
	}

	private void initUI() {
		iv_addblack = (ImageView) findViewById(R.id.iv_addblack);
		lv_blacknumber = (ListView) findViewById(R.id.lv_blacknumber);
		
		// 给添加按钮 添加点击事件
		iv_addblack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 展示一个对话框
				showDialog();
			}
		});
		
		// 给listview设置滚动监听事件
		lv_blacknumber.setOnScrollListener(new OnScrollListener() {
			// 当滚动状态改变时调用
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				/*OnScrollListener.SCROLL_STATE_FLING  快速滚动的时候
				OnScrollListener.SCROLL_STATE_IDLE   空闲的时候  (没有滚动)
				OnScrollListener.SCROLL_STATE_TOUCH_SCROLL  手指按住屏幕滚动的时候*/
				// 如果当前不能滑动并且到了集合的最后一个条目(其索引值>=集合个数-1)，没有加载数据   这时候才能去加载数据
				// 如果mBlackNumberList不为空 并且数据库中数据的总个数>集合的大小  才能去加载更多数据
				if(mBlackNumberList != null && mCount > mBlackNumberList.size()){
					if(scrollState == OnScrollListener.SCROLL_STATE_IDLE
							&& lv_blacknumber.getLastVisiblePosition() >= mBlackNumberList.size()-1
							&& !isLoad){
						isLoad = true;
						// 开一个子线程  去加载另外20条数据
						new Thread(){
							public void run() {
								// 查询数据库中的所有数据   结果保存到集合中
								mDao = BlackNumberDao.getInstance(getApplicationContext());
								List<BlackNumberInfo> queryLimitList = mDao.queryLimit(mBlackNumberList.size());
								
								// 将查询完的集合添加到mBlackNumberList中去
								mBlackNumberList.addAll(queryLimitList);
								// 发送一条空消息  告诉adapter去刷新数据
								mHandler.sendEmptyMessage(0);
							};
						}.start();
					}
				}
			}
			
			// 滚动的时候调用
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				
			}
		});
	}
	
	protected void showDialog() {
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();
		View view = View.inflate(getApplicationContext(), R.layout.dialog_blacknumber_add, null);
		dialog.setView(view, 0, 0, 0, 0);
		
		final EditText et_phone = (EditText) view.findViewById(R.id.et_phone);
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		RadioGroup rg_group = (RadioGroup) view.findViewById(R.id.rg_group);
		
		// 监听RadioGroup的选中条目改变
		rg_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				switch (checkedId) {
				case R.id.rb_sms:
					mode = 1;
					break;
				case R.id.rb_phone:
					mode = 2;
					break;
				case R.id.rb_all:
					mode = 3;
					break;
				}
			}
		});
		
		// 确认的按钮点击事件
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String phone = et_phone.getText().toString().trim();
				// 如果内容不为空
				if(!TextUtils.isEmpty(phone)){
					// 1.在数据库插入一条数据
					mDao.insert(phone, mode);
					// 2.在集合中添加一个javabeen对象   位置在集合的最顶端  索引值为0
					BlackNumberInfo blackNumberInfo = new BlackNumberInfo();
					blackNumberInfo.setPhone(phone);
					blackNumberInfo.setMode(mode);
					mBlackNumberList.add(0, blackNumberInfo);
					// 3.如果数据适配器不为空  
					if(adapter!=null){
						// 通知数据适配器刷新数据
						adapter.notifyDataSetChanged();
					}
					// 隐藏对话框
					dialog.dismiss();
				}else{
					ToastUtli.show(getApplicationContext(), "请输入要拦截的电话号码");
				}
			}
		});
		
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
		
		dialog.show();
	}

	// 数据适配器
	class MyAdapter extends BaseAdapter{

		@Override
		public int getCount() {
			return mBlackNumberList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBlackNumberList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			// 对listview的优化   复用convertView
			// 减少findViewById的次数  用ViewHolder类 来存储findViewById的值
			ViewHolder viewHolder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.view_balcknumber_item, null);
				
				viewHolder = new ViewHolder();
				viewHolder.tv_phone = (TextView) convertView.findViewById(R.id.tv_phone);
				viewHolder.tv_mode = (TextView) convertView.findViewById(R.id.tv_mode);
				viewHolder.iv_delete = (ImageView) convertView.findViewById(R.id.iv_delete);
				
				// 将存有findViewById的viewHolder存到convertView中
				convertView.setTag(viewHolder);
			}else{
				// 复用convertView里存的viewHolder
				viewHolder = (ViewHolder) convertView.getTag();
			}
			
			final String phone = mBlackNumberList.get(position).getPhone();
			mode = mBlackNumberList.get(position).getMode();
			
			viewHolder.tv_phone.setText(phone);
			switch (mode) {
			case 1:
				viewHolder.tv_mode.setText("拦截短信");
				break;
			case 2:
				viewHolder.tv_mode.setText("拦截电话");
				break;
			case 3:
				viewHolder.tv_mode.setText("拦截所有");
				break;
			}
			
			final View animView = convertView;
			
			// 删除的点击事件
			viewHolder.iv_delete.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					// 设置一个平移动画  当动画执行完再删除数据
					animView.startAnimation(mTranslateAnim);
					mTranslateAnim.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							// 1.删除数据库中的一条数据
							mDao.delete(phone);
							// 2.删除集合中的一条数据
							mBlackNumberList.remove(position);
							// 3.通知数据适配器刷新数据
							if(adapter!=null){
								adapter.notifyDataSetChanged();
							}
						}
					});
				}
			});
			
			return convertView;
		}
	}
	
	// 用来复用findViewById  加静态可以使其只创建一次
	static class ViewHolder{
		TextView tv_phone;
		TextView tv_mode;
		ImageView iv_delete;
	}
}
