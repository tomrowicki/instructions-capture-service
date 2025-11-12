package com.example.instructions.model;

import java.time.ZonedDateTime;
import java.util.Objects;

public class PlatformTrade {

    private String platformId;
    private Trade trade;

    public PlatformTrade(String platformId, String account, String security, String type,
                         double amount, ZonedDateTime timestamp) {
        this.platformId = platformId;
        this.trade = new Trade(account, security, type, amount, timestamp);
    }

    public static class Trade {

        private String account;
        private String security;
        private String type;
        private double amount;
        private ZonedDateTime timestamp;

        public Trade(String account, String security, String type, double amount, ZonedDateTime timestamp) {
            this.account = account;
            this.security = security;
            this.type = type;
            this.amount = amount;
            this.timestamp = timestamp;
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

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public ZonedDateTime getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(ZonedDateTime timestamp) {
            this.timestamp = timestamp;
        }
    }


    public String getPlatformId() {
        return platformId;
    }

    public void setPlatformId(String platformId) {
        this.platformId = platformId;
    }

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        PlatformTrade that = (PlatformTrade) o;
        return Objects.equals(getPlatformId(), that.getPlatformId()) && Objects.equals(getTrade(), that.getTrade());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getPlatformId(), getTrade());
    }
}
