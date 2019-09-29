package com.nacho.dynamicpropertiesarchaius.service;

import javax.annotation.PostConstruct;

import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.RetryOneTime;
import org.springframework.stereotype.Service;

import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.DynamicWatchedConfiguration;
import com.netflix.config.source.ZooKeeperConfigurationSource;

@Service
public class ArchaiusZookeeperPropertiesService {

    @PostConstruct
    public void init() throws Exception {

        final String CONNECT_STRING = "localhost:2181";

        final int SESSION_TIMEOUT_MS = Integer.valueOf(60 * 1000);
        final int CONNECTION_TIMEOUT_MS = Integer.valueOf(15 * 1000);
        final RetryPolicy RETRY_POLICY = new RetryOneTime(1000);

        final CuratorFramework client = CuratorFrameworkFactory.newClient(CONNECT_STRING, SESSION_TIMEOUT_MS, CONNECTION_TIMEOUT_MS, RETRY_POLICY);
        client.start();

        final ZooKeeperConfigurationSource zkConfigSource = new ZooKeeperConfigurationSource(client, "/conf");
        zkConfigSource.start();

        final DynamicWatchedConfiguration zkDynamicConfig = new DynamicWatchedConfiguration(zkConfigSource);

        ConfigurationManager.getConfigInstance().addConfigurationListener(event -> {
            if (!event.isBeforeUpdate()) {
                System.out.println("Property: " + event.getPropertyName() + ". New value: " + event.getPropertyValue());
            }
        });
        ConfigurationManager.install(zkDynamicConfig);

        /*
         * https://www.hascode.com/2016/04/dynamic-configuration-management-with-netflix-archaius-and-apache-zookeeper-property-
         * files-jmx/
         */

    }

    public String getString(final String key) {
        return DynamicPropertyFactory.getInstance().getStringProperty(key, null).getValue();
    }
}
