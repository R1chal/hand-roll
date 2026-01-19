package com.richal.learn.strategy;

/**
 * 银行卡支付策略
 * 具体策略类，实现银行卡支付逻辑
 *
 * @author Richal
 */
public class CreditCardStrategy implements PaymentStrategy {

    private String cardNumber;
    private String cvv;
    private String expiryDate;

    public CreditCardStrategy(String cardNumber, String cvv, String expiryDate) {
        this.cardNumber = cardNumber;
        this.cvv = cvv;
        this.expiryDate = expiryDate;
    }

    @Override
    public String pay(double amount) {
        if (!validate()) {
            return "银行卡信息无效，支付失败";
        }
        return String.format("使用银行卡支付 %.2f 元，卡号：%s", amount, maskCardNumber(cardNumber));
    }

    @Override
    public String getPaymentMethod() {
        return "银行卡";
    }

    @Override
    public boolean validate() {
        // 简单验证：卡号、CVV、有效期不为空
        if (cardNumber == null || cardNumber.isEmpty()) {
            return false;
        }
        if (cvv == null || cvv.isEmpty()) {
            return false;
        }
        if (expiryDate == null || expiryDate.isEmpty()) {
            return false;
        }

        // 验证卡号长度（通常是 16 位）
        String cleanCardNumber = cardNumber.replaceAll("\\s+", "");
        if (cleanCardNumber.length() != 16) {
            return false;
        }

        // 验证 CVV 长度（通常是 3 位）
        if (cvv.length() != 3) {
            return false;
        }

        return true;
    }

    /**
     * 脱敏处理卡号
     */
    private String maskCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 4) {
            return "****";
        }
        String cleanNumber = cardNumber.replaceAll("\\s+", "");
        return "**** **** **** " + cleanNumber.substring(cleanNumber.length() - 4);
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }
}
