package com.irm.parselog.parselog.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.irm.parselog.parselog.dao.LogDataDao;
import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.service.LogDataService;
import com.irm.parselog.parselog.service.MongoDbService;
import com.irm.parselog.parselog.util.DateUtils;
import com.irm.parselog.parselog.util.FileMD5Util;
import com.irm.parselog.parselog.util.FileUtils;
import com.irm.parselog.parselog.util.MultipartFileParam;
import com.irm.parselog.parselog.util.PageUtils;
import com.irm.parselog.parselog.vo.LogDataVo;
import com.irm.parselog.parselog.vo.Result;


@Service("LogDataService")
public class LogDataServiceImpl implements LogDataService {

	private static final Logger logger = LoggerFactory.getLogger(LogDataServiceImpl.class);

	private final LogDataDao logDataDao;
	
	FileUtils fileUtil = new FileUtils();
	
	private long CHUNK_SIZE = 1024 * 1024 * 16;

	@Autowired
	public LogDataServiceImpl(MongoDbService mongoDbService, LogDataDao logDataDao) {
		mongoDbService.createOrFindMongoCollection(LogData.COLLECTION_NAME);
		this.logDataDao = logDataDao;
	}

	@Override
	public Result<LogData> queryPageByExample(LogDataVo logDataVo) {

		Result<LogData> result = new Result<>();

		try {

			ExampleMatcher matcher = ExampleMatcher.matchingAll().withIgnoreNullValues().withIgnorePaths("pageNo", "pageSize");
			Example<LogData> example = Example.of(logDataVo.covert(), matcher);

			long total = logDataDao.count(example);

			int pageSize = logDataVo.getPageSize();
			int totalPage = Math.toIntExact(PageUtils.getTotalPage(pageSize, total));
			result.setTotal(totalPage);

			int curPage = PageUtils.covertPageNoToCurPage(logDataVo.getPageNo(), totalPage);

			Page<LogData> logDataPage = logDataDao.findAll(example, PageRequest.of(curPage, pageSize));

			List<LogData> dataList = logDataPage.getContent();
			result.setDataList(dataList);
			result.setResultCode(Result.RESULTCODE_SUCCESS);
			result.setSuccess(dataList.size());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			result.setResultCode(Result.RESULTCODE_FAILED);
			result.setResultMsg(e.getMessage());
		}

		return result;
	}

	@Override
	public void saveFromFile(File file, Result<LogData> result) {

		BufferedReader bufferedReader = null;
		try {
			logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " uploaded successfully, starts parsing...");
			bufferedReader = new BufferedReader(new FileReader(file));
			//正则表达式优化？
			String regex = "([\\s\\S]*) - ([\\s\\S]*) \\[([\\s\\S]*)\\] \"([\\s\\S]*)\" ([\\s\\S]*) ([\\s\\S]*) \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" \"([\\s\\S]*)\" ";
			Pattern pattern = Pattern.compile(regex);
			List<LogData> logDataList = new ArrayList<>();
			String string;
			int i = 0;
			while ((string = bufferedReader.readLine()) != null) {
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
				if (i > 10000) {
					logDataDao.saveAll(logDataList);
					result.setSuccess(result.getSuccess() + logDataList.size());
					logDataList.clear();
					i = 0;
					logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " 10000 rows saved");
				}
			}

			if (i != 0) {
				logDataDao.saveAll(logDataList);
				result.setSuccess(result.getSuccess() + logDataList.size());
				logDataList.clear();
				logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " " + i +" rows saved");
			}
			logger.info("[" + DateUtils.getCurrentTime() + "] " + file.getName() + " parsed successfully ");
			result.setResultCode(Result.RESULTCODE_SUCCESS);
			String str = result.toString();
			logger.info(str);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
			result.setResultCode(Result.RESULTCODE_FAILED);
			result.setResultMsg(e.getMessage());
			//latch.countDown();
		} finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					logger.error(e.getMessage(), e);
				}
			}
		}
	}

	@Override
	public void uploadFileByMappedByteBuffer(MultipartFileParam param) {
        String fileName = param.getName();
        String uploadDirPath =  fileUtil.userDir + System.getProperty("file.separator") + param.getMd5();
        String tempFileName = fileName + "_tmp";
        File tmpDir = new File(uploadDirPath);
        File tmpFile = new File(uploadDirPath, tempFileName);
        if (!tmpDir.exists()) {
            tmpDir.mkdirs();
        }
        RandomAccessFile tempRaf = null;
        FileChannel fileChannel = null;
		try {
			tempRaf = new RandomAccessFile(tmpFile, "rw");
			fileChannel = tempRaf.getChannel();

			//写入该分片数据
			long offset = CHUNK_SIZE * param.getChunk();
			byte[] fileData = param.getFile().getBytes();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, offset, fileData.length);
			mappedByteBuffer.put(fileData);
			// 释放
			FileMD5Util.freedMappedByteBuffer(mappedByteBuffer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			logger.error("文件上传失败", param.toString());
			e.printStackTrace();			
		} finally {
			try {
				if (fileChannel != null) {
					fileChannel.close();
				}
				if (tempRaf != null) {
					tempRaf.close();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
        

       // boolean isOk = checkAndSetUploadProgress(param, uploadDirPath);
        if (true) {
            boolean flag = renameFile(tmpFile, fileName);
            System.out.println("upload complete !!" + flag + " name=" + fileName);
        }
		
	}

	//文件重命名

    public boolean renameFile(File toBeRenamed, String toFileNewName) {
        //检查要重命名的文件是否存在，是否是文件
        if (!toBeRenamed.exists() || toBeRenamed.isDirectory()) {
            logger.info("File does not exist: " + toBeRenamed.getName());
            return false;
        }
        String p = toBeRenamed.getParent();
        File newFile = new File(p + File.separatorChar + toFileNewName);
        //修改文件名
        return toBeRenamed.renameTo(newFile);
    }
}
