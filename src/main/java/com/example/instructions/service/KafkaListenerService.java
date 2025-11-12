package com.example.instructions.service;

import com.example.instructions.model.CanonicalTrade;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaListenerService {

    private final TradeService tradeService;

    public KafkaListenerService(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @KafkaListener(topics = "${spring.kafka.topic.instructionsin}")
    public void listen(CanonicalTrade trade) {
        if (trade != null) {
            // this is just for early testing purposes
            System.out.println("📥 \u001B[32m Instruction received: \u001B[0m");
            System.out.printf("\u001B[33m ID: %s, Symbol: %s, Side: %s, Qty: %.2f, Price: %.2f%n \u001B[0m",
                    trade.getInstructionId(),
                    trade.getInstrument().getSymbol(),
                    trade.getTransaction().getSide(),
                    trade.getTransaction().getQuantity(),
                    trade.getTransaction().getPrice());
        }

        tradeService.handleCanonicalTradeInstruction(trade);
    }
}
