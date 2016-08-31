package com.xiaochen.mobilesafe.engine;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.xiaochen.mobilesafe.R;
import com.xiaochen.mobilesafe.db.domain.ProcessInfo;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

public class ProcessInfoProvider {
	/**获取运行的进程总数
	 * @param context	上下文环境
	 * @return	运行的进程总数
	 */
	public static int getProcessCount(Context context){
		// 创建activity管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获取运行中的进程集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		// 返回集合的大小  即运行的进程总数
		return runningAppProcesses.size();
	}
	
	/**返回可用内存
	 * @param context 上下文环境
	 * @return 可用内存  单位为byte
	 */
	public static long getAvailable(Context context){
		// 创建activity管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 构建存储可用内存对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 给MemoryInfo对象赋值
		am.getMemoryInfo(memoryInfo);
		// 返回可用内存大小
		return memoryInfo.availMem;
	}
	
//	@SuppressLint("NewApi")
	/**获取总内存
	 * @param context 上下文环境
	 * @return 总内存大小 单位byte
	 */
	public static long getTotalMemory(Context context){
		/*// 创建activity管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 构建存储可用内存对象
		MemoryInfo memoryInfo = new MemoryInfo();
		// 给MemoryInfo对象赋值
		am.getMemoryInfo(memoryInfo);
		// 返回可用内存大小   需要api16才能使用
		return memoryInfo.totalMem;*/
		// 兼容低版本
		FileReader fileReader = null;
		BufferedReader bufferedReader = null;
		try {
			fileReader = new FileReader("proc/meminfo");
			bufferedReader = new BufferedReader(fileReader);
			String readLine = bufferedReader.readLine();
			char[] charArray = readLine.toCharArray();
			StringBuffer stringBuffer = new StringBuffer();
			for (char c : charArray) {
				if(c>='0' && c<='9'){
					stringBuffer.append(c);
				}
			}
			String string = stringBuffer.toString();
			return Long.parseLong(string)*1024;
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fileReader!=null && bufferedReader!=null){
					bufferedReader.close();
					fileReader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return 0;
	}
	
	/**获取正在运行的应用信息集合
	 * @param context 上下文环境
	 * @return 正在运行的应用信息集合
	 */
	public static List<ProcessInfo> getProcessInfo(Context context){
		// 创建一个包含进程信息的集合
		List<ProcessInfo> processInfoList = new ArrayList<ProcessInfo>();
		// 获得activity管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 获得包管理者对象
		PackageManager pm = context.getPackageManager();
		// 通过ActivityManager对象 拿到包含运行中进程信息的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		// 遍历集合
		for (RunningAppProcessInfo info : runningAppProcesses) {
			// 创建javabean对象
			ProcessInfo processInfo = new ProcessInfo();
			// 拿到包名 设置给javabean对象processInfo
			String packageName = info.processName;
			processInfo.setPackageName(packageName);
			// 通过ActivityManager对象 拿到进程占用内存的信息processMemoryInfo  (参数为一个int类型的数组  里面是进程号)
			android.os.Debug.MemoryInfo[] processMemoryInfo = am.getProcessMemoryInfo(new int[]{info.pid});
			// 拿到数组中的第一个元素 即当前进程的内存占用信息memoryInfo
			android.os.Debug.MemoryInfo memoryInfo = processMemoryInfo[0];
			// 得到当前进程占用的内存大小  单位byte
			processInfo.setOccupyMemory(memoryInfo.getTotalPrivateDirty()*1024);
			try {
				// 通过PackageManager对象得到对应包名应用的信息applicationInfo
				ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, 0);
				// 获得应用的名称 并设置给javabean对象
				processInfo.setName(applicationInfo.loadLabel(pm).toString());
				// 获得应用的图标 并设置给javabean对象
				processInfo.setIcon(applicationInfo.loadIcon(pm));
				// 根据状态码 判断是否是系统应用
				if((applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == ApplicationInfo.FLAG_SYSTEM){
					processInfo.setSystem(true);
				}else{
					processInfo.setSystem(false);
				}
			} catch (NameNotFoundException e) {
				// 异常情况的处理  当找不到对应的Application时
				processInfo.setName(packageName);
				processInfo.setIcon(context.getResources().getDrawable(R.drawable.ic_launcher_null));
				processInfo.setSystem(true);
				e.printStackTrace();
			}
			
			processInfoList.add(processInfo);
		}
		
		return processInfoList;
	}
	
	/**获取正在运行的应用包名
	 * @param context 上下文环境
	 * @return 正在运行的应用包名
	 */
	public static List<String> getProcessPackageName(Context context){
		// 创建一个包含进程信息的集合
		List<String> processPackageNameList = new ArrayList<String>();
		// 获得activity管理者对象
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		// 通过ActivityManager对象 拿到包含运行中进程信息的集合
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		// 遍历集合
		for (RunningAppProcessInfo info : runningAppProcesses) {
			String packageName = info.processName;
			processPackageNameList.add(packageName);
		}
		return processPackageNameList;
	}
	
	/**杀死后台进程
	 * @param context 上下文环境
	 * @param processInfo ProcessInfo进程信息的javabean对象
	 */
	public static void clearMem(Context context, ProcessInfo processInfo){
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		am.killBackgroundProcesses(processInfo.getPackageName());
	}
	
	/**杀死所有进程
	 * @param context 上下文环境
	 */
	public static void killAllProcess(Context context){
		// 通过ActivityManager获得运行中的进程信息集合
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningAppProcesses = am.getRunningAppProcesses();
		// 遍历集合   杀死后台进程
		for (RunningAppProcessInfo info : runningAppProcesses) {
			// 如果是本应用进程就跳出本次循环
			if (info.processName.equals("com.xiaochen.mobilesafe")) {
				continue;
			}
			am.killBackgroundProcesses(info.processName);
		}
	}
}
