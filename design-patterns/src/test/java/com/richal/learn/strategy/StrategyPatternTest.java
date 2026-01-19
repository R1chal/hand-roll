package com.richal.learn.strategy;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * 策略模式测试类
 * 演示策略模式的使用
 *
 * @author Richal
 */
public class StrategyPatternTest {

    @Test
    public void testAlipayPayment() {
        System.out.println("=== 测试支付宝支付 ===");

        PaymentStrategy alipay = new AlipayStrategy("user@alipay.com", "password123");
        PaymentContext context = new PaymentContext(alipay);

        assertTrue(context.validateStrategy());
        String result = context.executePayment(99.99);
        assertTrue(result.contains("支付宝"));
        assertTrue(result.contains("99.99"));
    }

    @Test
    public void testWeChatPayment() {
        System.out.println("=== 测试微信支付 ===");

        PaymentStrategy wechat = new WeChatPayStrategy("wx_openid_123456789", "password123");
        PaymentContext context = new PaymentContext(wechat);

        assertTrue(context.validateStrategy());
        String result = context.executePayment(199.50);
        assertTrue(result.contains("微信"));
        assertTrue(result.contains("199.50"));
    }

    @Test
    public void testCreditCardPayment() {
        System.out.println("=== 测试银行卡支付 ===");

        PaymentStrategy creditCard = new CreditCardStrategy(
            "1234 5678 9012 3456",
            "123",
            "12/25"
        );
        PaymentContext context = new PaymentContext(creditCard);

        assertTrue(context.validateStrategy());
        String result = context.executePayment(299.00);
        assertTrue(result.contains("银行卡"));
        assertTrue(result.contains("299.00"));
    }

    @Test
    public void testSwitchStrategy() {
        System.out.println("=== 测试切换支付策略 ===");

        PaymentContext context = new PaymentContext(
            new AlipayStrategy("user@alipay.com", "password123")
        );

        // 使用支付宝支付
        String result1 = context.executePayment(100.00);
        assertTrue(result1.contains("支付宝"));

        // 切换到微信支付
        context.setStrategy(new WeChatPayStrategy("wx_openid_123", "password123"));
        String result2 = context.executePayment(200.00);
        assertTrue(result2.contains("微信"));

        // 切换到银行卡支付
        context.setStrategy(new CreditCardStrategy("1234567890123456", "123", "12/25"));
        String result3 = context.executePayment(300.00);
        assertTrue(result3.contains("银行卡"));
    }

    @Test
    public void testInvalidPayment() {
        System.out.println("=== 测试无效支付信息 ===");

        // 测试空账号
        PaymentStrategy invalidAlipay = new AlipayStrategy("", "password");
        PaymentContext context = new PaymentContext(invalidAlipay);
        assertFalse(context.validateStrategy());

        String result = context.executePayment(100.00);
        assertTrue(result.contains("失败"));
    }

    @Test
    public void testInvalidAmount() {
        System.out.println("=== 测试无效支付金额 ===");

        PaymentStrategy alipay = new AlipayStrategy("user@alipay.com", "password123");
        PaymentContext context = new PaymentContext(alipay);

        // 测试负数金额
        String result1 = context.executePayment(-100.00);
        assertTrue(result1.contains("必须大于 0"));

        // 测试零金额
        String result2 = context.executePayment(0);
        assertTrue(result2.contains("必须大于 0"));
    }

    @Test
    public void testNullStrategy() {
        System.out.println("=== 测试空策略 ===");

        PaymentContext context = new PaymentContext(null);
        String result = context.executePayment(100.00);
        assertTrue(result.contains("未设置支付策略"));
    }

