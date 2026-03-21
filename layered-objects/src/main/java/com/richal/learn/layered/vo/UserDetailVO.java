package com.richal.learn.layered.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * VO (View Object) - 视图对象
 *
 * 返回给前端展示的数据，通常只包含必要字段
 * 根据前端展示需求定制，可能聚合多个领域对象
 *
 * 特点：
 * - 只包含前端需要的字段（敏感字段如密码不应包含）
 * - 字段命名和类型便于前端处理
 * - 可以包含格式化后的数据（如状态描述、格式化时间）
 * - 可能包含嵌套 VO 用于展示关联数据
 *
 * 本示例：用户详情视图对象
 *
 * @author Richal
 * @since 2026/03/21
 */
public class UserDetailVO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号（脱敏处理，如 138****8888）
     */
    private String maskedPhone;

    /**
     * 邮箱（脱敏处理，如 a***@example.com）
     */
    private String maskedEmail;

    /**
     * 账户余额（格式化，如 100.00）
     */
    private String formattedBalance;

    /**
     * 用户状态描述
     */
    private String statusDesc;

    /**
     * 注册时间（格式化字符串）
     */
    private String registerTimeStr;

    /**
     * 用户订单列表（嵌套 VO）
     */
    private List<UserOrderVO> recentOrders;

    /**
     * 用户订单统计 VO（嵌套 VO）
     */
    private OrderStatisticsVO orderStatistics;

    /**
     * 用户订单 VO
     */
    public static class UserOrderVO {
        /**
         * 订单ID
         */
        private Long orderId;

        /**
         * 订单编号
         */
        private String orderNo;

        /**
         * 订单金额（格式化）
         */
        private String amountStr;

        /**
         * 订单状态描述
         */
        private String statusDesc;

        /**
         * 订单状态颜色（用于前端展示）
         */
        private String statusColor;

        /**
         * 创建时间（格式化）
         */
        private String createTimeStr;

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

        public String getAmountStr() {
            return amountStr;
        }

        public void setAmountStr(String amountStr) {
            this.amountStr = amountStr;
        }

        public String getStatusDesc() {
            return statusDesc;
        }

        public void setStatusDesc(String statusDesc) {
            this.statusDesc = statusDesc;
        }

        public String getStatusColor() {
            return statusColor;
        }

        public void setStatusColor(String statusColor) {
            this.statusColor = statusColor;
        }

        public String getCreateTimeStr() {
            return createTimeStr;
        }

        public void setCreateTimeStr(String createTimeStr) {
            this.createTimeStr = createTimeStr;
        }
    }

    /**
     * 订单统计 VO
     */
    public static class OrderStatisticsVO {
        /**
         * 总订单数
         */
        private Integer totalCount;

        /**
         * 待支付订单数
         */
        private Integer pendingPayCount;

        /**
         * 已完成订单数
         */
        private Integer completedCount;

        /**
         * 累计消费金额（格式化）
         */
        private String totalAmountStr;

        // Getters and Setters
        public Integer getTotalCount() {
            return totalCount;
        }

        public void setTotalCount(Integer totalCount) {
            this.totalCount = totalCount;
        }

        public Integer getPendingPayCount() {
            return pendingPayCount;
        }

        public void setPendingPayCount(Integer pendingPayCount) {
            this.pendingPayCount = pendingPayCount;
        }

        public Integer getCompletedCount() {
            return completedCount;
        }

        public void setCompletedCount(Integer completedCount) {
            this.completedCount = completedCount;
        }

        public String getTotalAmountStr() {
            return totalAmountStr;
        }

        public void setTotalAmountStr(String totalAmountStr) {
            this.totalAmountStr = totalAmountStr;
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

    public String getMaskedPhone() {
        return maskedPhone;
    }

    public void setMaskedPhone(String maskedPhone) {
        this.maskedPhone = maskedPhone;
    }

    public String getMaskedEmail() {
        return maskedEmail;
    }

    public void setMaskedEmail(String maskedEmail) {
        this.maskedEmail = maskedEmail;
    }

    public String getFormattedBalance() {
        return formattedBalance;
    }

    public void setFormattedBalance(String formattedBalance) {
        this.formattedBalance = formattedBalance;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getRegisterTimeStr() {
        return registerTimeStr;
    }

    public void setRegisterTimeStr(String registerTimeStr) {
        this.registerTimeStr = registerTimeStr;
    }

    public List<UserOrderVO> getRecentOrders() {
        return recentOrders;
    }

    public void setRecentOrders(List<UserOrderVO> recentOrders) {
        this.recentOrders = recentOrders;
    }

    public OrderStatisticsVO getOrderStatistics() {
        return orderStatistics;
    }

    public void setOrderStatistics(OrderStatisticsVO orderStatistics) {
        this.orderStatistics = orderStatistics;
    }

    @Override
    public String toString() {
        return "UserDetailVO{" +
                "userId=" + userId +
                ", username='" + username + '\'' +
                ", maskedPhone='" + maskedPhone + '\'' +
                ", maskedEmail='" + maskedEmail + '\'' +
                ", formattedBalance='" + formattedBalance + '\'' +
                ", statusDesc='" + statusDesc + '\'' +
                ", registerTimeStr='" + registerTimeStr + '\'' +
                ", recentOrdersCount=" + (recentOrders != null ? recentOrders.size() : 0) +
                ", orderStatistics=" + orderStatistics +
                '}';
    }
}
