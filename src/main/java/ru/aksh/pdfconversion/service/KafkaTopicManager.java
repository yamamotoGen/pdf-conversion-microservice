package ru.aksh.pdfconversion.service;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

@Service
@Slf4j
public class KafkaTopicManager {
    private AdminClient adminClient;

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${app.kafka.topics.success-pdf}")
    private String successPdfTopic;

    @PostConstruct
    public void init() {
        Properties props = new Properties();
        props.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        this.adminClient = AdminClient.create(props);

        createDefaultTopics();
    }

    private void createDefaultTopics() {
        createTopicIfNotExists(successPdfTopic, 3, (short) 2);
    }

    public void createTopicIfNotExists(String topicName, int numPartitions, short replicationFactor) {
        try {
            if (topicExists(topicName)) {
                log.warn("Topic '{}' already exists. Checking parameters...", topicName);
                validateTopicConfig(topicName, numPartitions, replicationFactor);
                return;
            }

            NewTopic newTopic = new NewTopic(topicName, numPartitions, replicationFactor);
            adminClient.createTopics(Collections.singleton(newTopic)).all().get();
            log.info("Topic '{}' created successfully (Partitions: {}, Replication: {})",
                    topicName, numPartitions, replicationFactor);
        } catch (InterruptedException | ExecutionException e) {
            log.error("Failed to create topic '{}'", topicName, e);
            Thread.currentThread().interrupt();
        }
    }

    public boolean topicExists(String topicName) throws ExecutionException, InterruptedException {
        return adminClient.listTopics().names().get().contains(topicName);
    }

    private void validateTopicConfig(String topicName, int expectedPartitions, short expectedReplication)
            throws ExecutionException, InterruptedException {

        DescribeTopicsResult describeResult = adminClient.describeTopics(Collections.singletonList(topicName));
        TopicDescription description = describeResult.topicNameValues().get(topicName).get();

        if (description.partitions().size() != expectedPartitions) {
            log.error("Topic '{}' exists but has {} partitions (expected {})",
                    topicName, description.partitions().size(), expectedPartitions);
        }

        short actualReplication = (short) description.partitions().get(0).replicas().size();
        if (actualReplication != expectedReplication) {
            log.error("Topic '{}' has replication factor {} (expected {})",
                    topicName, actualReplication, expectedReplication);
        }
    }

    @PreDestroy
    public void close() {
        if (adminClient != null) {
            adminClient.close();
            log.info("Kafka AdminClient closed");
        }
    }
}
