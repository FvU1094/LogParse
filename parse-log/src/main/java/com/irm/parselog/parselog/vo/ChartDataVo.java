package com.irm.parselog.parselog.vo;

public class ChartDataVo {
	private String _id;
	private Integer count;
	
	public String get_id() {
		return _id;
	}
	public void set_id(String _id) {
		this._id = _id;
	}
	public Integer getCount() {
		return count;
	}
	public void setCount(Integer count) {
		this.count = count;
	}
	
	public ChartDataVo() {
		super();
	}
	public ChartDataVo(String _id, Integer count) {
		super();
		this._id = _id;
		this.count = count;
	}
	@Override
	public String toString() {
		return "ChartDataVo [_id=" + _id + ", count=" + count + "]";
	}
	
	
}
