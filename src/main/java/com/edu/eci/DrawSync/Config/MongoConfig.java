package com.edu.eci.DrawSync.Config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import jakarta.annotation.PostConstruct;

@Configuration
public class MongoConfig {

    @Autowired
    private MappingMongoConverter mongoConverter;

    @PostConstruct
    public void setUpMongoEscapeCharacterConversion() {
        mongoConverter.setMapKeyDotReplacement("_");
    }
}
