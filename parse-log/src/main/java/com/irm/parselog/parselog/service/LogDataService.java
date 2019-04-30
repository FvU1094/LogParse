package com.irm.parselog.parselog.service;

import java.io.File;
import java.util.concurrent.CountDownLatch;

import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.util.MultipartFileParam;
import com.irm.parselog.parselog.vo.LogDataVo;
import com.irm.parselog.parselog.vo.Result;


public interface LogDataService {

	/**
	 *
	 * @param logDataVo
	 *            example to query
	 * @return data
	 */
	Result<LogData> queryPageByExample(LogDataVo logDataVo);

	void saveFromFile(File file, Result<LogData> result);
	
	void uploadFileByMappedByteBuffer(MultipartFileParam param);

}
