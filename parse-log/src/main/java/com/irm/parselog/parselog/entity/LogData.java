package com.irm.parselog.parselog.entity;

import java.io.Serializable;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "LogData", value = "LogData")
public class LogData implements Serializable, Cloneable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -4322287575459757771L;

	public static final String COLLECTION_NAME = "LogData";

	@Id
	private ObjectId _id;

	private String remote_addr;

	private String remote_user;

	private String time_local;

	private String request;

	private String status;

	private String body_bytes_sent;

	private String http_referer;

	private String http_user_agent;

	private String gzip_ratio;

	private String request_time;

	private String upstream_response_time;
	
	private String upstream_addr;

	private String upstream_cache_status;

	private String upstream_status;

	private String file_name;

	public String getFile_name() {
		return file_name;
	}

	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}

	public ObjectId get_id() {
		return _id;
	}

	public void set_id(ObjectId _id) {
		this._id = _id;
	}

	public String getRemote_addr() {
		return remote_addr;
	}

	public void setRemote_addr(String remote_addr) {
		this.remote_addr = remote_addr;
	}

	public String getRemote_user() {
		return remote_user;
	}

	public void setRemote_user(String remote_user) {
		this.remote_user = remote_user;
	}

	public String getTime_local() {
		return time_local;
	}

	public void setTime_local(String time_local) {
		this.time_local = time_local;
	}

	public String getRequest() {
		return request;
	}

	public void setRequest(String request) {
		this.request = request;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getBody_bytes_sent() {
		return body_bytes_sent;
	}

	public void setBody_bytes_sent(String body_bytes_sent) {
		this.body_bytes_sent = body_bytes_sent;
	}

	public String getHttp_referer() {
		return http_referer;
	}

	public void setHttp_referer(String http_referer) {
		this.http_referer = http_referer;
	}

	public String getHttp_user_agent() {
		return http_user_agent;
	}

	public void setHttp_user_agent(String http_user_agent) {
		this.http_user_agent = http_user_agent;
	}

	public String getGzip_ratio() {
		return gzip_ratio;
	}

	public void setGzip_ratio(String gzip_ratio) {
		this.gzip_ratio = gzip_ratio;
	}

	public String getRequest_time() {
		return request_time;
	}

	public void setRequest_time(String request_time) {
		this.request_time = request_time;
	}

	public String getUpstream_response_time() {
		return upstream_response_time;
	}

	public void setUpstream_response_time(String upstream_response_time) {
		this.upstream_response_time = upstream_response_time;
	}

	public String getUpstream_addr() {
		return upstream_addr;
	}

	public void setUpstream_addr(String upstream_addr) {
		this.upstream_addr = upstream_addr;
	}

	public String getUpstream_cache_status() {
		return upstream_cache_status;
	}

	public void setUpstream_cache_status(String upstream_cache_status) {
		this.upstream_cache_status = upstream_cache_status;
	}

	public String getUpstream_status() {
		return upstream_status;
	}

	public void setUpstream_status(String upstream_status) {
		this.upstream_status = upstream_status;
	}


}
