package com.nacho.dynamicpropertiesarchaius.service;

import java.util.function.Function;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.bson.Document;
import org.springframework.stereotype.Service;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PollResult;
import com.netflix.config.PolledConfigurationSource;

@Service
public class ArchaiusMongoPropertiesService {

    MongoClient mongoClient = MongoClients.create(MongoClientSettings.builder()//
            .applyConnectionString(new ConnectionString("mongodb://localhost:27017")).build());

    private DynamicConfiguration configuration;

    @PostConstruct
    public void init() throws Exception {
        final PolledConfigurationSource source = (initial, checkPoint) -> {
            final Document first = mongoClient.getDatabase("config").getCollection("conf").find().first();
            return PollResult.createFull(first.keySet().stream().collect(Collectors.toMap(Function.identity(), key -> first.get(key))));
        };
        final AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler(0, 1000, true);
        configuration = new DynamicConfiguration(source, scheduler);
        configuration.addConfigurationListener(event -> {
            if (!event.isBeforeUpdate()) {
                System.out.println("Property: " + event.getPropertyName() + ". New value: " + event.getPropertyValue());
            }
        });
    }

    public String getString(final String key) {
        return configuration.getString(key);
    }
}
