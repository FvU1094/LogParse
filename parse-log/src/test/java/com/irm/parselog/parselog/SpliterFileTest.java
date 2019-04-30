package com.irm.parselog.parselog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import org.junit.Test;

import com.fasterxml.jackson.databind.util.ByteBufferBackedInputStream;
import com.irm.parselog.parselog.service.FileHandler;
import com.irm.parselog.parselog.service.impl.BigFileHandler;
import com.irm.parselog.parselog.util.FileUtils;

public class SpliterFileTest {
	@Test
	public void writeFile() throws IOException, InterruptedException {
        FileUtils fileUtil = new FileUtils();
		System.out.println(fileUtil.userDir);
		File tempFile = new File(fileUtil.userDir + System.getProperty("file.separator") + "1.log");
		//StringBuilder sb = new StringBuilder();

		//long originFileSize = 1024 * 1024 * 100;// 100M
		int blockFileSize = 1024 * 1024 * 5;// 5M
		/*//生成大文件
		for (int i = 0; i < originFileSize; i++) {
			sb.append("A");
		}*/
		

		RandomAccessFile file = new RandomAccessFile(tempFile, "r");
		FileChannel channel = file.getChannel();
		MappedByteBuffer mbb = channel.map(FileChannel.MapMode.READ_ONLY, 0, file.length());
        BufferedReader bufferedReader = new BufferedReader(new FileReader("1.log"));
        String fileName = fileUtil.userDir + System.getProperty("file.separator") + "1.log";
        System.out.println(fileName);
        System.out.println(FileUtils.writeFile(fileName, bufferedReader.toString()));
        
/*        // 追加内容
        sb.setLength(0);
        sb.append("0123456789");
        FileUtils.append(fileName, sb.toString());*/

        //将文件拆分
        fileUtil.splitBySize(fileName, blockFileSize);
        Thread.sleep(10000);// 稍等10秒，等前面的小文件全都写完
        // 合并成新文件
        fileUtil.mergePartFiles(fileUtil.userDir, ".log",
                blockFileSize, fileUtil.userDir + "1.log");
        
        
	}
	@Test
	public  void FileHandler (){
		BigFileHandler.Builder builder = new BigFileHandler.Builder("D://log//4.log",new FileHandler() {
	
					@Override
					public void handle(String line) {
						//System.out.println(line);
						//increat();
					}
				});
				builder.withTreahdSize(10)
					   .withCharset("gbk")
					   .withBufferSize(1024*1024);
				BigFileHandler bigFileReader = builder.build();
				//BigFileHandler.start();
			}

}
