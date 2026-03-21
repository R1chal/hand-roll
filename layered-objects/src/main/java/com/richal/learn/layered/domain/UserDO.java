package com.richal.learn.layered.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DO (Domain Object) - 领域对象
 *
 * 领域模型中的核心对象，包含业务规则和业务逻辑
 * 是业务领域的抽象，不关注技术实现细节（如数据库、网络等）
 *
 * 特点：
 * - 包含业务方法（如计算、校验、状态流转）
 * - 使用业务类型（如 BigDecimal 表示金额）
 * - 富血模型，不只是数据容器
 *
 * @author Richal
 * @since 2026/03/21
 */
public class UserDO {

    /**
     * 用户ID
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 账户余额（使用 BigDecimal 精确计算）
     */
    private BigDecimal balance;

    /**
     * 用户状态
     */
    private UserStatus status;

    /**
     * 注册时间
     */
    private LocalDateTime registerTime;

    /**
     * 用户状态枚举
     */
    public enum UserStatus {
        DISABLED(0, "禁用"),
        ENABLED(1, "启用"),
        FROZEN(2, "冻结");

        private final int code;
        private final String desc;

        UserStatus(int code, String desc) {
            this.code = code;
            this.desc = desc;
        }

        public int getCode() {
            return code;
        }

        public String getDesc() {
            return desc;
        }

        public static UserStatus of(int code) {
            for (UserStatus status : values()) {
                if (status.code == code) {
                    return status;
                }
            }
            throw new IllegalArgumentException("Invalid status code: " + code);
        }
    }

    // ========== 业务方法 ==========

    /**
     * 检查用户是否可用
     *
     * @return true 如果用户状态为启用
     */
    public boolean isActive() {
        return status == UserStatus.ENABLED;
    }

    /**
     * 扣除余额
     *
     * @param amount 扣除金额
     * @throws IllegalStateException 如果余额不足或用户被冻结
     */
    public void deductBalance(BigDecimal amount) {
        if (!isActive()) {
            throw new IllegalStateException("User is not active: " + status);
        }
        if (balance.compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance: " + balance);
        }
        this.balance = balance.subtract(amount);
    }

    /**
     * 增加余额
     *
     * @param amount 增加金额
     */
    public void addBalance(BigDecimal amount) {
        this.balance = balance.add(amount);
    }

    /**
     * 冻结账户
     */
    public void freeze() {
        this.status = UserStatus.FROZEN;
    }

    /**
     * 解冻账户
     */
    public void unfreeze() {
        if (this.status == UserStatus.FROZEN) {
            this.status = UserStatus.ENABLED;
        }
    }

    // ========== Getter/Setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public LocalDateTime getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(LocalDateTime registerTime) {
        this.registerTime = registerTime;
    }

    @Override
    public String toString() {
        return "UserDO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", balance=" + balance +
                ", status=" + status +
                ", registerTime=" + registerTime +
                '}';
    }
}
