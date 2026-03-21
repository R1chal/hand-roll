package com.richal.learn.layered.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO (Data Transfer Object) - 数据传输对象
 *
 * 用于层与层之间的数据传输，如 Service 间调用、远程调用参数等
 *
 * 特点：
 * - 可以聚合多个领域对象的数据
 * - 可以根据传输需求灵活定义字段
 * - 通常不包含业务逻辑
 * - 用于减少调用次数（一次传输所需的所有数据）
 *
 * 本示例：用户订单详情 DTO，包含用户信息和订单列表
 *
 * @author Richal
 * @since 2026/03/21
 */
public class UserOrderDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户手机号
     */
    private String phone;

    /**
     * 用户订单列表
     */
    private List<OrderSummaryDTO> orders;

    /**
     * 订单总数
     */
    private Integer totalOrderCount;

    /**
     * 订单总金额
     */
    private BigDecimal totalOrderAmount;

    /**
     * 数据传输时间
     */
    private LocalDateTime transferTime;

    /**
     * 订单摘要 DTO（嵌套 DTO）
     */
    public static class OrderSummaryDTO {
        /**
         * 订单ID
         */
        private Long orderId;

        /**
         * 订单编号
         */
        private String orderNo;

        /**
         * 订单金额
         */
        private BigDecimal amount;

        /**
         * 订单状态描述
         */
        private String statusDesc;

        /**
         * 创建时间
         */
        private LocalDateTime createTime;

        // Getters and Setters
        public Long getOrderId() {
            return orderId;
        }

        public void setOrderId(Long orderId) {
            this.orderId = orderId;
        }

        public String getOrderNo() {
            return orderNo;
        }

        public void setOrderNo(String orderNo) {
            this.orderNo = orderNo;
        }

        public BigDecimal getAmount() {
            return amount;
        }

        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        public LocalDateTime getCreateTime() {
            return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
            this.createTime = createTime;
        }
    }

    // ========== Getter/Setter ==========

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<OrderSummaryDTO> getOrders() {
        return orders;
    }

    public void setOrders(List<OrderSummaryDTO> orders) {
        this.orders = orders;
    }

    public Integer getTotalOrderCount() {
        return totalOrderCount;
    }

    public void setTotalOrderCount(Integer totalOrderCount) {
        this.totalOrderCount = totalOrderCount;
    }

    public BigDecimal getTotalOrderAmount() {
        return totalOrderAmount;
    }

    public void setTotalOrderAmount(BigDecimal totalOrderAmount) {
        this.totalOrderAmount = totalOrderAmount;
    }

    public LocalDateTime getTransferTime() {
        return transferTime;
    }

    public void setTransferTime(LocalDateTime transferTime) {
        this.transferTime = transferTime;
    }

    @Override
    public String toString() {
        return "UserOrderDTO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", ordersCount=" + (orders != null ? orders.size() : 0) +
                ", totalOrderCount=" + totalOrderCount +
                ", totalOrderAmount=" + totalOrderAmount +
                ", transferTime=" + transferTime +
                '}';
    }
}
