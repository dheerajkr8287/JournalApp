package net.engineeringdigest.journalApp.scheduler;

import net.engineeringdigest.journalApp.cache.AppCache;
import net.engineeringdigest.journalApp.entity.JournalEntry;
import net.engineeringdigest.journalApp.entity.User;
import net.engineeringdigest.journalApp.enums.Sentiment;
import net.engineeringdigest.journalApp.repository.UserRepositoryImpl;
import net.engineeringdigest.journalApp.service.EmailService;
import net.engineeringdigest.journalApp.model.SentimentData;
import net.engineeringdigest.journalApp.service.KafkaProducerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
public class UserScheduler {

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepositoryImpl userRepositoryImpl;

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @Autowired
    private AppCache appCache;

    // Cron Expression: second minute hour day-of-month month day-of-week

    // @Scheduled(cron = "0 0 9 * * SUN") // Every Sunday at 9:00 AM
    // @Scheduled(cron = "0 * * ? * *") // Every hour
    @Scheduled(fixedDelay = 30000) // Every 30 seconds
    public void fetchUsersAndSendSaMail() {
        System.out.println("Executing scheduled task every 30 seconds");
        List<User> users = userRepositoryImpl.getUserForSA();
        for (User user : users) {
            List<JournalEntry> journalEntries = user.getJournalEntries();
            List<Sentiment> sentiments = journalEntries.stream()
                    .filter(x -> x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS)))
                    .map(x -> x.getSentiment()).collect(Collectors.toList());
            Map<Sentiment, Integer> sentimentCounts = new HashMap<>();
            for (Sentiment sentiment : sentiments) {
                if (sentiment != null) {
                    sentimentCounts.put(sentiment, sentimentCounts.getOrDefault(sentiment, 0) + 1);
                }
            }

            Sentiment mostFrequentSentiment = null;
            int maxCount = 0;
            for (Map.Entry<Sentiment, Integer> entry : sentimentCounts.entrySet()) {
                if (entry.getValue() > maxCount) {
                    maxCount = entry.getValue();
                    mostFrequentSentiment = entry.getKey();
                }
            }

            if (mostFrequentSentiment != null) {
                SentimentData sentimentData = SentimentData.builder()
                        .email(user.getEmail())
                        .sentiment("Sentiment for Last 7 days: " + mostFrequentSentiment)
                        .build();
                kafkaProducerService.sendSentimentData("weekly-sentiments", user.getEmail(), sentimentData);
            }

            // List<String> filteredEnteries = journalEntries.stream().filter(x ->
            // x.getDate().isAfter(LocalDateTime.now().minus(7, ChronoUnit.DAYS))).map(x ->
            // x.getContent()).collect(Collectors.toList());
            // String entry = String.join(" ", filteredEnteries);
            // String sentiment = sentimentAnalysisService.getSentiment(entry);
            // emailService.sendEmail(user.getEmail(),"Sentiment for last 7
            // days",sentiment);
        }

    }

    @Scheduled(cron = "0 0/10 * ? * *") // Every 10 minutes
    public void clearAppCache() {
        appCache.init();
    }

}
