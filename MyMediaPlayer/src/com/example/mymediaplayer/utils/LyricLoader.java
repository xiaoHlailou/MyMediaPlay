package com.example.mymediaplayer.utils;

import com.example.mymediaplayer.bean.LyricItem;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

public class LyricLoader {

//	public static void main(String[] args) {
//
//		String musicPath="F:\\视频\\广州1期\\09-手机影音项目-戴振良\\day01\\资料\\视频和音乐资源\\test\\audio\\TongHua.mp3";
//		ArrayList<LyricItem> lyricItems=loadLyric(musicPath);
//		for(LyricItem item:lyricItems){
//			System.out.println(item);
//		}
//	}

	/**
	 * 加载歌词文件
	 * @param musicPath
	 * @return
	 */
	public static ArrayList<LyricItem> loadLyric(String musicPath) {
		ArrayList<LyricItem> lyricItems = null;
		
		//把音乐地址后缀名改成lrc txt
		String prefix=musicPath.substring(0, musicPath.lastIndexOf("."));//删除音乐的扩展名
		
		File lrcFile=new File(prefix+".lrc");
		File txtFile=new File(prefix+".txt");
		
		if(lrcFile.exists()){
			//读取歌词文件
			lyricItems=readLyricFile(lrcFile);
		}else if(txtFile.exists()){
			//读取歌词文件
			lyricItems=readLyricFile(txtFile);
		}
		
		if(lyricItems==null||lyricItems.isEmpty()){
			return null;
		}
		//按开始显示时间排序
		Collections.sort(lyricItems);
		
		return lyricItems;
	}

	/**
	 * 读取歌词文件
	 * @param lrcFile
	 * @return
	 */
	private static ArrayList<LyricItem> readLyricFile(File lrcFile) {
		ArrayList<LyricItem> lyricItems=new ArrayList<LyricItem>();
		
		InputStream in;
		try {
			in = new FileInputStream(lrcFile);
			BufferedReader reader=new BufferedReader(new InputStreamReader(in,"GBK"));
			String line;
			while ((line=reader.readLine())!=null) {
//				System.out.println(line);
				//解读一行的歌词：[03:19.79][02:51.56][02:23.36][01:16.28]张开双手 变成翅膀守护你
				//[03:19.79 
				//张开双手 变成翅膀守护你	最后一行为歌词
				String[] strings = line.split("]");
				String lyricText=strings[strings.length-1];//歌词
				for(int i=0;i<strings.length-1;i++){
					//解析 [03:19.79 ，解析成long类型的毫秒值
					long startShowTime=parseTime(strings[i]);
					lyricItems.add(new LyricItem(startShowTime, lyricText));
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return lyricItems;
	}

	/** 
	 * 解析 [03:19.79 ，解析成long类型的毫秒值  注意：最后的毫秒值需要乘以10
	 * @param time
	 * @return
	 */
	private static long parseTime(String time) {
		String minute=time.substring(1, 3);		//获取分：03
		String second=time.substring(4, 6);		//获取秒：19
		String millis=time.substring(7, 9);	//获取毫秒：790
		
		long minuteMillis=Integer.parseInt(minute)*60*1000;
		long secondMillis=Integer.parseInt(second)*1000;
		long millisMillis=Integer.parseInt(millis)*10;
		
		return minuteMillis+secondMillis+millisMillis;
	}

}
