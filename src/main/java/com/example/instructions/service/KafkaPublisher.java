package com.example.instructions.service;

import com.example.instructions.model.PlatformTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class KafkaPublisher {

    private static final Logger logger = LoggerFactory.getLogger(KafkaPublisher.class);

    private final KafkaTemplate<String, PlatformTrade> kafkaTemplate;
    @Value("${spring.kafka.topic.instructionsout}")
    private String topic;

    private final ConcurrentHashMap<String, PlatformTrade> tradeMap;

    public KafkaPublisher(KafkaTemplate<String, PlatformTrade> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
        this.tradeMap = new ConcurrentHashMap<>();
    }

    public void addTrade(PlatformTrade trade) {
        if (trade != null && trade.getTrade().getTimestamp() != null) {
            tradeMap.put(establishTradeKey(trade), trade);
        }
    }

    public void publishAll() {
        List<PlatformTrade> tradesToSend = tradeMap.values()
                .stream()
                .toList();

        for (PlatformTrade trade : tradesToSend) {
            // message keys do not need to be unique, so platformId appears to be ok
            CompletableFuture<SendResult<String, PlatformTrade>> future =
                    kafkaTemplate.send(topic, trade.getPlatformId(), trade);

            future.whenComplete((result, ex) -> {
                if (ex != null) {
                    logger.error("Could not send instruction performed at {} to topic {}",
                            trade.getTrade().getTimestamp().toEpochMilli(), topic, ex);
                } else {
                    tradeMap.remove(establishTradeKey(trade));
                    logger.debug("Sent instruction performed at {} to topic {}, offset={}",
                            trade.getTrade().getTimestamp().toEpochMilli(), topic, result.getRecordMetadata().offset());
                }
            });
        }
    }

    public List<PlatformTrade> getAllTrades() {
        return tradeMap.values().stream().collect(Collectors.toList());
    }

    // establishing unique trade key
    private String establishTradeKey(PlatformTrade trade) {
        return trade.getPlatformId() + "-" + trade.getTrade().getAccount() +
                "-" + trade.getTrade().getTimestamp().getEpochSecond();
    }
}
