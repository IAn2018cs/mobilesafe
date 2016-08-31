package com.xiaochen.mobilesafe.activity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import net.youmi.android.AdManager;
import net.youmi.android.normal.spot.SplashView;
import net.youmi.android.normal.spot.SpotDialogListener;
import net.youmi.android.normal.spot.SpotManager;

import org.json.JSONException;
import org.json.JSONObject;
import com.lidroid.xutils.HttpUtils;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.utlis.SpUtils;
import com.xiaochen.mobilesafe.utlis.ConstantValue;
import com.xiaochen.mobilesafe.utlis.StreamUtils;
import com.xiaochen.mobilesafe.utlis.ToastUtli;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class SplashActivity extends Activity {
	protected static final int UPDATA_VERSION = 0;
	protected static final int ENTER_HOME = 1;
	protected static final int URL_EXCEPTION = 2;
	protected static final int IO_EXCEPTION = 3;
	protected static final int JSON_EXCEPTION = 4;
	protected static final String TAG = "SplashActivity";
//	private TextView tv_version;
	private RelativeLayout rl_root;
	private int mVersionCode;
	private String mVersionDes;
	private String mDownloadeUrl;
	private ProgressDialog progressDialog;
	private Animation alpha;
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case UPDATA_VERSION: //提示用户更新
				showUpDataDialog();
				break;
			case ENTER_HOME: //进入主界面
				enterMain();
				break;
			case URL_EXCEPTION: //url地址异常
				enterMain();
				break;
			case IO_EXCEPTION: //io流异常
				enterMain();
				break;
			case JSON_EXCEPTION: //json异常
				enterMain();
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_splash);
		
		// 初始化有米广告
		AdManager.getInstance(this).init("b386a1614ab419e3", "ab22f41d067a8367", false, false);
		
		
		// 检测是否是第一次启动应用
		if(checkFirst()){
			// 初始化UI
			initUI();
			// 初始化数据
			initData();
			// 设置一个开启动画
			setAnimition();
			// 初始化数据库
			initDB();
		}
		
	}

	private void initDB() {
		//拷贝归属地数据库
		copyDB("address.db");
		//拷贝常用号码数据库
		copyDB("commonnum.db");
		//拷贝病毒数据库
		copyDB("antivirus.db");
	}

	// 拷贝数据库
	private void copyDB(String dbName) {
		//文件夹路径
		File files = getFilesDir();
		//创建files路径下一个叫dbName的文件
		File file = new File(files, dbName);
		//如果文件已经存在就跳出该方法
		if(file.exists()){
			return;
		}
		InputStream stream = null;
		FileOutputStream fos = null;
		try {
			//读取第三方资产目录下的文件
			stream = getAssets().open(dbName);
			//将读取的内容写入到指定文件夹的文件中去
			fos = new FileOutputStream(file);
			//每次读取内容的大小
			byte[] bs = new byte[1024];
			//临时变量
			int temp = -1;
			while((temp = stream.read(bs))!=-1){
				fos.write(bs, 0, temp);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			//如果流不等于空  就把他们都关掉
			if(stream!=null && fos!=null){
				try {
					stream.close();
					fos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	//给相对布局设置一个透明动画
	private void setAnimition() {
		alpha = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.splash_alpha);
		rl_root.startAnimation(alpha);
	}

	//显示更新对话框
	protected void showUpDataDialog() {
		Builder builder = new AlertDialog.Builder(this);
		//设置对话框左上角图标
		builder.setIcon(R.drawable.ic_launcher);
		//设置对话框标题
		builder.setTitle("发现新版本");
		//设置对话框内容
		builder.setMessage(mVersionDes);
		
		//设置积极的按钮
		builder.setPositiveButton("立即更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//下载apk
				downloadApk();
				// 显示一个进度条对话框
				showProgressDialog();
			}
		});
		
		//设置消极的按钮
		builder.setNegativeButton("暂不更新", new OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//进入主界面
				enterMain();
			}
		});
		
		//监听取消按钮
		builder.setOnCancelListener(new OnCancelListener() {
			//当点击返回的按钮时执行
			@Override
			public void onCancel(DialogInterface dialog) {
				//进入主界面
				enterMain();
				//让对话框消失
				dialog.dismiss();
			}
		});
		
		builder.show();
	}

	// 下载的进度条对话框
	protected void showProgressDialog() {
		progressDialog = new ProgressDialog(this);
		progressDialog.setIcon(R.drawable.ic_launcher2);
		progressDialog.setTitle("下载安装包中");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.show();
	}

	// 下载安装包
	protected void downloadApk() {
		//检测sd卡是否可用
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			//获得sd卡路径
			String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/mobilesafe.apk"; 
			HttpUtils httpUtils = new HttpUtils();
			// url:访问服务器的url地址
			// target:下载后文件存储的路径
			// callback:一个文件类型的泛型
			httpUtils.download(mDownloadeUrl, path, new RequestCallBack<File>() {
				//当下载成功的时候调用
				@Override
				public void onSuccess(ResponseInfo<File> arg0) {
					progressDialog.dismiss();
					//下载完的文件就存在arg0的结果里
					File file = arg0.result;
					//下载完成后安装应用
					installApk(file);
				}
				//当下载失败的时候调用
				@Override
				public void onFailure(HttpException arg0, String arg1) {
					ToastUtli.show(getApplicationContext(), "下载失败");
					enterMain();
				}
				//当开始下载的时候调用
				@Override
				public void onStart() {
					super.onStart();
				}
				//下载过程中调用total：文件的总大小   current：当前下载大小  isUploading：是否正在下载
				@Override
				public void onLoading(long total, long current,
						boolean isUploading) {
					super.onLoading(total, current, isUploading);
					progressDialog.setMax((int) total);
					progressDialog.setProgress((int) current);
				}
			});
		}
	}

	//安装应用
	protected void installApk(File file) {
		Intent intent = new Intent("android.intent.action.VIEW");
		intent.addCategory("android.intent.category.DEFAULT");
		//文件作为数据源
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		//如果用户在安装界面点取消按钮 就要回退到这里 然后再调到主界面 就需要一个带返回结果的开启方式
		startActivityForResult(intent, 0);
		
	}
	
	//当打开安装界面然后返回时调用
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//进入主界面
		enterMain();
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	// 使用广告进入应用
	public void useADInApp(){
		SplashView splashView = SpotManager.getInstance(this).getSplashView(this);
		// 设置是否显示倒计时，默认显示
		splashView.setShowReciprocal(true);
		// 设置是否显示关闭按钮，默认为不显示
		splashView.hideCloseBtn(false);
		// 传入跳转的intent，若传入intent，初始化时目标activity传入null
		Intent intent = new Intent(this, HomeActivity.class);
		splashView.setIntent(intent);
		// 展示失败后是否直接跳转，默认为是
		splashView.setIsJumpTargetWhenFail(true);
		//开屏也可以作为控件加入到界面中。
		setContentView(splashView.getSplashView());
		SpotManager.getInstance(this).showSplashSpotAds(this, splashView, new SpotDialogListener() {
			@Override
			public void onShowSuccess() {
				Log.i(TAG, "开屏展示成功");
			}

			@Override
			public void onShowFailed() {
				Log.i(TAG, "开屏展示失败");
			}

			@Override
			public void onSpotClosed() {
				Log.i(TAG, "开屏被关闭");
			}

			@Override
			public void onSpotClick(boolean isWebPath) {
				Log.i(TAG, "开屏被点击");
			}
		});
	}

	//进入主界面
	protected void enterMain() {
		// 判断是否显示广告
		if(SpUtils.getBoolSp(this, ConstantValue.OPEN_AD, true) && (!SpUtils.getBoolSp(this, ConstantValue.IS_FIRST,true))){
			// 如果不是第一次启动应用并且打开显示广告  就使用广告进入应用
			useADInApp();
		}else{
			SpUtils.putBoolSp(this, ConstantValue.IS_FIRST, false);
			Intent intent = new Intent(SplashActivity.this,HomeActivity.class);
			startActivity(intent);
			finish();
		}
	}

	//检测是否是第一次启动 
	//如果是就返回false 这样就不会执行下面的步骤
	//如果不是就返回true 这样就会执行下面的步骤
	private boolean checkFirst() {
		boolean b = true;
		//创建SharedPreferences
		SharedPreferences sp = getSharedPreferences("first", MODE_PRIVATE);
		if(sp.getBoolean("first", true)){
			Editor edit = sp.edit();
			edit.putBoolean("first", false);
			edit.commit();
			b = false;
			Intent intent = new Intent(SplashActivity.this,HelloActivity.class);
			startActivity(intent);
			finish();
		}
		return b;
	}

	private void initData() {
		// 获得版本名称 并 显示到textview上
//		tv_version.setText("版本名称" + getVersionName());
		// 获取当前版本号和服务器最新的版本号 然后比较
		// 获取当前版本号
		mVersionCode = getVersionCode();
		// 如果勾选自动更新选项  则检测服务器中的版本号   否则进入主界面
		if(SpUtils.getBoolSp(getApplicationContext(), ConstantValue.OPEN_UPDATE, false)){
			checkVersionCode();
		}else{
			//发送一条消息    并延迟3s执行  以达到展示启动页面的效果
			mHandler.sendEmptyMessageDelayed(ENTER_HOME, 3000);
		}
	}

	// 检测是否更新(获取服务器的数据)
	private void checkVersionCode() {
		new Thread() {
			public void run() {
				// 获取Message对象
				Message message = Message.obtain();
				// 记录开始时间
				long startTime = System.currentTimeMillis();
				try {
					URL url = new URL("http://192.168.0.106:8080/version.json");
					HttpURLConnection connection = (HttpURLConnection) url.openConnection();
					connection.setConnectTimeout(2000);
					connection.setReadTimeout(2000);
					connection.setRequestMethod("GET");
					if (connection.getResponseCode() == 200) {
						// 以流的形式将数据取下来
						InputStream is = connection.getInputStream();
						// 将流转化成字符串
						String json = StreamUtils.streamToString(is);
						// 解析json
						JSONObject jsonObject = new JSONObject(json);
						String versionName = jsonObject.getString("versionName");
						String versionCode = jsonObject.getString("versionCode");
						mVersionDes = jsonObject.getString("versionDes");
						mDownloadeUrl = jsonObject.getString("downloadeUrl");
						System.out.println(versionName+versionCode+mVersionDes+mDownloadeUrl);
						// 将当前应用版本和服务器里应用版本做比较
						if (mVersionCode < Integer.parseInt(versionCode)) {
							// 说明出现新版本 提示用户更新
							message.what = UPDATA_VERSION;
						} else {
							// 没有新版本 直接进入主界面
							message.what = ENTER_HOME;
						}
					}
				} catch (MalformedURLException e) {
					e.printStackTrace();
					message.what = URL_EXCEPTION;
				} catch (IOException e) {
					e.printStackTrace();
					message.what = IO_EXCEPTION;
				} catch (JSONException e) {
					e.printStackTrace();
					message.what = JSON_EXCEPTION;
				} finally {
					//为了更好的用户体验 指定睡眠时间 使在启动页面的时间保持在3秒
					//让网络请求的时间和睡眠的时间加起来为3秒
					//结束时间
					long endTime = System.currentTimeMillis();
					if((endTime-startTime)<3000){
						try {
							Thread.sleep(3000-(endTime-startTime));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					mHandler.sendMessage(message);
				}

			};
		}.start();
	}

	// 获取本应用版本号
	private int getVersionCode() {
		// 拿到包管理者
		PackageManager pm = getPackageManager();
		// 获取包的基本信息
		try {
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			// 返回应用的版本号
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 0;
	}

	// 获取本应用版本名称
	private String getVersionName() {
		// 拿到包管理者
		PackageManager pm = getPackageManager();
		// 获取包的信息(Info)
		try {
			// flags：为0是获取基本信息
			PackageInfo info = pm.getPackageInfo(getPackageName(), 0);
			return info.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void initUI() {
//		tv_version = (TextView) findViewById(R.id.tv_version);
		rl_root = (RelativeLayout) findViewById(R.id.rl_root);
	}

}
