package com.irm.parselog.parselog;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.TypedAggregation;
import org.springframework.test.context.junit4.SpringRunner;

import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.service.MongoDbService;
import com.irm.parselog.parselog.service.impl.MongoDbServiceImpl;
import com.mongodb.AggregationOutput;
import com.mongodb.Block;
import com.mongodb.client.AggregateIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.Sorts;
import com.mongodb.util.JSON;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AggregateTest {
	
	@Autowired
	private MongoTemplate mongoTemplate;
	
	@Autowired
	MongoDbService mongoDbService;
	@Test
	public void testAggregate(){	
		//private static final Logger log = LoggerFactory.getLogger(OtherTest.class);

			MongoCollection<Document> mongoCollection = mongoDbService.createOrFindMongoCollection("LogData");
			String item = "file_name";
			Document doc = new Document();
			doc.put("_id", "$" + item);
			doc.put("count", new Document("$sum", 1));
			
			Document group = new Document("$group", doc);
			Document sort = new Document("$sort",  new Document("count", -1));
			Document limit = new Document("$limit", 5);
			
			List<Document> aggreateList = new ArrayList<Document>();
			aggreateList.add(group);
			aggreateList.add(sort);
			aggreateList.add(limit);
			System.out.println(aggreateList);
			//System.out.println(JSON.serialize(aggreateList));
			
			AggregateIterable<Document> resultSet = mongoCollection.aggregate(aggreateList);
			
			 //  result 输出结果:
			 // [ { "_id" : "irm-web-access.log.06.log" , "count" : 71700} , 
			 //	  { "_id" : "irm-web-access.log.07.log" , "count" : 66585} , 
			 //	  { "_id" : "irm-web-access.log.05.log" , "count" : 66149} , 
			 //	  { "_id" : "irm-web-access.log.04.log" , "count" : 65180} , 
			 //	  { "_id" : "irm-web-access.log.08.log" , "count" : 64399}]

			System.out.println(JSON.serialize(resultSet));
			for (Document result : resultSet) {
				System.out.println(result);
			}
	}
		
	
}
