package com.richal.learn.layered.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * DO (Domain Object) - 领域对象
 *
 * 订单领域对象，包含订单业务逻辑
 *
 * @author Richal
 * @since 2026/03/21
 */
public class OrderDO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 下单用户
     */
    private UserDO user;

    /**
     * 订单金额
     */
    private BigDecimal amount;

    /**
     * 订单状态
     */
    private OrderStatus status;

    /**
     * 订单创建时间
     */
    private LocalDateTime createTime;

    /**
     * 支付时间
     */
    private LocalDateTime payTime;

    /**
     * 订单项列表
     */
    private List<OrderItemDO> items = new ArrayList<>();

    /**
     * 订单状态枚举
     */
    public enum OrderStatus {
        PENDING_PAY(0, "待支付"),
        PAID(1, "已支付"),
        SHIPPED(2, "已发货"),
        COMPLETED(3, "已完成"),
        CANCELLED(4, "已取消");

        private final int code;
        private final String desc;

        OrderStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }
    }

    // ========== 业务方法 ==========

    /**
     * 计算订单总金额
     *
     * @return 订单总金额
     */
    public BigDecimal calculateTotalAmount() {
        return items.stream()
                .map(OrderItemDO::getSubTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * 支付订单
     *
     * @throws IllegalStateException 如果订单状态不是待支付
     */
    public void pay() {
        if (status != OrderStatus.PENDING_PAY) {
            throw new IllegalStateException("Order can only be paid when pending: " + status);
        }
        this.status = OrderStatus.PAID;
        this.payTime = LocalDateTime.now();
    }

    /**
     * 发货
     *
     * @throws IllegalStateException 如果订单未支付
     */
    public void ship() {
        if (status != OrderStatus.PAID) {
            throw new IllegalStateException("Order must be paid before shipping: " + status);
        }
        this.status = OrderStatus.SHIPPED;
    }

    /**
     * 完成订单
     *
     * @throws IllegalStateException 如果订单未发货
     */
    public void complete() {
        if (status != OrderStatus.SHIPPED) {
            throw new IllegalStateException("Order must be shipped before complete: " + status);
        }
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 取消订单
     *
     * @throws IllegalStateException 如果订单已发货或已完成
     */
    public void cancel() {
        if (status == OrderStatus.SHIPPED || status == OrderStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel shipped or completed order: " + status);
        }
        this.status = OrderStatus.CANCELLED;
    }

    /**
     * 添加订单项
     *
     * @param item 订单项
     */
    public void addItem(OrderItemDO item) {
        items.add(item);
        // 重新计算金额
        this.amount = calculateTotalAmount();
    }

    /**
     * 检查是否可以取消
     *
     * @return true 如果订单可以取消
     */
    public boolean canCancel() {
        return status == OrderStatus.PENDING_PAY || status == OrderStatus.PAID;
    }

    // ========== Getter/Setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public UserDO getUser() {
        return user;
    }

    public void setUser(UserDO user) {
        this.user = user;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getPayTime() {
        return payTime;
    }

    public void setPayTime(LocalDateTime payTime) {
        this.payTime = payTime;
    }

    public List<OrderItemDO> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDO> items) {
        this.items = items;
    }

    @Override
    public String toString() {
        return "OrderDO{" +
                "id=" + id +
                ", orderNo='" + orderNo + '\'' +
                ", user=" + (user != null ? user.getUsername() : null) +
                ", amount=" + amount +
                ", status=" + status +
                ", createTime=" + createTime +
                ", payTime=" + payTime +
                ", itemsCount=" + items.size() +
                '}';
    }
}
