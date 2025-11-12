package com.example.instructions.service;

import com.example.instructions.model.PlatformTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KafkaPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, PlatformTrade> kafkaTemplate;
    private final DateTimeFormatter timestampFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy-HH:mm:ss Z");
    @Value("${spring.kafka.topic.instructionsout}")
    private String topic;

    private final ConcurrentHashMap<ZonedDateTime, PlatformTrade> tradeMap;

    public KafkaPublisher(KafkaTemplate<String, PlatformTrade> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.tradeMap = new ConcurrentHashMap<>();
    }

    public void addTrade(PlatformTrade trade) {
        if (trade != null && trade.getTrade().getTimestamp() != null) {
            tradeMap.put(trade.getTrade().getTimestamp(), trade);
        }
    }

    public void publishAll() {
        List<PlatformTrade> tradesToSend = tradeMap.values()
                .stream()
                .collect(Collectors.toList());

        for (PlatformTrade trade : tradesToSend) {
            CompletableFuture<SendResult<String, PlatformTrade>> future =
                    kafkaTemplate.send(topic, trade.getTrade().getTimestamp().format(timestampFormatter), trade);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Could not send instruction {} to topic {}",
                            trade.getTrade().getTimestamp().format(timestampFormatter), topic, ex);
                } else {
                    tradeMap.remove(trade.getTrade().getTimestamp());
                    logger.info("Sent instruction {} to topic {}, offset={}",
                            trade.getTrade().getTimestamp().format(timestampFormatter), topic, result.getRecordMetadata().offset());
                }
            });
        }
    }

    public List<PlatformTrade> getAllTrades() {
        return tradeMap.values().stream().collect(Collectors.toList());
    }
}
