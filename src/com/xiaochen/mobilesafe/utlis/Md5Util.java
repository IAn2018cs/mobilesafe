package com.xiaochen.mobilesafe.utlis;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class Md5Util {
	/**
	 * 给密码加密
	 * @param pwd 需要加密的密码
	 * @return 返回加密的字符串
	 */
	public static String encryption(String pwd) {
		try {
			//加盐
			pwd = pwd + "IAn2018cs";
			// 指定加密算法类型 algorithm:算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// 将需要加密的字符串转换成byte类型的数组 然后进行随机哈希过程
			byte[] bs = digest.digest(pwd.getBytes()); // bs的长度为16
			// 遍历bs 然后生成32位字符串 固定写法
			// 拼接字符串
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				// int类型的i需要转换成16进制的字符
				String hexString = Integer.toHexString(i);
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				stringBuffer.append(hexString);
			}
			// 打印结果
			System.out.println(stringBuffer.toString());
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// 没有该算法的异常
			e.printStackTrace();
			return null;
		}
	}
	
	/**普通的MD5加密方式(没有加盐)给字符串按照md5算法加密 将字符串转换成32位字符(由16进制字符(0~f)组成)
	 * @param strMd5  需要Md5加密的字符串
	 * @return 加密后的字符串
	 */
	public static String commonMd5(String strMd5) {
		try {
			// 指定加密算法类型 algorithm:算法类型
			MessageDigest digest = MessageDigest.getInstance("MD5");
			// 将需要加密的字符串转换成byte类型的数组 然后进行随机哈希过程
			byte[] bs = digest.digest(strMd5.getBytes()); // bs的长度为16
			// 遍历bs 然后生成32位字符串 固定写法
			// 拼接字符串
			StringBuffer stringBuffer = new StringBuffer();
			for (byte b : bs) {
				int i = b & 0xff;
				// int类型的i需要转换成16进制的字符
				String hexString = Integer.toHexString(i);
				if (hexString.length() < 2) {
					hexString = "0" + hexString;
				}
				stringBuffer.append(hexString);
			}
			return stringBuffer.toString();
		} catch (NoSuchAlgorithmException e) {
			// 没有该算法的异常
			e.printStackTrace();
			return null;
		}
	}

}
