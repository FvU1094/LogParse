package com.irm.parselog.parselog;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

import com.irm.parselog.parselog.util.FileUtils;

public class GetFileLineTest {
	/*
	 * MappedByteBuffer readTime:56ms
	 * 643006
	 * getFileLineNumber readTime:4785ms
	 * 335073628
	 * 643006
	 * getFileRow readTime:592ms
	 */
	public static void main (String []args) {
		File file = new File("D:\\log\\irm-web-access.log");
		FileUtils fileUtil = new FileUtils();
		RandomAccessFile rfile = null;
		FileChannel channel = null;
		int rowNumber = 0;
		try {
			rfile = new RandomAccessFile("irm-web-access.log", "r");
			channel = rfile.getChannel();
			long timeBegin = System.currentTimeMillis();
			MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, rfile.length());
			long timeEnd = System.currentTimeMillis();
			System.out.println("MappedByteBuffer readTime:"+ (timeEnd-timeBegin) + "ms");
			//getFileLineNumber 1870ms
			timeBegin = System.currentTimeMillis();
			System.out.println(fileUtil.getFileLineNumber(file));
			timeEnd = System.currentTimeMillis();
			
			System.out.println("getFileLineNumber readTime:"+ (timeEnd-timeBegin) + "ms");
			System.out.println(rfile.length());
			
			//BufferedInputStream 709ms
			timeBegin = System.currentTimeMillis();
			System.out.println(fileUtil.getFileRow(file));
			System.out.println("getFileRow readTime:" + (System.currentTimeMillis() - timeBegin) + "ms");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
            try{
                if(rfile!=null){
                	rfile.close();
                }
                if(channel!=null){
                	channel.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
		}
	}

}
