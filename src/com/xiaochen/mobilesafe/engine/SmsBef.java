package com.xiaochen.mobilesafe.engine;

import java.io.File;
import java.io.FileOutputStream;
import org.xmlpull.v1.XmlSerializer;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Xml;

public class SmsBef {

	/**
	 * 备份短信 需要在子线程里完成
	 * 
	 * @param context
	 *            上下文环境
	 * @param name
	 *            要打开的文件名称
	 * @param cb  
	 *            实现了回掉接口的对象
	 */
	public static void backUp(Context context, String name, CallBack cb) {
		FileOutputStream fos = null;
		Cursor cursor = null;
		int index = 0;
		try {
			// 获取短信备份写入的文件
			File file = new File(name);
			// 通过内容解析器去查询短信数据库
			cursor = context.getContentResolver().query(
					Uri.parse("content://sms/"),
					new String[] { "address", "date", "type", "body" }, null,
					null, null);
			
			// 获取短信总数
			int max = cursor.getCount();
			
			// 设置进度最大值
			if(cb!=null){
				cb.setMax(max);
			}
			
			// 打开文件输出流
			fos = new FileOutputStream(file);
//			fos = context.openFileOutput(name, Context.MODE_WORLD_READABLE + Context.MODE_WORLD_WRITEABLE);
			
			// 序列化xml
			XmlSerializer newSerializer = Xml.newSerializer();
			newSerializer.setOutput(fos, "utf-8");
			
			newSerializer.startDocument("utf-8", true);
			newSerializer.startTag(null, "smss");
			while (cursor.moveToNext()) {
				newSerializer.startTag(null, "sms");
				
				newSerializer.startTag(null, "address");
				newSerializer.text(cursor.getString(0));
				newSerializer.endTag(null, "address");
				
				newSerializer.startTag(null, "date");
				newSerializer.text(cursor.getString(1));
				newSerializer.endTag(null, "date");
				
				newSerializer.startTag(null, "type");
				newSerializer.text(cursor.getString(2));
				newSerializer.endTag(null, "type");
				
				newSerializer.startTag(null, "body");
				String xmlChar = xmlChar(cursor.getString(3));
				newSerializer.text(xmlChar);
				newSerializer.endTag(null, "body");
				
				newSerializer.endTag(null, "sms");
				
				// 更新进度
				index++;
				if(max<1000){
					Thread.sleep(30);
				}
				if(cb!=null){
					cb.setProgress(index);
				}
			}
			newSerializer.endTag(null, "smss");
			newSerializer.endDocument();
			
			if(index >= max && cb!=null){
				cb.dismissDialog();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				if(fos!=null && cursor!=null){
					cursor.close();
					fos.close();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	/**过滤xml文件中的非法字符
	 * @param str 要转换的字符串
	 * @return 返回合法的xml字符串
	 */
	public static String xmlChar(String str){
		StringBuffer sb = new StringBuffer();
		char c;
		if(str==null || str.equals("")){
			return "";
		}
		for(int i=0; i<str.length(); i++){
			c = str.charAt(i);
			if((c==0x9) || (c==0xA) || (c==0xD) || ((c>=0x20)&&(c<=0xD7FF)) || ((c>=0xE000)&&(c<=0xFFFD)) || (c>=0x10000)&&(c<=0x10FFFF)){
				sb.append(c);
			}
		}
		return sb.toString();
	}
	
	// 回掉
	// 1 定义接口
	// 2 在接口里写必要的未实现方法
	// 3 传递一个实现了该接口的对象
	// 4 用该对象在合适的位置调方法
	
	public interface CallBack{
		public abstract void setMax(int max);
		public abstract void setProgress(int index);
		public abstract void dismissDialog();
	}
	
}
