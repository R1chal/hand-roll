package com.richal.learn.strategy;

/**
 * 支付宝支付策略
 * 具体策略类，实现支付宝支付逻辑
 *
 * @author Richal
 */
public class AlipayStrategy implements PaymentStrategy {

    private String account;
    private String password;

    public AlipayStrategy(String account, String password) {
        this.account = account;
        this.password = password;
    }

    @Override
    public String pay(double amount) {
        if (!validate()) {
            return "支付宝账号或密码无效，支付失败";
        }
        return String.format("使用支付宝支付 %.2f 元，账号：%s", amount, maskAccount(account));
    }

    @Override
    public String getPaymentMethod() {
        return "支付宝";
    }

    @Override
    public boolean validate() {
        // 简单验证：账号和密码不为空
        return account != null && !account.isEmpty()
            && password != null && !password.isEmpty();
    }

    /**
     * 脱敏处理账号信息
     */
    private String maskAccount(String account) {
        if (account == null || account.length() <= 4) {
            return "****";
        }
        return account.substring(0, 3) + "****" + account.substring(account.length() - 2);
    }

    public String getAccount() {
        return account;
    }
}
