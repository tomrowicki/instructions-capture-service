package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    private static final Logger logger = LoggerFactory.getLogger(KafkaListenerService.class);

    private final TradeService tradeService;

    public KafkaListenerService(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.instructionsin}")
    public void listen(CanonicalTrade trade) {
        if (trade != null) {
            logger.info("📥 \u001B[32m Instruction received: \u001B \u001B[0m {} \u001B[0m",
                    trade.getInstructionId());
        }

        tradeService.handleCanonicalTradeInstruction(trade);
    }
}
