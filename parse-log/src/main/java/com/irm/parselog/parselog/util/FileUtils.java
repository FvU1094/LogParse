package com.irm.parselog.parselog.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit; 

import com.google.common.collect.Lists;  
import com.google.common.collect.Maps;  

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;  

import com.fasterxml.classmate.members.RawMethod;
import com.irm.parselog.parselog.service.LogDataService;
import com.irm.parselog.parselog.vo.PartFile;


public class FileUtils {
	
	//文件保存路径 userDir
	public final String userDir = System.getProperty("user.dir");
	private static final Logger log = LoggerFactory.getLogger(FileUtils.class);
	//private static final long originFileSize = 1024 * 1024 * 100; //100M
	private static final int partFileSize = 1024 * 1024 * 16; //拆分文件大小16M(32/8)
	
	//根据"数量判断
	private static final char logSeparator = '"';
	
	LogDataService logDataService;
	
	//左填充, 文件拆分rename
	public static String leftPad (String str, int length, char ch) {
		if (str.length() >= length) {
			return str;
		}
		char[] chs = new char[length];
		Arrays.fill(chs, ch);
		char[] src = str.toCharArray();
		System.arraycopy(src, 0, chs, length - src.length, src.length);
		return new String(chs);

	}
	
	/*
	 * 删除文件
	 * fileName 待删除的完整文件名
	 */
	public static boolean delete ( String fileName) {
		boolean result = false;
		File file = new File(fileName);
		if (file.exists()) {
			result = file.delete();
		} else {
			result = true;
		}
		return result;
	}
	
	  /**
	   * 修改最后一行数据
	   * @param fileName
	   * @param lastString
	   * @return
	   * @throws IOException
	   */
	  private void writeLastLine(String fileName,String lastString){
	    try {
	      // 打开一个随机访问文件流，按读写方式
	      RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
	      // 文件长度，字节数
	      long fileLength = randomFile.length();
	      //将写文件指针移到文件尾。
	      randomFile.seek(fileLength);
	      //避免写入乱码
	      randomFile.write(lastString.getBytes("gbk"));
	      randomFile.close();
	    } catch (IOException e) {
	      log.error(e.getMessage());
	    }
	  }
	  /**
	   * 读取最后一行数据
	   * @param filename
	   * @return
	   * @throws IOException
	   */
	  private String readLastLine(String filename) throws IOException {
	    // 使用RandomAccessFile , 从后找最后一行数据
	    RandomAccessFile raf = new RandomAccessFile(filename, "r");
	    long len = raf.length();
	    String lastLine = "";
	    if(len!=0L) {
	      long pos = len - 1;
	      while (pos > 0) {
	        pos--;
	        raf.seek(pos);
	        if (raf.readByte() == '\n') {
	          lastLine = raf.readLine();
	          lastLine=new String(lastLine.getBytes("8859_1"), "gbk");
	          break;
	        }
	      }
	    }
	    raf.close();
	    return lastLine;
	  }

	  
	/*
	 *  获取指定目录下的所有文件(不包括子文件夹)
	 *  dirPath
	 */
	public static ArrayList<File> getDirFiles(String dirPath) {
		File path = new File(dirPath);
        File[] fileArr = path.listFiles();
        ArrayList<File> files = new ArrayList<File>();
        	for (File file : fileArr) {
        		if (file.isFile()) {
        			files.add(file);
        		}
        	}
        return files;

	}
	
