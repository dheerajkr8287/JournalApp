package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {

    @Autowired
    private KafkaTemplate<String, SentimentData> kafkaTemplate;

    public void sendSentimentData(String topic, String key, SentimentData sentimentData) {
        try {
            kafkaTemplate.send(topic, key, sentimentData);
            log.info("Sent sentiment data to topic {}: {}", topic, sentimentData);
        } catch (Exception e) {
            log.error("Exception while sending message to Kafka topic {}: ", topic, e);
        }
    }
}
