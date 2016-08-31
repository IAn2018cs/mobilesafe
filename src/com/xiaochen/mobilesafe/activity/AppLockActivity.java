package com.xiaochen.mobilesafe.activity;

import java.util.ArrayList;
import java.util.List;

import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.dao.AppLockDao;
import com.xiaochen.mobilesafe.db.domain.AppInfo;
import com.xiaochen.mobilesafe.engine.AppInfoProvider;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.ShowDialog;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AppLockActivity extends StatusBarColorActivity {
	private TextView tv_lock, tv_unlock;
	private ListView lv_lock, lv_unlock;
	private AppLockDao mDao;
	private List<AppInfo> mAppInfoList;
	private List<AppInfo> mLockList;
	private List<AppInfo> mUnlockList;
	private MyAdapter mLockAdapter;
	private MyAdapter mUnLockAdapter;
	private TranslateAnimation mUnLockTranslateAnim, mLockTranslateAnim;
	float x1 = 0;
	float x2 = 0;
	float y1 = 0;
	float y2 = 0;
	private ImageView iv_setpwd;
	private TranslateAnimation mOutAnimation, mInAnimation;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_applock);
		initUI();
		// 初始化数据
		initDate();
		// 初始化平移动画
		initAnimation();
		// 设置listView的触摸事件
		initOnTouch();
		// 设置一个平移动画
		initAnim();
		// 监听listview的滑动事件
		initScrollListener();
	}

	private void initAnim() {
		// 滑出动画
		mOutAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1);
		mOutAnimation.setDuration(500);
		// 滑入动画
		mInAnimation = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0);
		mInAnimation.setDuration(500);
	}

	private void initScrollListener() {
		lv_lock.setOnScrollListener(new OnScrollListener() {
			private boolean isFling = false;
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					iv_setpwd.setVisibility(View.VISIBLE);
					if(isFling){
						iv_setpwd.startAnimation(mInAnimation);
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					isFling = true;
					iv_setpwd.startAnimation(mOutAnimation);
					mOutAnimation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							iv_setpwd.setVisibility(View.GONE);
						}
					});
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isFling = false;
					break;
				}
			}
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		lv_unlock.setOnScrollListener(new OnScrollListener() {
			private boolean isFling = false;
			@Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
				switch (scrollState) {
				case OnScrollListener.SCROLL_STATE_IDLE:
					iv_setpwd.setVisibility(View.VISIBLE);
					if(isFling){
						iv_setpwd.startAnimation(mInAnimation);
					}
					break;
				case OnScrollListener.SCROLL_STATE_FLING:
					isFling = true;
					iv_setpwd.startAnimation(mOutAnimation);
					mOutAnimation.setAnimationListener(new AnimationListener() {
						@Override
						public void onAnimationStart(Animation animation) {
						}
						@Override
						public void onAnimationRepeat(Animation animation) {
						}
						@Override
						public void onAnimationEnd(Animation animation) {
							iv_setpwd.setVisibility(View.GONE);
						}
					});
					break;
				case OnScrollListener.SCROLL_STATE_TOUCH_SCROLL:
					isFling = false;
					break;
                }
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                    int visibleItemCount, int totalItemCount) {
            }
		});
	}

	private void initAnimation() {
		// 未加锁界面的平移动画    向右滑动 滑出屏幕
		mUnLockTranslateAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 1,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		mUnLockTranslateAnim.setDuration(400);
		
		// 已加锁界面的平移动画   向左滑动  滑出屏幕
		mLockTranslateAnim = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, -1,
				Animation.RELATIVE_TO_SELF, 0,
				Animation.RELATIVE_TO_SELF, 0);
		mLockTranslateAnim.setDuration(400);
	}

	private void initDate() {
		ShowDialog.showDialog(this);
		new Thread(){
			public void run() {
				// 获取包管理者  用来判断应用是否可以启动
				PackageManager pm = getPackageManager();
				// 获取所有应用信息
				mAppInfoList = AppInfoProvider.getAppInfo(getApplicationContext());
				// 操作数据库  获取所有加锁应用的包名
				mDao = AppLockDao.getInstance(getApplicationContext());
				List<String> mAllLockList = mDao.queryAll();
				// 创建加锁和未加锁应用集合
				mLockList = new ArrayList<AppInfo>();
				mUnlockList = new ArrayList<AppInfo>();
				// 遍历所有应用集合
				for (AppInfo appInfo : mAppInfoList) {
					// 通过包管理者获取开启这个应用的Intent  通过看Intent是否为空来判断该应用能否启动
					Intent launchIntent = pm.getLaunchIntentForPackage(appInfo.getPackageName());
					// 如果可以启动  就添加数据
					if(launchIntent != null){
						// 如果加锁应用集合中包含 对应应用的包名  就添加到加锁应用集合中
						if(mAllLockList.contains(appInfo.getPackageName())){
							mLockList.add(appInfo);
						}else{
							mUnlockList.add(appInfo);
						}
					}
				}
				
				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						// 加锁应用的数据适配器
						mLockAdapter = new MyAdapter(true);
						lv_lock.setAdapter(mLockAdapter);
						// 未加锁应用的数据适配器
						mUnLockAdapter = new MyAdapter(false);
						lv_unlock.setAdapter(mUnLockAdapter);
						// 设置按钮TextView的文字
						tv_lock.setText("已加锁应用("+mLockList.size()+")");
						tv_unlock.setText("未加锁应用("+mUnlockList.size()+")");
						
						ShowDialog.dismiss();
					}
				});
			};
		}.start();
	}
	
	class MyAdapter extends BaseAdapter{
		private boolean isLock;
		
		/**
		 * @param isLock 是否加锁
		 */
		public MyAdapter(boolean isLock){
			this.isLock = isLock;
		}
		
		@Override
		public int getCount() {
			// 判断是否加锁   true  返回加锁集合的大小   false   返回未加锁集合大小
			if(isLock){
				return mLockList.size();
			}else{
				return mUnlockList.size();
			}
		}

		@Override
		public AppInfo getItem(int position) {
			// 判断是否加锁   true  返回加锁集合对应的对象   false   返回未加锁集合对应的对象 
			if(isLock){
				return mLockList.get(position);
			}else{
				return mUnlockList.get(position);
			}
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView == null){
				convertView = View.inflate(getApplicationContext(), R.layout.app_lock_item, null);
				holder = new ViewHolder();
				holder.iv_icon = (ImageView) convertView.findViewById(R.id.iv_icon);
				holder.iv_lock = (ImageView) convertView.findViewById(R.id.iv_lock);
				holder.tv_name = (TextView) convertView.findViewById(R.id.tv_name);
				convertView.setTag(holder);
			}else{
				holder = (ViewHolder) convertView.getTag();
			}
			// 获得点中条目应用对象
			final AppInfo appInfo = getItem(position);
			holder.iv_icon.setImageDrawable(appInfo.getIcon());
			holder.tv_name.setText(appInfo.getName());
			if(isLock){
				holder.iv_lock.setImageResource(R.drawable.lock);
			}else{
				holder.iv_lock.setImageResource(R.drawable.unlock);
			}
			
			// 设置一个执行动画的View临时变量
			final View animView = convertView;
			
			// 设置锁的点击事件
			holder.iv_lock.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if(isLock){
						// 已锁应用界面的锁
						// 开始执行动画
						animView.startAnimation(mLockTranslateAnim);
						// 设置动画执行状态监听   当动画执行完 再执行相应逻辑
						mLockTranslateAnim.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
								// 动画开始的时候调用
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
								// 动画重复的时候调用
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								// 动画结束的时候调用
								// 在已锁应用集合中删除一条数据
								mLockList.remove(appInfo);
								// 在未加锁应用集合添加一条数据
								mUnlockList.add(0, appInfo);
								// 删除程序锁数据库中的一条数据
								mDao.delete(appInfo.getPackageName());
								// 刷新数据适配器
								mLockAdapter.notifyDataSetChanged();
								// 设置按钮TextView的文字
								tv_lock.setText("已加锁应用("+mLockList.size()+")");
								tv_unlock.setText("未加锁应用("+mUnlockList.size()+")");
							}
						});
					}else{
						// 未锁应用界面的锁
						// 开始执行动画
						animView.startAnimation(mUnLockTranslateAnim);
						// 设置动画执行状态监听   当动画执行完 再执行相应逻辑
						mUnLockTranslateAnim.setAnimationListener(new AnimationListener() {
							@Override
							public void onAnimationStart(Animation animation) {
							}
							
							@Override
							public void onAnimationRepeat(Animation animation) {
							}
							
							@Override
							public void onAnimationEnd(Animation animation) {
								// 在已锁应用集合中添加一条数据
								mLockList.add(0, appInfo);
								// 在未加锁应用集合删除一条数据
								mUnlockList.remove(appInfo);
								// 在程序锁数据库中添加的一条数据
								mDao.insert(appInfo.getPackageName());
								// 刷新数据适配器
								mUnLockAdapter.notifyDataSetChanged();
								// 设置按钮TextView的文字
								tv_lock.setText("已加锁应用("+mLockList.size()+")");
								tv_unlock.setText("未加锁应用("+mUnlockList.size()+")");
							}
						});
					}
				}
			});
			
			return convertView;
		}
	}
	
	static class ViewHolder{
		ImageView iv_icon;
		ImageView iv_lock;
		TextView tv_name;
	}

	private void initUI() {
		tv_lock = (TextView) findViewById(R.id.tv_lock);
		tv_unlock = (TextView) findViewById(R.id.tv_unlock);
		
		lv_lock = (ListView) findViewById(R.id.lv_lock);
		lv_unlock = (ListView) findViewById(R.id.lv_unlock);
		
		iv_setpwd = (ImageView) findViewById(R.id.iv_setpwd);
		
		// 设置点击事件
		tv_lock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 切换背景
				tv_lock.setBackgroundResource(R.drawable.mini_bg_gray);
				tv_unlock.setBackgroundResource(R.drawable.mini_bg_white);
				// 更换字体颜色
				tv_lock.setTextColor(Color.parseColor("#2FB1E3"));
				tv_unlock.setTextColor(Color.parseColor("#898989"));
				// 改变listview的可见性
				lv_lock.setVisibility(View.VISIBLE);
				lv_unlock.setVisibility(View.GONE);
				// 刷新适配器
				if(mLockAdapter != null){
					mLockAdapter.notifyDataSetChanged();
				}
			}
		});
		
		tv_unlock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 切换背景
				tv_lock.setBackgroundResource(R.drawable.mini_bg_white);
				tv_unlock.setBackgroundResource(R.drawable.mini_bg_gray);
				// 更换字体颜色
				tv_lock.setTextColor(Color.parseColor("#898989"));
				tv_unlock.setTextColor(Color.parseColor("#2FB1E3"));
				// 改变listview的可见性
				lv_lock.setVisibility(View.GONE);
				lv_unlock.setVisibility(View.VISIBLE);
				// 刷新适配器
				if(mUnLockAdapter != null){
					mUnLockAdapter.notifyDataSetChanged();
				}
			}
		});
		
		iv_setpwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 显示设置密码对话框
				showSetPwdDialog();
			}
		});
	}
	
	// listview的触摸事件
	private void initOnTouch() {
		// 未加锁界面的触摸事件
		lv_unlock.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 当手指按下的时候
					x1 = event.getX();
					y1 = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// 当手指离开的时候
					x2 = event.getX();
					y2 = event.getY();
					//左滑，向右翻页
					if ((x1 - x2 > 100) && ((x1 - x2)-(y1 - y2) > 100))  {
						// 如果未加锁界面可见  说明当前在未加锁界面  可以向右翻页
						if(lv_unlock.getVisibility() == View.VISIBLE){
							lv_unlock.startAnimation(mLockTranslateAnim);
							mLockTranslateAnim.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									// 切换背景
									tv_lock.setBackgroundResource(R.drawable.mini_bg_gray);
									tv_unlock.setBackgroundResource(R.drawable.mini_bg_white);
									// 更换字体颜色
									tv_lock.setTextColor(Color.parseColor("#2FB1E3"));
									tv_unlock.setTextColor(Color.parseColor("#898989"));
									// 改变listview的可见性
									lv_lock.setVisibility(View.VISIBLE);
									lv_unlock.setVisibility(View.GONE);
									// 刷新适配器
									if(mLockAdapter != null){
										mLockAdapter.notifyDataSetChanged();
									}
								}
							});
						}
					}	
				}
				return false;
			}
		});
		
		// 已加锁界面的触摸事件
		lv_lock.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					// 当手指按下的时候
					x1 = event.getX();
					y1 = event.getY();
				}
				if (event.getAction() == MotionEvent.ACTION_UP) {
					// 当手指离开的时候
					x2 = event.getX();
					y2 = event.getY();
					
					//右滑，向左翻页
					if ((x2 - x1 > 100) && ((x2 - x1)-(y2 - y1) > 100)) {
						// 如果加锁界面可见  说明当前在加锁界面  可以向左翻页
						if(lv_lock.getVisibility() == View.VISIBLE){
							lv_lock.startAnimation(mUnLockTranslateAnim);
							mUnLockTranslateAnim.setAnimationListener(new AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {
								}
								@Override
								public void onAnimationRepeat(Animation animation) {
								}
								@Override
								public void onAnimationEnd(Animation animation) {
									// 切换背景
									tv_lock.setBackgroundResource(R.drawable.mini_bg_white);
									tv_unlock.setBackgroundResource(R.drawable.mini_bg_gray);
									// 更换字体颜色
									tv_lock.setTextColor(Color.parseColor("#898989"));
									tv_unlock.setTextColor(Color.parseColor("#2FB1E3"));
									// 改变listview的可见性
									lv_lock.setVisibility(View.GONE);
									lv_unlock.setVisibility(View.VISIBLE);
								}
							});
						}
					}
				}
				return false;
			}
		});
	}

	// 设置密码对话框
	private void showSetPwdDialog() {
		// 自定义对话框 要用dialog.setView(view); 来设置
		Builder builder = new AlertDialog.Builder(this);
		final AlertDialog dialog = builder.create();

		// 将一个xml转换成一个view对象
		View view = View.inflate(this, R.layout.dialog_set_pwd, null);
		// 兼容低版本 去掉对话框的内边距
		dialog.setView(view, 0, 0, 0, 0);
		// dialog.setView(view);
		dialog.show();

		// 找到相应的控件
		Button bt_submit = (Button) view.findViewById(R.id.bt_submit);
		Button bt_cancel = (Button) view.findViewById(R.id.bt_cancel);
		final EditText et_set_pwd = (EditText) view
				.findViewById(R.id.et_set_pwd);
		final EditText et_confirm_pwd = (EditText) view
				.findViewById(R.id.et_confirm_pwd);

		// 确认的按钮
		bt_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 获取edittext的内容
				String set_pwd = et_set_pwd.getText().toString().trim();
				String confirm_pwd = et_confirm_pwd.getText().toString().trim();

				if (TextUtils.isEmpty(set_pwd)
						|| TextUtils.isEmpty(confirm_pwd)) {
					ToastUtli.show(getApplicationContext(), "密码不能为空");
				} else if (set_pwd.equals(confirm_pwd)) {
					// 将密码存在sp中
					SpUtils.putStringSp(getApplicationContext(),
							ConstantValue.APP_LOCK_PWD, set_pwd);
					dialog.dismiss();
					ToastUtli.show(getApplicationContext(), "设置成功，您需要在设置中心打开程序锁");
				} else {
					ToastUtli.show(getApplicationContext(), "两次密码不一致");
				}
			}
		});

		// 取消的按钮
		bt_cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});
	}
}
