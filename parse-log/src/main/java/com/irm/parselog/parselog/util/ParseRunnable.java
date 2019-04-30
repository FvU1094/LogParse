package com.irm.parselog.parselog.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.irm.parselog.parselog.dao.LogDataDao;
import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.vo.Result;

public class ParseRunnable implements Runnable{
	
	private static final Logger logger = LoggerFactory.getLogger(ParseRunnable.class);
	private static final String regex = "([\\s\\S]*) - ([\\s\\S]*) \\[([\\s\\S]*)\\] \"([\\s\\S]*)\" ([\\s\\S]*) ([\\s\\S]*) \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" ";
	@Autowired
	private LogDataDao logDataDao;
	
	File file; 
	Result<LogData> result;
	CountDownLatch latch;
	
	public ParseRunnable(File file, Result<LogData> result, CountDownLatch latch, LogDataDao logDataDao) {
		this.file = file;
		this.result = result;
		this.latch = latch;
		this.logDataDao = logDataDao;
	} 
	@Override
	public void run() {
		BufferedReader bufferedReader = null;
		RandomAccessFile randomAccessFile = null;
		try {
			logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " uploaded successfully, starts parsing...");
			bufferedReader = new BufferedReader(new FileReader(file));
			randomAccessFile = new RandomAccessFile(file, "r");
			Pattern pattern = Pattern.compile(regex);
			List<LogData> logDataList = new ArrayList<>();
			String string;
			int i = 0;
			while ((string = randomAccessFile.readLine()) != null) {
				Matcher matcher = pattern.matcher(string);
				if (matcher.find()) {
					if (matcher.groupCount() == 14) {
						LogData logData = new LogData();
						logData.setFile_name(file.getName());
						logData.setRemote_addr(matcher.group(1));
						logData.setRemote_user(matcher.group(2));
						logData.setTime_local(matcher.group(3));
						logData.setRequest(matcher.group(4));
						logData.setStatus(matcher.group(5));
						logData.setBody_bytes_sent(matcher.group(6));
						logData.setHttp_referer(matcher.group(7));
						logData.setHttp_user_agent(matcher.group(8));
						logData.setGzip_ratio(matcher.group(9));
						logData.setRequest_time(matcher.group(10));
						logData.setUpstream_response_time(matcher.group(11));
						logData.setUpstream_addr(matcher.group(12));
						logData.setUpstream_cache_status(matcher.group(13));
						logData.setUpstream_status(matcher.group(14));

						logDataList.add(logData);

					}
				} else {
					result.setFail(result.getFail() + 1);
					logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " failed to parse at:" + string);
				}

				i++;
				if (i > 1000) {
					logDataDao.saveAll(logDataList);
					result.setSuccess(result.getSuccess() + logDataList.size());
					logDataList.clear();
					i = 0;
					logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " 1000 rows saved");
				}
			}

			if (i != 0) {
				logDataDao.saveAll(logDataList);
				logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " " + i + " rows saved");
				result.setSuccess(result.getSuccess() + logDataList.size());
				logDataList.clear();
			}
			logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " parsed successfully ");
			result.setResultCode(Result.RESULTCODE_SUCCESS);
			logger.info(result.getResultCode() + result.getTotal() + result.getResultMsg());
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			result.setResultCode(Result.RESULTCODE_FAILED);
			result.setResultMsg(e.getMessage());
			latch.countDown();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
			if (randomAccessFile != null){
				try {
					randomAccessFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					logger.error(e.getMessage(), e);
				}
			}

		}
		latch.countDown();
		
	}

}
