package com.irm.parselog.parselog.controller;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.io.FileUtils;
import org.apache.tomcat.util.http.fileupload.servlet.ServletFileUpload;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.irm.parselog.parselog.service.LogDataService;
import com.irm.parselog.parselog.util.MultipartFileParam;



@Controller
public class IndexController {
	
    private Logger logger = LoggerFactory.getLogger(IndexController.class);

    @Autowired
    LogDataService logDataService;
    
	@RequestMapping("/index")
	public String index() {
		return "index";
	}

	@RequestMapping("/eg/index")
	public String exIndex() {
		return "eg/index";
	}
	
	@RequestMapping("/eg2/index")
	public String exIndex2() {
		return "eg2/index";
	}
	@RequestMapping("/test")
	public String test() {
		return "test";
	}
	
	@RequestMapping("/calendar")
	public String calendar() {
		return "eg2/calendar";
	}
	@RequestMapping("/breakPointUpload")
	public String breakPointUpload() {
		return "breakPointUploadTest";
	}

/*
    *//**
     * 秒传判断，断点判断
     *
     * @return
     *//*
    @RequestMapping(value = "checkFileMd5", method = RequestMethod.POST)
    @ResponseBody
    public Object checkFileMd5(String md5) throws IOException {
        Object processingObj = stringRedisTemplate.opsForHash().get(Constants.FILE_UPLOAD_STATUS, md5);
        if (processingObj == null) {
            return new ResultVo(ResultStatus.NO_HAVE);
        }
        String processingStr = processingObj.toString();
        boolean processing = Boolean.parseBoolean(processingStr);
        String value = stringRedisTemplate.opsForValue().get(Constants.FILE_MD5_KEY + md5);
        if (processing) {
            return new ResultVo(ResultStatus.IS_HAVE, value);
        } else {
            File confFile = new File(value);
            byte[] completeList = FileUtils.readFileToByteArray(confFile);
            List<String> missChunkList = new LinkedList<>();
            for (int i = 0; i < completeList.length; i++) {
                if (completeList[i] != Byte.MAX_VALUE) {
                    missChunkList.add(i + "");
                }
            }
            return new ResultVo<>(ResultStatus.ING_HAVE, missChunkList);
        }
    }*/

    /**
     * 上传文件
     *
     * @param param
     * @param request
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "fileUpload", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity fileUpload(MultipartFileParam param, HttpServletRequest request) {
        boolean isMultipart = ServletFileUpload.isMultipartContent(request);
        if (isMultipart) {
            logger.info("上传文件start");
            logDataService.uploadFileByMappedByteBuffer(param);
            logger.info("上传文件end");
        }
        return ResponseEntity.ok().body("上传成功");
    }

}
