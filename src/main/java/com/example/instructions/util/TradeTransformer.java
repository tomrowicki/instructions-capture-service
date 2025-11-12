package com.example.instructions.util;

import com.example.instructions.model.CanonicalTrade;
import com.example.instructions.model.PlatformTrade;

public class TradeTransformer {

    private static final String PLATFORM_ID = "ACCT123";

    /**
     * Used to mask account number, leaves four last digits unmodified.
     *
     * @param accountNo Unmasked account number
     * @return Masked number
     */
    public String maskAccountNo(String accountNo) {
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


    // TODO javadoc
    public String formatAndValidateSecurityNo(String securityNo) {
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

    // TODO javadoc
    public String normalizeTradeType(String tradeType) {
        return switch (tradeType.toUpperCase()) {
            case "BUY" -> "B";
            case "SELL" -> "S";
            default -> throw new IllegalArgumentException("Trade type is invalid!");
        };
        // TODO test
    }

    public PlatformTrade transformCanonicIntoPlatform(CanonicalTrade canonicalTrade) {
        // due to how canonical trade is modelled we have access to both buyer and seller (finalized trade)
        // it could always be remodelled to indicate each request separately
        // here we assume we always look at things from the buyer's perspective
        CanonicalTrade.Party.PartyDetail partyDetail = canonicalTrade.getParty().getBuyer();
        String maskedAccount = maskAccountNo(partyDetail.getAccount());
        String formattedSecurityNo = formatAndValidateSecurityNo(partyDetail.getSecurity());

        CanonicalTrade.Transaction transaction = canonicalTrade.getTransaction();
        String normalizedType = normalizeTradeType(transaction.getTradeType());

        return new PlatformTrade(PLATFORM_ID, maskedAccount, formattedSecurityNo,
                normalizedType, transaction.getGrossAmount(), canonicalTrade.getMetadata().getReceivedTimestamp());
    }

    private boolean securityNoIsValid(String capitalisedSecNo) {
        return capitalisedSecNo.matches("^[A-Z]{3}[0-9]{3}$");
    }
}
