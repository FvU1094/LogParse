package com.irm.parselog.parselog.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.service.ChartService;
import com.irm.parselog.parselog.service.MongoDbService;
import com.irm.parselog.parselog.vo.ChartDataVo;
import com.mongodb.client.AggregateIterable;
import com.mongodb.util.JSON;

import net.sf.json.JSONArray;


@Service("ChartService")
public class ChartServiceImpl implements ChartService{
	
	private static final Logger log = LoggerFactory.getLogger(ChartServiceImpl.class);
	
	@Autowired
	MongoDbService mongoDbService;
	
	@Override
	public List aggregateCountByItems(String item, int limitNo) {		
		
		Document doc = new Document();
		doc.put("_id", "$" + item);
		doc.put("count", new Document("$sum", 1));
		
		Document group = new Document("$group", doc);
		Document sort = new Document("$sort",  new Document("count", -1));
		Document limit = new Document("$limit", limitNo);
		
		List<Document> aggregateList = new ArrayList<Document>();
		aggregateList.add(group);
		aggregateList.add(sort);
		aggregateList.add(limit);
		
		return aggregateList;
		
	}
	
	//执行aggregate，将document转化为json
	@Override
	public Map executeAggregate(List aggregateList) {
		AggregateIterable<Document> resultSet = mongoDbService.createOrFindMongoCollection(LogData.COLLECTION_NAME).aggregate(aggregateList);
		//FindIterable<Document> resultSet = mongoDbService.createOrFindMongoCollection(LogData.COLLECTION_NAME).aggregate(aggregateList);
		String result = JSON.serialize(resultSet);
		
		//将result转化为list
		List<ChartDataVo> list = new ArrayList<>();		
		JSONArray jsonArray = JSONArray.fromObject(result);
		
		//[ChartDataVo [_id=10.221.128.226, count=30221], ChartDataVo [_id=10.9.33.152, count=29244], ChartDataVo [_id=10.10.138.35, count=22828], ChartDataVo [_id=10.10.84.186, count=21523], ChartDataVo [_id=10.9.26.199, count=17528]]
		list = (List) JSONArray.toCollection(jsonArray, ChartDataVo.class);

		Map map = new HashMap<>();
		map.put("list", list);
		return map;
	}
	
}
