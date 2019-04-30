package com.irm.parselog.parselog.vo;

import com.irm.parselog.parselog.entity.LogData;

public class LogDataVo extends LogData {

	private int pageNo;

	private int pageSize;

	public LogData covert() {
		LogData logData = new LogData();
		logData.setUpstream_status(this.getUpstream_status());
		logData.setUpstream_cache_status(this.getUpstream_status());
		logData.setUpstream_addr(this.getUpstream_addr());
		logData.setUpstream_response_time(this.getUpstream_response_time());
		logData.setRequest_time(this.getRequest_time());
		logData.setRequest(this.getRequest());
		logData.setGzip_ratio(this.getGzip_ratio());
		logData.setHttp_user_agent(this.getHttp_user_agent());
		logData.setHttp_referer(this.getHttp_referer());
		logData.setBody_bytes_sent(this.getBody_bytes_sent());
		logData.setStatus(this.getStatus());
		logData.setTime_local(this.getTime_local());
		logData.setRemote_addr(this.getRemote_addr());
		logData.setFile_name(this.getFile_name());
		logData.setRemote_user(this.getRemote_user());
		return logData;
	}

	public int getPageNo() {
		return pageNo;
	}

	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
}
