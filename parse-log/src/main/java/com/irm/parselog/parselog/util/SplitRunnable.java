package com.irm.parselog.parselog.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//分割处理
public class SplitRunnable implements Runnable {
	
	private static final Logger log = LoggerFactory.getLogger(SplitRunnable.class);
	int byteSize;
	int rowNumber;
	String partFileName;
	File originFile;
	long startPos;
	CountDownLatch latch;
	
	public SplitRunnable(int byteSize, long startPos, String partFileName,File originFile, CountDownLatch latch) {
       this.startPos = startPos;
       this.byteSize = byteSize;
       this.partFileName = partFileName;
       this.originFile = originFile;
       this.latch = latch;
    }

	@Override
	public void run() {
	      RandomAccessFile rFile;
	      OutputStream os;
	      try {
	        rFile = new RandomAccessFile(originFile, "r");
	        byte[] b = new byte[byteSize];
	        rFile.seek(startPos);// 移动指针到每“段”开头
	        int s = rFile.read(b);
	        os = new FileOutputStream(partFileName);
	        os.write(b, 0, s);
	        os.flush();
	        os.close();
	        latch.countDown();
	      } catch (IOException e) {
	        log.error(e.getMessage());
	        latch.countDown();
	      }
	}		
}
