package com.example.instructions.model;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Objects;

// Mostly generated class, courtesy of ChatGPT
public class CanonicalTrade {

    private String instructionId;
    private String sourceSystem;

    private Instrument instrument;
    private Trader trader;
    private Transaction transaction;
    private Metadata metadata;


    public static class Instrument {
        private String symbol;
        private String isin;
        private String instrumentType;

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getIsin() {
            return isin;
        }

        public void setIsin(String isin) {
            this.isin = isin;
        }

        public String getInstrumentType() {
            return instrumentType;
        }

        public void setInstrumentType(String instrumentType) {
            this.instrumentType = instrumentType;
        }
    }


    public static class Trader {
        private String id;
        private String name;
        private String account;
        private String security;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAccount() {
            return account;
        }

        public void setAccount(String account) {
            this.account = account;
        }

        public String getSecurity() {
            return security;
        }

        public void setSecurity(String security) {
            this.security = security;
        }
    }

    public static class Transaction {
        private String side;
        private double quantity;
        private double price;
        private String currency;
        private double grossAmount;
        private String tradeType;
        private String executionVenue;

        public String getSide() {
            return side;
        }

        public void setSide(String side) {
            this.side = side;
        }

        public double getQuantity() {
            return quantity;
        }

        public void setQuantity(double quantity) {
            this.quantity = quantity;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }

        public String getCurrency() {
            return currency;
        }

        public void setCurrency(String currency) {
            this.currency = currency;
        }

        public double getGrossAmount() {
            return grossAmount;
        }

        public void setGrossAmount(double grossAmount) {
            this.grossAmount = grossAmount;
        }

        public String getTradeType() {
            return tradeType;
        }

        public void setTradeType(String tradeType) {
            this.tradeType = tradeType;
        }

        public String getExecutionVenue() {
            return executionVenue;
        }

        public void setExecutionVenue(String executionVenue) {
            this.executionVenue = executionVenue;
        }
    }

    public static class Metadata {
        private ZonedDateTime tradeDateTime;
        private String correlationId;
        private String sourceMessageFormat;
        private String sourceMessageId;
        private Map<String, Object> additionalProperties;

        public ZonedDateTime getTradeDateTime() {
            return tradeDateTime;
        }

        public void setTradeDateTime(ZonedDateTime tradeDateTime) {
            this.tradeDateTime = tradeDateTime;
        }

        public String getCorrelationId() {
            return correlationId;
        }

        public void setCorrelationId(String correlationId) {
            this.correlationId = correlationId;
        }

        public String getSourceMessageFormat() {
            return sourceMessageFormat;
        }

        public void setSourceMessageFormat(String sourceMessageFormat) {
            this.sourceMessageFormat = sourceMessageFormat;
        }

        public String getSourceMessageId() {
            return sourceMessageId;
        }

        public void setSourceMessageId(String sourceMessageId) {
            this.sourceMessageId = sourceMessageId;
        }

        public Map<String, Object> getAdditionalProperties() {
            return additionalProperties;
        }

        public void setAdditionalProperties(Map<String, Object> additionalProperties) {
            this.additionalProperties = additionalProperties;
        }
    }

    public String getInstructionId() {
        return instructionId;
    }

    public void setInstructionId(String instructionId) {
        this.instructionId = instructionId;
    }

    public String getSourceSystem() {
        return sourceSystem;
    }

    public void setSourceSystem(String sourceSystem) {
        this.sourceSystem = sourceSystem;
    }

    public Instrument getInstrument() {
        return instrument;
    }

    public void setInstrument(Instrument instrument) {
        this.instrument = instrument;
    }

    public Trader getTrader() {
        return trader;
    }

    public void setTrader(Trader trader) {
        this.trader = trader;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTransaction(Transaction transaction) {
        this.transaction = transaction;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CanonicalTrade that = (CanonicalTrade) o;
        return Objects.equals(getInstructionId(), that.getInstructionId()) && Objects.equals(getSourceSystem(), that.getSourceSystem()) && Objects.equals(getInstrument(), that.getInstrument()) && Objects.equals(getTrader(), that.getTrader()) && Objects.equals(getTransaction(), that.getTransaction()) && Objects.equals(getMetadata(), that.getMetadata());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getInstructionId(), getSourceSystem(), getInstrument(), getTrader(), getTransaction(), getMetadata());
    }
}