	  /**
	   * 拆分文件
	   * @param fileName 待拆分的完整文件名
	   * @param byteSize 按多少字节大小拆分
	   * @return 拆分后的文件名列表
	   */
	public List<String> splitBySize (String fileName, int byteSize) {
		
			RandomAccessFile raf = null;
			List<String> parts = new ArrayList<>();
			File file = new File(fileName);
			int count = (int) Math.ceil(file.length() / (double) byteSize);
			int countLen = (count + "").length();
			String partFileName = null;
		
			try {
				raf = new RandomAccessFile(fileName, "r");
				long totalLen = raf.length();
				CountDownLatch latch = new CountDownLatch(count);
				for (int i = 0; i < count; i++) {
					partFileName = file.getPath() + "." + leftPad((i + 1) + "", countLen, '0') + ".log";
					int readSize = byteSize;
					long startPos = (long) i * byteSize;
					long nextPos = (long) (i + 1) * byteSize;
					if (nextPos > totalLen) {
						readSize = (int) (totalLen - startPos);
					}
					new SplitRunnable(readSize, startPos, partFileName, file, latch).run();
				    parts.add(partFileName);
				}
				latch.await();//等待所有文件写完
				//由于切割时可能会导致行被切断，加工所有的的分割文件,合并行
				mergeRow(parts);
				//return parts;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				log.error(e.getMessage(), e);
				e.printStackTrace();
			}	finally {
				try {
					raf.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					log.error(e.getMessage(), e);
				}
			}
			return parts;
	}
	/**
	   * 合并被切断的行
	   *
	   * @param parts
	   */
	  private void mergeRow(List<String> parts) {
		    List<PartFile> partFiles = new ArrayList<PartFile>();
		    try {
		      //合并被切分的行
		      for (int i=0;i<parts.size();i++) {
		        String partFileName=parts.get(i);
		        File splitFileTemp = new File(partFileName);
		        if (splitFileTemp.exists()) {
		          PartFile partFile = new PartFile();
		          BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(splitFileTemp),"gbk"));
		          String firstRow = reader.readLine();
		          String secondRow = reader.readLine();
		          String endRow = readLastLine(partFileName);
		          partFile.setPartFileName(partFileName);
		          partFile.setFirstRow(firstRow);
		          partFile.setEndRow(endRow);
		          if(i>=1){
		            String prePartFile=parts.get(i - 1);
		            String preEndRow = readLastLine(prePartFile);
		            partFile.setFirstIsFull(getCharCount(firstRow+preEndRow)>getCharCount(secondRow));
		          }
		  
		          partFiles.add(partFile);
		          reader.close();
		        }
		      }
		      //进行需要合并的行的写入
		      for (int i = 0; i < partFiles.size() - 1; i++) {
		        PartFile partFile = partFiles.get(i);
		        PartFile partFileNext = partFiles.get(i + 1);
		        StringBuilder sb = new StringBuilder();
		        if (partFileNext.getFirstIsFull()) {
		          //sb.append("\r\n");
		          sb.append(partFileNext.getFirstRow());
		        } else {
		          sb.append(partFileNext.getFirstRow());
		        }
		        writeLastLine(partFile.getPartFileName(),sb.toString());
		      }
		    } catch (Exception e) {
		      log.error(e.getMessage());
		    }
	  }
	  /**
	   * 得到某个字符出现的次数
	   * @param s
	   * @return
	   */
	  private int getCharCount(String s) {
	    int count = 0;
	    for (int i = 0; i < s.length(); i++) {
	      if (s.charAt(i) == logSeparator) {
	        count++;
	      }
	    }
	    return count;
	  }
	  
	//获取文件总行数 LineNumberReader
	public int getFileLineNumber(File file) {
		int lineNo = 0;
        try {
			LineNumberReader lnr = new LineNumberReader(new FileReader(file));
			lnr.skip(Long.MAX_VALUE);
			lineNo = lnr.getLineNumber() + 1; //行数从0开始计算，为总行数-1
			//log.info(String.format("fileNo is %d", lineNo));
			lnr.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
        return lineNo;
	}
	
	  // 获取文件总行数
	  // 采用BufferedInputStream方式（更快）
	  public int getFileRow(File file) throws IOException {
	    InputStream is = new BufferedInputStream(new FileInputStream(file));
	    byte[] c = new byte[1024];
	    int count = 0;
	    int readChars = 0;
	    while ((readChars = is.read(c)) != -1) {
	      for (int i = 0; i < readChars; ++i) {
	        if (c[i] == '\n')
	          ++count;
	      }
	    }
	    //从0开始计算，最终行数+1
	    count ++;
	    is.close();
	    return count;
	  }

	/*
	 *从缓存中读取文件
	 */
	public void readFile(String fileName, String fileContext){
        RandomAccessFile aFile = null;
        try{
            aFile = new RandomAccessFile(fileName,"rw");
            FileChannel fileChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int bytesRead = fileChannel.read(buf);
            //从buffer中读取数据
            //MappedByteBuffer mbb = fileChannel.map(FileChannel.MapMode.READ_ONLY, 0, aFile.length());
            System.out.println(bytesRead);
            while(bytesRead != -1)
            {
                buf.flip();
                while(buf.hasRemaining())
                {
                	//输出test
                    System.out.print((char)buf.get());
                }
                buf.compact();
                bytesRead = fileChannel.read(buf);
            }
        }catch (IOException e){
            e.printStackTrace();
        }finally{
            try{
                if(aFile != null){
                    aFile.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 文件写入缓存
     * @param fileName 目标文件名
     * @param fileContent 写入的内容
     * @return
     * @throws IOException
     */
	public static boolean writeFile (String fileName, String fileContent) {
        RandomAccessFile aFile = null;
        try{
            aFile = new RandomAccessFile(fileName,"rw");
            FileChannel fileChannel = aFile.getChannel();
            ByteBuffer buf = ByteBuffer.allocate(1024);
            int bytesWrite = fileChannel.write(buf);
        }catch (IOException e){
            e.printStackTrace();
            return false;
        }finally{
            try{
                if(aFile != null){
                    aFile.close();
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }
        return true;
	}
	
    /**
     * 追加内容到指定文件
     * 
     * @param fileName
     * @param fileContent
     * @return
     * @throws IOException
     */
    public static boolean append(String fileName, String fileContent) throws IOException {
        boolean result = false;
        File f = new File(fileName);
        if (f.exists()) {
            RandomAccessFile rFile = new RandomAccessFile(f, "rw");
            byte[] b = fileContent.getBytes();
            long originLen = f.length();
            rFile.setLength(originLen + b.length);
            rFile.seek(originLen);
            rFile.write(b);
            rFile.close();
        }
        result = true;
        return result;
    }
    /*
     * 递归获取指定目录下的所有的文件（不包括文件夹）
     */
    public static ArrayList<File> getAllFiles(String dirPath) {
    	File dir = new File(dirPath);
    	ArrayList<File> files = new ArrayList<File>();
    	if (dir.isDirectory()) {
    		File[] fileArr = dir.listFiles();
    		for (int i = 0; i < files.size(); i++) {
    			File f = fileArr[i];
    			if (f.isFile()) {
    				files.add(f);
    			} else {
    				files.addAll(getAllFiles(f.getPath()));
    			}
    		}
    	}
    	return files;
    }

    
    /**
     * 获取指定目录下特定文件后缀名的文件列表(不包括子文件夹) 
     * @param dirPath  目录路径
     * @param suffix  文件后缀
     * @return
     */
	public static ArrayList<File> getDirFiles(String dirPath, final String suffix) {
		File path = new File(dirPath);
		File[] fileArr = path.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                String lowerName = name.toLowerCase();
                String lowerSuffix = suffix.toLowerCase();
                if (lowerName.endsWith(lowerSuffix)) {
                    return true;
                }
                return false;
            }
        });
		ArrayList<File> files = new ArrayList<File>();
		for (File file : fileArr) {
			if (file.isFile()) {
				files.add(file);
			}
		}
		return files;
	}

    /**
     * 合并文件(2)
     * 
     * @param dirPath 拆分文件所在目录名
     * @param partFileSuffix 拆分文件后缀名
     * @param partFileSize 拆分文件的字节数大小
     * @param mergeFileName 合并后的文件名
     * @throws IOException
     */
	public void mergePartFiles(String dirPath, String partFileSuffix,int partFileSize, 
								String mergeFileName) throws IOException {
        ArrayList<File> partFiles = FileUtils.getDirFiles(dirPath, partFileSuffix);
        Collections.sort(partFiles, new FileComparator());
        RandomAccessFile randomAccessFile = new RandomAccessFile(mergeFileName, "rw");
        randomAccessFile.setLength(partFileSize * (partFiles.size() - 1) + partFiles.get(partFiles.size() - 1).length());
        randomAccessFile.close();
        ThreadPoolExecutor threadPool = new ThreadPoolExecutor( partFiles.size(), partFiles.size() * 3, 1, TimeUnit.SECONDS,
                						new ArrayBlockingQueue<Runnable>(partFiles.size() * 2));
        for (int i = 0; i < partFiles.size(); i++) {
            threadPool.execute(new MergeRunnable(i * partFileSize,
                    mergeFileName, partFiles.get(i)));
        }		
	}

	/*
	 * 根据文件名，比较文件
	 */
	private class FileComparator implements Comparator<File> {
		public int compare(File o1, File o2) {
			return o1.getName().compareToIgnoreCase(o2.getName());

		}
	}
	
	//test read
	 /** 
     * 通过BufferedRandomAccessFile读取文件
     * 
     * @param file     源文件 
     * @param encoding 文件编码 
     * @param pos      偏移量 
     * @param num      读取量 
     * @return pins文件内容，pos当前偏移量 
     */ 
	 public static Map<String, Object> BufferedRandomAccessFileReadLine(File file, String encoding, long pos, int num) {  
	        Map<String, Object> res = Maps.newHashMap();  
	        List<String> pins = Lists.newArrayList();  
	        res.put("pins", pins);  
	        BufferedRandomAccessFile reader = null;  
	        try {  
	            reader = new BufferedRandomAccessFile(file, "r");  
	            reader.seek(pos);  
	            for (int i = 0; i < num; i++) {  
	                String pin = reader.readLine();  
	                if (StringUtils.isBlank(pin)) {  
	                	break;  
	                }  
	                pins.add(new String(pin.getBytes("8859_1"), encoding));  
	            }  
	            res.put("pos", reader.getFilePointer());  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            IOUtils.closeQuietly(reader);  
	        }  
	        return res;  
	    }  
	 
	 /** 
	     * 通过RandomAccessFile读取文件，能出来大数据文件，效率低 
	     * 
	     * @param file     源文件 
	     * @param encoding 文件编码 
	     * @param pos      偏移量 
	     * @param num      读取量 
	     * @return pins文件内容，pos当前偏移量 
	     */ 
	 public static Map<String, Object> readLine(File file, String encoding, long pos, int num) {  
	        Map<String, Object> res = Maps.newHashMap();  
	        List<String> pins = Lists.newArrayList();  
	        res.put("pins", pins);  
	        RandomAccessFile reader = null;  
	        try {  
	            reader = new RandomAccessFile(file, "r");  
	            reader.seek(pos);  
	            for (int i = 0; i < num; i++) {  
	                String pin = reader.readLine();  
	                if (StringUtils.isBlank(pin)) {  
	                	break;  
	                }  
	                pins.add(new String(pin.getBytes("8859_1"), encoding));  
	            }  
	            res.put("pos", reader.getFilePointer());  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            IOUtils.closeQuietly(reader);  
	        }  
	        return res;  
	    }  
	 
	 /** 
	     * 使用LineNumberReader读取文件，1000w行比RandomAccessFile效率高，无法处理1亿条数据 
	     * 
	     * @param file     源文件 
	     * @param encoding 文件编码 
	     * @param index    开始位置 
	     * @param num      读取量 
	     * @return pins文件内容 
	     */ 
	 public static List<String> readLine(File file, String encoding, int index, int num) {  
	        List<String> pins = Lists.newArrayList();  
	        LineNumberReader reader = null;  
	        try {  
	            reader = new LineNumberReader(new InputStreamReader(new FileInputStream(file), encoding));  
	            int lines = 0;  
	            while (true) {  
	                String pin = reader.readLine();  
	                if (StringUtils.isBlank(pin)) {  
	                	break;  
	                }  
	                if (lines >= index) {  
	                	if (StringUtils.isNotBlank(pin)) {  
	                        pins.add(pin);  
	                    }  
	                }  
	                if (num == pins.size()) {  
	                	break;  
	                }  
	                lines++;  
	            }  
	        } catch (Exception e) {  
	            e.printStackTrace();  
	        } finally {  
	            IOUtils.closeQuietly(reader);  
	        }  
	        return pins;  
	    }  	
}
 