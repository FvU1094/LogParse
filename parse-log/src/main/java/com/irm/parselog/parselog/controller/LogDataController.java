package com.irm.parselog.parselog.controller;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.List;
import java.util.RandomAccess;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.irm.parselog.parselog.dao.LogDataDao;
import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.service.LogDataService;
import com.irm.parselog.parselog.service.MongoDbService;
import com.irm.parselog.parselog.util.FileUtils;
import com.irm.parselog.parselog.util.MergeRunnable;
import com.irm.parselog.parselog.util.ParseRunnable;
import com.irm.parselog.parselog.vo.LogDataVo;
import com.irm.parselog.parselog.vo.Result;

@Controller
@RequestMapping("/log")
public class LogDataController {
	private static final Logger log = LoggerFactory.getLogger(LogDataController.class);

	private static Executor threadPool = Executors.newCachedThreadPool();
	//private static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(3, 5, 5, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(5));
	private final LogDataDao logDataDao;
	private final LogDataService logDataService;

	@Autowired
	public LogDataController(LogDataService logDataService, MongoDbService mongoDbService, LogDataDao logDataDao) {
		this.logDataService = logDataService;
		this.logDataDao = logDataDao;
	}

	@RequestMapping(value = "/list", method = RequestMethod.GET)
	public String testList() {
		return "list";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public String upload() {
		return "upload";
	}

	@RequestMapping(value = "/list", method = RequestMethod.POST)
	@ResponseBody
	public Result<LogData> queryLogDataList(LogDataVo logDataVo) {
		return logDataService.queryPageByExample(logDataVo);
	}

	//处理文件上传并解析
	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	@ResponseBody
	public Result singleFileUpload(@RequestParam("file") MultipartFile file) {

		Result result = new Result();
		FileUtils fileUtil = new FileUtils();
		//原始文件大小限制100M
		long originFileSize = 1024 * 1024 * 200;// 200M
		//子文件分割大小
		int partFileSize = 1024 * 1024 * 16; //16M
		
		//1.文件校验
		//判断是否为空
		if (file.isEmpty()) {
			result.setResultCode(Result.RESULTCODE_FAILED);
			result.setResultMsg("Please select a file to upload");
			return result;
		}
/*		//上传文件大小限制
		if (file.getSize() > originFileSize) {
			result.setResultCode(Result.RESULTCODE_FAILED);
			result.setResultMsg("File size is over max_file_size（200MB）");
			return result;
		}*/
		


		String fileName = fileUtil.userDir + System.getProperty("file.separator") + file.getOriginalFilename();
		File tempFile = new File(fileName);
		
		if (tempFile.exists()) {
			tempFile.delete();
			log.info("文件：" + file.getOriginalFilename() + " 已存在！");
		}

		try {
			file.transferTo(tempFile);
		} catch (IllegalStateException e) {
			log.error(e.getMessage(), e);
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		}
		
		long start = System.currentTimeMillis();
		CountDownLatch latch = new CountDownLatch((int) Math.ceil(tempFile.length() / (double) partFileSize));
		
		//判断原始文件是否需要分片
		if (file.getSize() >= partFileSize) {
		    String logFileName = fileName.replaceAll("\\\\", "/");
		    List<String> parts;
			parts = fileUtil.splitBySize(logFileName, partFileSize);
				for(String part:parts){
		        	log.info("partFileName is:"+part);		        	
		        	File partFile = new File(part);
		        	//long startTime = System.currentTimeMillis();
		        	
		        	//logDataService.saveFromFile(partFile, result);
		        	/*ThreadPoolExecutor threadPool = new ThreadPoolExecutor( partFile.size(), partFile.size() * 3, 1, TimeUnit.SECONDS,
    						new ArrayBlockingQueue<Runnable>(partFile.size() * 2));*/

		        	threadPool.execute(new ParseRunnable(partFile, result, latch, logDataDao));
		        	//new ParseRunnable(partFile, result, latch).run();		   
		        	//log.info("eachParseTime:" + (System.currentTimeMillis() - startTime) + "ms");
		        }
				try {
					latch.await();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		  } else {
			  log.info("fileName is : " + file.getOriginalFilename());
			  long startTime = System.currentTimeMillis();
			  logDataService.saveFromFile(tempFile, result);
	          //threadPool.execute(new ParseRunnable(tempFile, result, latch, logDataDao));
			  log.info("parseTime:" + (System.currentTimeMillis() - startTime) + "ms");
		  }
		log.info("总文件长度"+file.getSize()/1024 + "KB , 解析文件耗时:" + (System.currentTimeMillis() - start) + "ms.");
		// irm-web-access 总文件长度327220KB , 解析文件耗时:1211485ms 
		//result: {"success":643006,"fail":19,"total":0,"resultCode":"success","resultMsg":null,"dataList":null}
		return result;
	}
	
}
