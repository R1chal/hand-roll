package com.richal.learn.strategy;

/**
 * 支付上下文类
 * 维护对策略对象的引用，并在运行时切换策略
 *
 * @author Richal
 */
public class PaymentContext {

    private PaymentStrategy strategy;

    /**
     * 构造函数，初始化支付策略
     *
     * @param strategy 支付策略
     */
    public PaymentContext(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 设置支付策略
     *
     * @param strategy 支付策略
     */
    public void setStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    /**
     * 获取当前支付策略
     *
     * @return 当前支付策略
     */
    public PaymentStrategy getStrategy() {
        return strategy;
    }

    /**
     * 执行支付
     *
     * @param amount 支付金额
     * @return 支付结果信息
     */
    public String executePayment(double amount) {
        if (strategy == null) {
            return "未设置支付策略，支付失败";
        }

        if (amount <= 0) {
            return "支付金额必须大于 0";
        }

        System.out.println("=== 开始支付 ===");
        System.out.println("支付方式：" + strategy.getPaymentMethod());
        System.out.println("支付金额：" + amount + " 元");

        String result = strategy.pay(amount);

        System.out.println("支付结果：" + result);
        System.out.println("=== 支付完成 ===\n");

        return result;
    }

    /**
     * 验证当前支付策略
     *
     * @return 如果支付策略有效返回 true，否则返回 false
     */
    public boolean validateStrategy() {
        return strategy != null && strategy.validate();
    }
}
