package com.irm.parselog.parselog.service.impl;

import java.io.UnsupportedEncodingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.irm.parselog.parselog.controller.LogDataController;
import com.irm.parselog.parselog.service.FileService;
import com.irm.parselog.parselog.util.FileReader;

public class FileServiceImpl implements FileService{
	private String encode = "GBK"; 
	private static final Logger log = LoggerFactory.getLogger(LogDataController.class);

	@Override
	public void process(byte[] data) {
		// TODO Auto-generated method stub
		try { 
            log.info(new String(data,encode).toString());
        } catch (UnsupportedEncodingException e) { 
            e.printStackTrace(); 
        } 

/*		FileReader fileReader = new FileReader("D:\\log\\test1.log",100,3); 
		fileReader.registerHanlder(new FileServiceImpl()); 
		fileReader.startRead(); */
    } 
}





	

