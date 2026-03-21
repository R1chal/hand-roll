package com.richal.learn.layered.bo;

import com.richal.learn.layered.domain.OrderDO;
import com.richal.learn.layered.domain.UserDO;

import java.math.BigDecimal;
import java.util.List;

/**
 * BO (Business Object) - 业务对象
 *
 * 封装业务逻辑，可能包含多个 DO 的组合
 * 用于处理复杂的业务场景，协调多个领域对象
 *
 * 特点：
 * - 封装业务逻辑（如计算、校验、流程控制）
 * - 可能包含多个 DO 对象
 * - 处理跨领域对象的业务操作
 * - 通常作为 Service 层的辅助对象
 *
 * 本示例：订单处理业务对象，封装下单流程
 *
 * @author Richal
 * @since 2026/03/21
 */
public class OrderProcessBO {

    /**
     * 下单用户
     */
    private UserDO buyer;

    /**
     * 当前处理的订单
     */
    private OrderDO currentOrder;

    /**
     * 用户历史订单列表
     */
    private List<OrderDO> historicalOrders;

    /**
     * 订单折扣率（根据用户等级计算）
     */
    private BigDecimal discountRate;

    /**
     * 是否可以使用优惠券
     */
    private Boolean canUseCoupon;

    /**
     * 用户会员等级
     */
    private MemberLevel memberLevel;

    /**
     * 会员等级枚举
     */
    public enum MemberLevel {
        NORMAL(0, "普通会员", new BigDecimal("1.0")),
        SILVER(1, "银卡会员", new BigDecimal("0.95")),
        GOLD(2, "金卡会员", new BigDecimal("0.90")),
        PLATINUM(3, "铂金会员", new BigDecimal("0.85"));

        private final int level;
        private final String desc;
        private final BigDecimal discount;

        MemberLevel(int level, String desc, BigDecimal discount) {
            this.level = level;
            this.desc = desc;
            this.discount = discount;
        }

        public int getLevel() {
            return level;
        }

        public String getDesc() {
            return desc;
        }

        public BigDecimal getDiscount() {
            return discount;
        }

        /**
         * 根据消费金额计算会员等级
         *
         * @param totalAmount 累计消费金额
         * @return 会员等级
         */
        public static MemberLevel calculateLevel(BigDecimal totalAmount) {
            if (totalAmount.compareTo(new BigDecimal("10000")) >= 0) {
                return PLATINUM;
            } else if (totalAmount.compareTo(new BigDecimal("5000")) >= 0) {
                return GOLD;
            } else if (totalAmount.compareTo(new BigDecimal("1000")) >= 0) {
                return SILVER;
            }
            return NORMAL;
        }
    }

    // ========== 业务方法 ==========

    /**
     * 初始化业务对象
     *
     * @param buyer 买家
     * @param historicalOrders 历史订单
     */
    public void initialize(UserDO buyer, List<OrderDO> historicalOrders) {
        this.buyer = buyer;
        this.historicalOrders = historicalOrders;

        // 计算会员等级
        BigDecimal totalAmount = calculateTotalHistoricalAmount();
        this.memberLevel = MemberLevel.calculateLevel(totalAmount);
        this.discountRate = memberLevel.getDiscount();

        // 判断是否可以使用优惠券（金卡以上可用）
        this.canUseCoupon = memberLevel.getLevel() >= MemberLevel.GOLD.getLevel();
    }

    /**
     * 计算历史订单总金额
     *
     * @return 总金额
     */
    public BigDecimal calculateTotalHistoricalAmount() {
        if (historicalOrders == null || historicalOrders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return historicalOrders.stream()
                .map(OrderDO::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 处理订单创建
     *
     * @param order 待创建的订单
     * @return true 如果创建成功
     * @throws IllegalStateException 如果用户状态异常或余额不足
     */
    public boolean processOrderCreation(OrderDO order) {
        // 检查买家状态
        if (!buyer.isActive()) {
            throw new IllegalStateException("Buyer is not active: " + buyer.getStatus());
        }

        // 应用折扣
        BigDecimal originalAmount = order.getAmount();
        BigDecimal discountedAmount = originalAmount.multiply(discountRate);
        order.setAmount(discountedAmount);

        // 扣除余额
        buyer.deductBalance(discountedAmount);

        // 设置订单状态
        order.setStatus(OrderDO.OrderStatus.PENDING_PAY);

        this.currentOrder = order;
        return true;
    }

    /**
     * 计算订单应付金额（应用折扣后）
     *
     * @param originalAmount 原始金额
     * @return 折扣后金额
     */
    public BigDecimal calculatePayableAmount(BigDecimal originalAmount) {
        return originalAmount.multiply(discountRate);
    }

    /**
     * 获取用户会员信息描述
     *
     * @return 会员信息
     */
    public String getMemberInfo() {
        return String.format("%s (Level: %d, Discount: %s)",
                memberLevel.getDesc(),
                memberLevel.getLevel(),
                discountRate.multiply(new BigDecimal("100")).intValue() + "%");
    }

    /**
     * 检查是否可以购买指定金额的商品
     *
     * @param amount 金额
     * @return true 如果可以购买
     */
    public boolean canAfford(BigDecimal amount) {
        BigDecimal payableAmount = calculatePayableAmount(amount);
        return buyer.getBalance().compareTo(payableAmount) >= 0;
    }

    // ========== Getter/Setter ==========

    public UserDO getBuyer() {
        return buyer;
    }

    public void setBuyer(UserDO buyer) {
        this.buyer = buyer;
    }

    public OrderDO getCurrentOrder() {
        return currentOrder;
    }

    public void setCurrentOrder(OrderDO currentOrder) {
        this.currentOrder = currentOrder;
    }

    public List<OrderDO> getHistoricalOrders() {
        return historicalOrders;
    }

    public void setHistoricalOrders(List<OrderDO> historicalOrders) {
        this.historicalOrders = historicalOrders;
    }

    public BigDecimal getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(BigDecimal discountRate) {
        this.discountRate = discountRate;
    }

    public Boolean getCanUseCoupon() {
        return canUseCoupon;
    }

    public void setCanUseCoupon(Boolean canUseCoupon) {
        this.canUseCoupon = canUseCoupon;
    }

    public MemberLevel getMemberLevel() {
        return memberLevel;
    }

    public void setMemberLevel(MemberLevel memberLevel) {
        this.memberLevel = memberLevel;
    }

    @Override
    public String toString() {
        return "OrderProcessBO{" +
                "buyer=" + (buyer != null ? buyer.getUsername() : null) +
                ", currentOrder=" + (currentOrder != null ? currentOrder.getOrderNo() : null) +
                ", historicalOrdersCount=" + (historicalOrders != null ? historicalOrders.size() : 0) +
                ", discountRate=" + discountRate +
                ", canUseCoupon=" + canUseCoupon +
                ", memberLevel=" + memberLevel +
                '}';
    }
}
