package com.irm.parselog.parselog.service;

import java.util.List;
import java.util.Map;

public interface ChartService {
	
	//生成查询语句
	List aggregateCountByItems (String item, int limitNo);
	
	
	//执行查询
	Map executeAggregate(List aggregateList);
}
