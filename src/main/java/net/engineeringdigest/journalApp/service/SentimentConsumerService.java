package net.engineeringdigest.journalApp.service;

import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.model.SentimentData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SentimentConsumerService {

    @Autowired
    private EmailService emailService;

    @KafkaListener(topics = "weekly-sentiments", groupId = "weekly-sentiment-group")
    public void consume(SentimentData sentimentData) {
        log.info("Consumed sentiment data from Kafka: {}", sentimentData);
        if (sentimentData != null && sentimentData.getEmail() != null) {
            sendEmail(sentimentData);
        }
    }

    private void sendEmail(SentimentData sentimentData) {
        emailService.sendEmail(
                sentimentData.getEmail(),
                "Sentiment for Last 7 days",
                sentimentData.getSentiment()
        );
    }
}
