package com.irm.parselog.parselog;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.Map;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.apache.commons.collections4.CollectionUtils;  
import org.apache.commons.collections4.MapUtils;  
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import com.irm.parselog.parselog.util.FileUtils;

public class FileReadTest {
	
    public static void testByteBuffer(){
        RandomAccessFile aFile = null;
        FileChannel fc = null;
        try{
            aFile = new RandomAccessFile("irm-web-access.log","rw");
            fc = aFile.getChannel();
            long timeBegin = System.currentTimeMillis();
            ByteBuffer buff = ByteBuffer.allocate((int) aFile.length());
            buff.clear();
            fc.read(buff);
            //System.out.println((char)buff.get((int)(aFile.length()/2-1)));
            //System.out.println((char)buff.get((int)(aFile.length()/2)));
            //System.out.println((char)buff.get((int)(aFile.length()/2)+1));
            long timeEnd = System.currentTimeMillis();
            System.out.println("ByteBuffer Read time: "+(timeEnd-timeBegin)+"ms");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile!=null){
                    aFile.close();
                }
                if(fc!=null){
                    fc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
	
    public static void testMappedByteBuffer(){
        RandomAccessFile aFile = null;
        FileChannel fc = null;
        try{
            aFile = new RandomAccessFile("irm-web-access.log","rw");
            fc = aFile.getChannel();
            long timeBegin = System.currentTimeMillis();
            MappedByteBuffer mbb = fc.map(FileChannel.MapMode.READ_ONLY, 0, aFile.length());
            // System.out.println((char)mbb.get((int)(aFile.length()/2-1)));
            // System.out.println((char)mbb.get((int)(aFile.length()/2)));
            //System.out.println((char)mbb.get((int)(aFile.length()/2)+1));
            long timeEnd = System.currentTimeMillis();
            System.out.println("MappedByteBuffer Read time: "+(timeEnd-timeBegin)+"ms");
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile!=null){
                    aFile.close();
                }
                if(fc!=null){
                    fc.close();
                }
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
	
	public static void testBufferReader () {
		File file = new File("C:\\Users\\Admin\\Desktop\\uploadFile-master\\parse-log-final\\parse-log\\irm-web-access.log");
		try {
			long timeBegin = System.currentTimeMillis();
			BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
			long timeEnd = System.currentTimeMillis();
            System.out.println("BufferReader Read time: "+(timeEnd-timeBegin)+"ms");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	/*
	 * 
	 */
	 private static final Logger logger = LoggerFactory.getLogger(FileReadTest.class);  
	 
	 private static final String ENCODING = "UTF-8";  
	 private static final int NUM = 50000;  
	 
	 private static File file = new File("D://log//test1.log");  
	 private static File randomFile = new File("D://log//test2.log");  
	 
	 /** 
	     * 生成1000w随机文本文件 
	     */ 
	 @Test 
	 public void makePin() {  
	        String prefix = "_$#";  
	        OutputStreamWriter out = null;  
	        try {  
	            out = new OutputStreamWriter(new FileOutputStream(file, true), ENCODING);  
	            // 在1500w里随机1000w数据 
	            for (int j = 0; j < 100000000; j++) {  
	                out.write(prefix + (int) (130000000 * Math.random()) + "\n");  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            IOUtils.closeQuietly(out);  
	        }  
	        logger.info(file.getAbsolutePath());  
	    }  
	 
	 	/** 
	     * 测试RandomAccessFile读取文件 
	     */ 
	 @Test 
	 public void testRandomAccessRead() {  
		 long start = System.currentTimeMillis();  
		 // 
	        logger.info(String.valueOf(file.exists()));  
	        long pos = 0L;  
	        while (true) {  
	            Map<String, Object> res = FileUtils.readLine(file, ENCODING, pos, NUM);  
	            // 如果返回结果为空结束循环 
	            if (MapUtils.isEmpty(res)) {  
	            	break;  
	            }  
	            Object po = res.get("pins");  
	            List<String> pins = (List<String>) res.get("pins");  
	            if (CollectionUtils.isNotEmpty(pins)) {  
	            	//	                logger.info(Arrays.toString(pins.toArray())); 
	            	if (pins.size() < NUM) {  
	            		break;  
	                }  
	            } else {  
	            	break;  
	            }  
	            pos = (Long) res.get("pos");  
	        }  
	        logger.info(((System.currentTimeMillis() - start) / 1000) + "");  
	    }  
	 
	 /** 
	     * 测试RandomAccessFile读取文件 
	     */ 
	 @Test 
	 public void testBufferedRandomAccessRead() {  
	 long start = System.currentTimeMillis();  
	 // 
	        logger.info(String.valueOf(file.exists()));  
	        long pos = 0L;  
	        while (true) {  
	            Map<String, Object> res = FileUtils.BufferedRandomAccessFileReadLine(file, ENCODING, pos, NUM);  
	            // 如果返回结果为空结束循环 
	            if (MapUtils.isEmpty(res)) {  
	            	break;  
	            }  
	            List<String> pins = (List<String>) res.get("pins");  
	            if (CollectionUtils.isNotEmpty(pins)) {  
	            	//	                logger.info(Arrays.toString(pins.toArray())); 
	            	if (pins.size() < NUM) {  
	            		break;  
	                }  
	            } else {  
	            	break;  
	            }  
	            pos = (Long) res.get("pos");  
	        }  
	        logger.info(((System.currentTimeMillis() - start) / 1000) + "");  
	    }  
	 
	 /** 
	     * 测试普通读取文件 
	     */ 
	 @Test 
	 public void testCommonRead() {  
	 long start = System.currentTimeMillis();  
	        logger.info(String.valueOf(randomFile.exists()));  
	        int index = 0;  
	        while (true) {  
	            List<String> pins = FileUtils.readLine(file, ENCODING, index, NUM);  
	            if (CollectionUtils.isNotEmpty(pins)) {  
//	                logger.info(Arrays.toString(pins.toArray())); 
	            	if (pins.size() < NUM) {  
	            		break;  
	                }  
	            } else {  
	            	break;  
	            }  
	            index += NUM;  
	        }  
	        logger.info(((System.currentTimeMillis() - start) / 1000) + "");  
	    }  
  
	public static void main (String []orgs) {
		
		testByteBuffer();
		testMappedByteBuffer();
		//testBufferReader();
		/* 第一次读取
		 * ByteBuffer Read time: 5000+ms
		 * MappedByteBuffer Read time: 700+ms
		 * 第二次读取
		 * ByteBuffer Read time: 704ms
		 * MappedByteBuffer Read time: 2ms
		 */
	}
	//fileReader test
	/*		FileReader fileReader = new FileReader("D:\\log\\test1.log",100,3); 
	fileReader.registerHanlder(new FileServiceImpl()); 
	fileReader.startRead(); */
	@Test
	public void RandomAccessFileTransfer () {
		
		try {
			RandomAccessFile fromFile = new RandomAccessFile("2.log", "rw");
			FileChannel fromChannel = fromFile.getChannel();

			RandomAccessFile toFile = new RandomAccessFile("2_1.log", "rw");
			FileChannel toChannel = toFile.getChannel();
			
			long count = fromChannel.size();
			int position = 0;
			long startTime = System.currentTimeMillis();
			fromChannel.transferTo(position, count, toChannel);
			System.out.println((System.currentTimeMillis() - startTime) + "ms"); //2827ms

			toChannel.close();
			fromFile.close();
			toFile.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}

}
