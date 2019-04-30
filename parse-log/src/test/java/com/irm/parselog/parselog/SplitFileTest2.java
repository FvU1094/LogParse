package com.irm.parselog.parselog;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;

import org.junit.Test;

import com.irm.parselog.parselog.util.FileUtils;

public class SplitFileTest2 {
/*	eachSplitTime:375
	eachSplitTime:754
	eachSplitTime:250
	eachSplitTime:532
	eachSplitTime:281
	eachSplitTime:594
	eachSplitTime:453
	eachSplitTime:578
	eachSplitTime:570
	eachSplitTime:415
	eachSplitTime:659
	eachSplitTime:584
	eachSplitTime:406
	eachSplitTime:1051
	eachSplitTime:724
	eachSplitTime:875
	eachSplitTime:5669
	eachSplitTime:445
	eachSplitTime:600
	eachSplitTime:774
	eachSplitTime:765
	eachSplitTime:904
	eachSplitTime:679
	eachSplitTime:834
	eachSplitTime:739
	eachSplitTime:685
	eachSplitTime:879
	eachSplitTime:777
	eachSplitTime:839
	eachSplitTime:203
	splitTime:41080*/
	@Test
	public void test () {
		try {
			FileInputStream fin = new FileInputStream("D://log//test1.log");
			FileChannel fcin = fin.getChannel();
			FileOutputStream fout = null;
			FileChannel fcout = null;
			ByteBuffer buffer = ByteBuffer.allocate(1024 * 1024 * 50); //50M
			long start = System.currentTimeMillis();
			int count = 0;
			while (true) {
				buffer.clear();
				int flag = fcin.read(buffer);
				
				if (flag == -1) {
					break;
				}
				buffer.flip();
				
				fout = new FileOutputStream("D://log//test1//" + Math.random() + ".log");
				fcout = fout.getChannel();
				long writeTime = System.currentTimeMillis();
				fcout.write(buffer);
				System.out.println("eachSplitTime:" + (System.currentTimeMillis() - writeTime));
				count ++;
				
			}
			System.out.println("splitTime:" + (System.currentTimeMillis() - start));
			fcout.close();
			fcin.close();
			fout.close();
			fin.close();
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	//总文件长度1549608145,拆分文件耗时:67325ms //16M
	//总文件长度1549608145,拆分文件耗时:36538ms //32M
	//总文件长度1549608145,拆分文件耗时:42306ms //64M
	@Test
	public void test2() {
		long originFileSize = 1024 * 1024 * 100;// 100M
		int partFileSize = 1024 * 1024 * 32; //32M
	    long start = System.currentTimeMillis();
	    try {
	      String fileName = "D:\\log\\irm-web-access.log";
	      File sourceFile = new File(fileName);
	      if (sourceFile.length() >= originFileSize) {
	        String cvsFileName = fileName.replaceAll("\\\\", "/");
	        FileUtils fileUtil = new FileUtils();
	        List<String> parts=fileUtil.splitBySize(cvsFileName, partFileSize);
	        for(String part:parts){
	          System.out.println("partName is:"+part);
	        }
	      }
	      System.out.println("总文件长度"+sourceFile.length()+",拆分文件耗时:" + (System.currentTimeMillis() - start) + "ms.");
	    }catch (Exception e){
	       e.getStackTrace();
	    }
	}
}
