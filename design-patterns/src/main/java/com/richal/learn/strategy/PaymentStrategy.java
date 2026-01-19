package com.richal.learn.strategy;

/**
 * 支付策略接口
 * 定义了支付行为的统一接口
 *
 * @author Richal
 */
public interface PaymentStrategy {

    /**
     * 执行支付
     *
     * @param amount 支付金额
     * @return 支付结果信息
     */
    String pay(double amount);

    /**
     * 获取支付方式名称
     *
     * @return 支付方式名称
     */
    String getPaymentMethod();

    /**
     * 验证支付信息是否有效
     *
     * @return 如果支付信息有效返回 true，否则返回 false
     */
    boolean validate();
}
