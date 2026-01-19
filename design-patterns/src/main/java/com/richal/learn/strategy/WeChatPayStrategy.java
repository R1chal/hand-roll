package com.richal.learn.strategy;

/**
 * 微信支付策略
 * 具体策略类，实现微信支付逻辑
 *
 * @author Richal
 */
public class WeChatPayStrategy implements PaymentStrategy {

    private String openId;
    private String password;

    public WeChatPayStrategy(String openId, String password) {
        this.openId = openId;
        this.password = password;
    }

    @Override
    public String pay(double amount) {
        if (!validate()) {
            return "微信账号或密码无效，支付失败";
        }
        return String.format("使用微信支付 %.2f 元，OpenID：%s", amount, maskOpenId(openId));
    }

    @Override
    public String getPaymentMethod() {
        return "微信支付";
    }

    @Override
    public boolean validate() {
        // 简单验证：openId 和密码不为空
        return openId != null && !openId.isEmpty()
            && password != null && !password.isEmpty();
    }

    /**
     * 脱敏处理 OpenID
     */
    private String maskOpenId(String openId) {
        if (openId == null || openId.length() <= 8) {
            return "****";
        }
        return openId.substring(0, 4) + "****" + openId.substring(openId.length() - 4);
    }

    public String getOpenId() {
        return openId;
    }
}
