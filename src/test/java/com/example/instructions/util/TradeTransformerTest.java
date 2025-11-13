package com.example.instructions.util;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.ZonedDateTime;

import static org.junit.jupiter.api.Assertions.*;

class TradeTransformerTest {

    private TradeTransformer transformer;
    private CanonicalTrade canonicalTrade;

    @BeforeEach
    void setUp() {
        transformer = new TradeTransformer();

        canonicalTrade = new CanonicalTrade();
        canonicalTrade.setSourceSystem("TEST_SYS");

        CanonicalTrade.Trader trader = new CanonicalTrade.Trader();
        trader.setId("TR-001");
        trader.setName("John Doe");
        trader.setAccount("1234567890");
        trader.setSecurity("ABC123");
        canonicalTrade.setTrader(trader);

        CanonicalTrade.Transaction tx = new CanonicalTrade.Transaction();
        tx.setSide("buy");
        tx.setGrossAmount(10000.0);
        canonicalTrade.setTransaction(tx);

        CanonicalTrade.Metadata metadata = new CanonicalTrade.Metadata();
        metadata.setTradeDateTime(ZonedDateTime.parse("2025-11-13T14:30:45.123Z"));
        canonicalTrade.setMetadata(metadata);
    }

    // -----------------------------
    // transformCanonicIntoPlatform()
    // -----------------------------

    @Test
    void shouldTransformCanonicalTradeSuccessfully() {
        PlatformTrade platformTrade = transformer.transformCanonicIntoPlatform(canonicalTrade);

        assertNotNull(platformTrade);
        assertEquals("******7890", platformTrade.getTrade().getAccount());
        assertEquals("ABC123", platformTrade.getTrade().getSecurity());
        assertEquals("B", platformTrade.getTrade().getType());
        assertNotNull(platformTrade.getTrade().getTimestamp());
    }

    @Test
    void shouldThrowExceptionWhenSecurityNoInvalid() {
        canonicalTrade.getTrader().setSecurity("AB12"); // zły format

        PlatformTrade platformTrade = transformer.transformCanonicIntoPlatform(canonicalTrade);
        assertNull(platformTrade, "Powinno zwrócić null przy błędzie transformacji");
    }

    @Test
    void shouldThrowExceptionWhenTradeTypeInvalid() {
        canonicalTrade.getTransaction().setSide("INVALID");

        PlatformTrade platformTrade = transformer.transformCanonicIntoPlatform(canonicalTrade);
        assertNull(platformTrade, "Powinno zwrócić null przy błędnym tradeType");
    }

    // -----------------------------
    // maskAccountNo()
    // -----------------------------

    @Test
    void shouldMaskAccountCorrectly() {
        String masked = invokeMaskAccountNo("1234567890");
        assertEquals("******7890", masked);
    }

    @Test
    void shouldNotMaskShortAccount() {
        String masked = invokeMaskAccountNo("1234");
        assertEquals("1234", masked);
    }

    @Test
    void shouldHandleNullAccountGracefully() {
        assertNull(invokeMaskAccountNo(null));
    }

    private String invokeMaskAccountNo(String input) {
        try {
            var method = TradeTransformer.class.getDeclaredMethod("maskAccountNo", String.class);
            method.setAccessible(true);
            return (String) method.invoke(transformer, input);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
