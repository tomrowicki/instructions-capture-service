package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;
import com.example.instructions.util.TradeTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    private final KafkaPublisher publisher;

    public TradeService(KafkaPublisher publisher) {
        this.publisher = publisher;
    }

    public void handleCanonicalTradeInstruction(CanonicalTrade instruction) {
        TradeTransformer tradeTransformer = new TradeTransformer();
        logger.info("Transforming canonical trade form to platform-specific...");
        PlatformTrade platformTrade = tradeTransformer.transformCanonicIntoPlatform(instruction);

        logger.info("Transformation successful, scheduling trade for publishing in platform...");
        publisher.addTrade(platformTrade);
    }

    @Scheduled(fixedRate = 10000)
    private void publishPlatformTradeInstructions() {
        logger.info("Publishing instructions...");
        publisher.publishAll();
    }
}
