package com.irm.parselog.parselog.util;

public class PageUtils {

	/**
	 * 计算总页数
	 * 
	 * @param pageSize
	 *            分页大小
	 * @param total
	 *            数据总量
	 * @return 总页数
	 */
	public static long getTotalPage(int pageSize, long total) {
		return (total % pageSize == 0) ? (total / pageSize) : (total / pageSize + 1);
	}

	/**
	 * 页码转换。前台传来的值从1开始，查询时从0开始
	 * 
	 * @param pageNo
	 *            前台传来的值
	 * @param totalPage
	 *            总页数
	 * @return 查询的页码
	 */
	public static int covertPageNoToCurPage(int pageNo, int totalPage) {
		if (totalPage <= 0) {
			return 0;
		}
		if (pageNo >= totalPage) {
			return totalPage - 1;
		} else if (pageNo > 0) {
			return pageNo - 1;
		}
		return 0;
	}

}
