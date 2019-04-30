package com.irm.parselog.parselog.vo;

import java.util.List;

public class Result<T> {

	public static final String RESULTCODE_SUCCESS = "success";

	public static final String RESULTCODE_FAILED = "failed";

	private int success;

	private int fail;

	private int total;

	private String resultCode;

	private String resultMsg;

	private List<T> dataList;

	public Result() {
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public List<T> getDataList() {
		return dataList;
	}

	public void setDataList(List<T> dataList) {
		this.dataList = dataList;
	}

	public int getSuccess() {
		return success;
	}

	public void setSuccess(int success) {
		this.success = success;
	}

	public int getFail() {
		return fail;
	}

	public void setFail(int fail) {
		this.fail = fail;
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public String getResultMsg() {
		return resultMsg;
	}

	public void setResultMsg(String resultMsg) {
		this.resultMsg = resultMsg;
	}
}
