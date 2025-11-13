package com.example.instructions.util;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TradeTransformer {

    private static final Logger logger = LoggerFactory.getLogger(TradeTransformer.class);

    public PlatformTrade transformCanonicIntoPlatform(CanonicalTrade canonicalTrade) {

        CanonicalTrade.Trader trader = canonicalTrade.getTrader();

        try {
            String maskedAccount = maskAccountNo(trader.getAccount());
            String formattedSecurityNo = formatAndValidateSecurityNo(trader.getSecurity());
            CanonicalTrade.Transaction transaction = canonicalTrade.getTransaction();
            String normalizedType = normalizeTradeType(transaction.getSide());

            return new PlatformTrade(canonicalTrade.getSourceSystem(), maskedAccount, formattedSecurityNo,
                    normalizedType, transaction.getGrossAmount(),
                    canonicalTrade.getMetadata().getTradeDateTime().toInstant());
        } catch (IllegalArgumentException e) {
            logger.error("Could not transform instruction!", e);
        }

        return null;
    }

    private String maskAccountNo(String accountNo) {
        logger.info("Masking account number...");
        if (accountNo == null) {
            return null;
        }

        int length = accountNo.length();
        if (length <= 4) {
            return accountNo;
        }

        StringBuilder masked = new StringBuilder();
        masked.append("*".repeat(length - 4));
        masked.append(accountNo.substring(length - 4));

        return masked.toString();

        // TODO test
        // String original = "1234567890";
        // String masked = mask(original);
        // System.out.println(masked); // ****567890
    }


    private String formatAndValidateSecurityNo(String securityNo) {
        logger.info("Formatting and validating security number...");
        if (securityNo == null) {
            return null;
        }
        String securityNoCapital = securityNo.toUpperCase();

        if (securityNoIsValid(securityNoCapital)) {
            return securityNoCapital;
        } else {
            throw new IllegalArgumentException("Security number is invalid!");
        }

        // TODO test
        // System.out.println(validate("ABC123")); // true
        // System.out.println(validate("AB123"));  // false (tylko 2 litery)
        // System.out.println(validate("ABCD123"));// false (4 litery)
        // System.out.println(validate("ABC12A")); // false (ostatnie 3 znaki nie są cyframi)
        // System.out.println(validate("abc123")); // false (małe litery)

    }

    private String normalizeTradeType(String tradeType) {
        logger.info("Normalizing trade type...");
        return switch (tradeType.toUpperCase()) {
            case "BUY" -> "B";
            case "SELL" -> "S";
            default -> throw new IllegalArgumentException("Trade type is invalid!");
        };
        // TODO test
    }

    private boolean securityNoIsValid(String capitalisedSecNo) {
        return capitalisedSecNo.matches("^[A-Z]{3}[0-9]{3}$");
    }
}
