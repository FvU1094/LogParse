package com.irm.parselog.parselog.service;

import org.bson.Document;

import com.mongodb.client.MongoCollection;

public interface MongoDbService {

	MongoCollection<Document> createOrFindMongoCollection(String collectionName);

}
