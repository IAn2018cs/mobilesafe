package com.xiaochen.mobilesafe.service;

import com.xiaochen.mobilesafe.R;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;

public class AlarmMusicService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 把系统音量调节到最大
		AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
		audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, maxVolume, 0);

		MediaPlayer mediaPlayer = MediaPlayer.create(getApplicationContext(),R.raw.ylzs);
		// 设置循环播放
		mediaPlayer.setLooping(true);
		// 开始播放
		mediaPlayer.start();
		Log.d("AlarmMusicService", "音乐播放了吗吗吗吗吗");
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}

}
