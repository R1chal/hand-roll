package com.richal.learn.layered.entity;

/**
 * PO (Persistent Object) - 持久化对象
 *
 * 订单表对应的 PO 对象
 *
 * @author Richal
 * @since 2026/03/21
 */
public class OrderPO {

    /**
     * 订单ID
     */
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 订单编号
     */
    private String orderNo;

    /**
     * 订单金额（分）
     */
    private Long amount;

    /**
     * 订单状态：0-待支付，1-已支付，2-已发货，3-已完成
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    // ========== Getter/Setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public Long getAmount() {
        return amount;
    }

    public void setAmount(Long amount) {
        this.amount = amount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public java.time.LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(java.time.LocalDateTime createTime) {
        this.createTime = createTime;
    }

    @Override
    public String toString() {
        return "OrderPO{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderNo='" + orderNo + '\'' +
                ", amount=" + amount +
                ", status=" + status +
                ", createTime=" + createTime +
                '}';
    }
}