    @Test
    public void testOrderWithPayment() {
        System.out.println("=== 测试订单支付流程 ===");

        // 创建订单
        Order order = new Order();
        order.addItem("Java编程思想", 108.00);
        order.addItem("设计模式", 89.00);
        order.addItem("深入理解JVM", 79.00);

        order.printOrderDetails();

        // 使用支付宝支付
        PaymentStrategy alipay = new AlipayStrategy("user@alipay.com", "password123");
        PaymentContext context = new PaymentContext(alipay);

        String result = context.executePayment(order.getTotalAmount());
        assertTrue(result.contains("支付宝"));
        assertTrue(result.contains("276.00"));
    }

    @Test
    public void testCardNumberMasking() {
        System.out.println("=== 测试卡号脱敏 ===");

        CreditCardStrategy creditCard = new CreditCardStrategy(
            "1234567890123456",
            "123",
            "12/25"
        );

        String result = creditCard.pay(100.00);
        // 验证卡号被脱敏
        assertFalse(result.contains("1234567890123456"));
        assertTrue(result.contains("****"));
        assertTrue(result.contains("3456"));
    }

    @Test
    public void testInvalidCreditCard() {
        System.out.println("=== 测试无效银行卡 ===");

        // 测试卡号长度不正确
        CreditCardStrategy invalidCard1 = new CreditCardStrategy(
            "12345",  // 卡号太短
            "123",
            "12/25"
        );
        assertFalse(invalidCard1.validate());

        // 测试 CVV 长度不正确
        CreditCardStrategy invalidCard2 = new CreditCardStrategy(
            "1234567890123456",
            "12",  // CVV 太短
            "12/25"
        );
        assertFalse(invalidCard2.validate());
    }

    /**
     * 主方法，演示策略模式的完整使用场景
     */
    public static void main(String[] args) {
        System.out.println("========== 策略模式演示 ==========\n");

        // 场景1：创建订单
        Order order = new Order();
        order.addItem("《设计模式：可复用面向对象软件的基础》", 89.00);
        order.addItem("《Java编程思想（第4版）》", 108.00);
        order.addItem("《深入理解Java虚拟机（第3版）》", 79.00);
        order.addItem("《Effective Java（第3版）》", 68.00);

        order.printOrderDetails();

        // 场景2：使用不同的支付策略
        PaymentContext paymentContext = new PaymentContext(null);

        // 尝试使用支付宝支付
        System.out.println(">>> 客户选择：支付宝支付");
        PaymentStrategy alipay = new AlipayStrategy("zhangsan@alipay.com", "alipay_password");
        paymentContext.setStrategy(alipay);
        paymentContext.executePayment(order.getTotalAmount());

        // 模拟支付失败，切换到微信支付
        System.out.println(">>> 客户切换到：微信支付");
        PaymentStrategy wechat = new WeChatPayStrategy("wx_openid_zhangsan123", "wechat_password");
        paymentContext.setStrategy(wechat);
        paymentContext.executePayment(order.getTotalAmount());

        // 再次切换到银行卡支付
        System.out.println(">>> 客户切换到：银行卡支付");
        PaymentStrategy creditCard = new CreditCardStrategy(
            "6222 0012 3456 7890",
            "123",
            "12/26"
        );
        paymentContext.setStrategy(creditCard);
        paymentContext.executePayment(order.getTotalAmount());

        // 场景3：演示无效支付
        System.out.println(">>> 测试无效支付信息");
        PaymentStrategy invalidAlipay = new AlipayStrategy("", "");
        paymentContext.setStrategy(invalidAlipay);
        paymentContext.executePayment(100.00);

        System.out.println("\n========== 演示结束 ==========");

        // 总结
        System.out.println("\n【策略模式优点】");
        System.out.println("1. 算法可以自由切换");
        System.out.println("2. 避免使用多重条件判断");
        System.out.println("3. 扩展性良好，符合开闭原则");
        System.out.println("\n【适用场景】");
        System.out.println("1. 多个类只有算法或行为上稍有不同的场景");
        System.out.println("2. 算法需要自由切换的场景");
        System.out.println("3. 需要屏蔽算法规则的场景");
    }
}
