package com.irm.parselog.parselog.service.impl;

import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import com.irm.parselog.parselog.service.MongoDbService;
import com.mongodb.client.MongoCollection;

@Service
public class MongoDbServiceImpl implements MongoDbService {

	private final MongoTemplate mongoTemplate;

	@Autowired
	public MongoDbServiceImpl(MongoTemplate mongoTemplate) {
		this.mongoTemplate = mongoTemplate;
	}

	@Override
	public MongoCollection<Document> createOrFindMongoCollection(String collectionName) {
		boolean collectionExists = mongoTemplate.collectionExists(collectionName);
		if (collectionExists) {
			return mongoTemplate.getCollection(collectionName);
		}
		return mongoTemplate.createCollection(collectionName);
	}
}
