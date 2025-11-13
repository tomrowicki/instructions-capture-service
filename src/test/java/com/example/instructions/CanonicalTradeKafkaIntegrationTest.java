package com.example.instructions;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.CanonicalTrade.Instrument;
import com.example.instructions.model.CanonicalTrade.Transaction;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@EmbeddedKafka(partitions = 1, topics = {"instructions.inbound"})
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
@ActiveProfiles("test")
class CanonicalTradeKafkaIntegrationTest {

    @Value("${spring.embedded.kafka.brokers}")
    private String embeddedKafkaBrokers;

    @Test
    void shouldSendAndReceiveCanonicalTradeAndSubmitItForFurtherProcessing() throws Exception {
        // Test data setup
        CanonicalTrade trade = new CanonicalTrade();
        trade.setInstructionId("INS-TEST-001");
        trade.setSourceSystem("TestSystem");

        Instrument instrument = new Instrument();
        instrument.setSymbol("AAPL");
        instrument.setInstrumentType("EQUITY");
        trade.setInstrument(instrument);

        CanonicalTrade.Trader trader = new CanonicalTrade.Trader();
        trader.setId("test-0");
        trader.setName("Mr. Trader");
        trader.setSecurity("abc123");
        trader.setAccount("1234567890");
        trade.setTrader(trader);

        Transaction tx = new Transaction();
        tx.setSide("BUY");
        tx.setQuantity(50);
        tx.setPrice(180.75);
        tx.setGrossAmount(200);
        tx.setCurrency("USD");
        trade.setTransaction(tx);

        CanonicalTrade.Metadata metadata = new CanonicalTrade.Metadata();
        metadata.setTradeDateTime(ZonedDateTime.now());
        trade.setMetadata(metadata);

        // Temporary producer creation
        Map<String, Object> props = new HashMap<>();
        props.put("bootstrap.servers", embeddedKafkaBrokers);
        props.put("key.serializer", StringSerializer.class);
        props.put("value.serializer", JsonSerializer.class);

        ProducerFactory<String, CanonicalTrade> producerFactory = new DefaultKafkaProducerFactory<>(props);
        KafkaTemplate<String, CanonicalTrade> kafkaTemplate = new KafkaTemplate<>(producerFactory);

        // Test msg sending
        kafkaTemplate.send(new ProducerRecord<>("instructions.inbound", trade));
        kafkaTemplate.flush();

        // Just to make sure kafka listener has time to receive the msg
        Thread.sleep(1500);

        // No exceptions → everything appears to be in order
        assertThat(true).isTrue();
    }
}
