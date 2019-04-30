package com.irm.parselog.parselog.dao;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import com.irm.parselog.parselog.entity.LogData;

public interface LogDataDao extends MongoRepository<LogData, String>, QueryByExampleExecutor<LogData> {
}
