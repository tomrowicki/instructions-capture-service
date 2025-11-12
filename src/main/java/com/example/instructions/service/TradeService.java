package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;
import com.example.instructions.util.TradeTransformer;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class TradeService {

    private final KafkaPublisher publisher;

    public TradeService(KafkaPublisher publisher) {
        this.publisher = publisher;
    }

    public void handleCanonicalTradeInstruction(CanonicalTrade instruction) {
        TradeTransformer tradeTransformer = new TradeTransformer();
        PlatformTrade platformTrade = tradeTransformer.transformCanonicIntoPlatform(instruction);

        publisher.addTrade(platformTrade);
    }

    @Scheduled(fixedRate = 10000)
    private void publishPlatformTradeInstructions() {
        publisher.publishAll();
    }
}
