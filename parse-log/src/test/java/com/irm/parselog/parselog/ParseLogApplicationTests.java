package com.irm.parselog.parselog;

import java.util.ArrayList;
import java.util.List;

import com.irm.parselog.parselog.vo.LogDataVo;
import org.bson.Document;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.junit4.SpringRunner;

import com.irm.parselog.parselog.dao.LogDataDao;
import com.irm.parselog.parselog.entity.LogData;
import com.irm.parselog.parselog.service.MongoDbService;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ParseLogApplicationTests {

	@Autowired
	MongoDbService mongoDbService;

	@Test
	public void contextLoads() {

		List<Document> documentList = new ArrayList<>();

		for (int i = 0; i < 10; i++) {
			//			Map<String, Object> row = new HashMap<>();
			//			row.put("remote_addr", "remote_addr:" + i);
			//			row.put("remote_user", "remote_user:" + i);
			//			row.put("time_local", "time_local:" + i);
			//			row.put("request", "request:" + i);
			//			row.put("status", "status:" + i);
			//			row.put("body_bytes_sent", "body_bytes_sent:" + i);
			//			row.put("http_referer", "http_referer:" + i);
			//			row.put("http_user_agent", "http_user_agent:" + i);
			//			row.put("gzip_ratio", "gzip_ratio:" + i);
			//			row.put("request_time", "request_time:" + i);
			//			row.put("upstream_response_time", "upstream_response_time:" + i);
			//			row.put("upstream_addr", "upstream_addr:" + i);
			//			row.put("upstream_cache_status", "upstream_cache_status:" + i);
			//			row.put("upstream_status", "upstream_status:" + i);
			//			documentList.add(new Document(row));
		}

		MongoCollection<Document> mongoCollection = mongoDbService.createOrFindMongoCollection("LogData");

		// 删除
		//		mongoCollection.deleteMany(Filters.exists("remote_addr"));

		// 插入
		//		mongoCollection.insertMany(documentList);

		// 删除
		//		mongoCollection.deleteMany(Filters.eq("remote_addr", "remote_addr:2"));

		// 条件查询
		for (Document document : mongoCollection.find(Filters.eq("time_local", "17/Dec/2018:17:07:43 +0800"))) {
			System.out.println(document);
		}

		// 查询所有
		//		for (Document document : mongoCollection.find()) {
		//			System.out.println(document);
		//		}

		// 计数
		System.out.println(mongoCollection.countDocuments());

	}

	@Test
	public void clearAll() {
		MongoCollection<Document> mongoCollection = mongoDbService.createOrFindMongoCollection("LogData");
		mongoCollection.drop();
	}

	@Autowired
	private MongoTemplate mongoTemplate;

	@Autowired
	private LogDataDao logDataDao;

	@Test
	public void testQueryByExample() {
		LogData logData = new LogDataVo();
//		logData.setTime_local("17/Dec/2018:17:07:43 +0800");
		Example<LogData> example = Example.of(logData,ExampleMatcher.matchingAll().withIgnoreNullValues());

		Pageable pageable = PageRequest.of(0, 10);
		Page<LogData> dataList1 = logDataDao.findAll(example,pageable);
		long count = logDataDao.count();

		Query query = new Query(new Criteria().alike(example));

		List<LogData> dataList2 = mongoTemplate.find(query, LogData.class);
		System.out.println(111);

	}

}
