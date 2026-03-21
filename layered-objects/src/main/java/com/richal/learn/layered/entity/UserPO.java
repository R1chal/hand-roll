package com.richal.learn.layered.entity;

/**
 * PO (Persistent Object) - 持久化对象
 *
 * 与数据库表结构一一对应，用于 DAO 层操作数据库
 * 字段通常与数据库表字段完全一致，包含所有存储字段
 *
 * 本示例：用户表对应的 PO 对象
 *
 * @author Richal
 * @since 2026/03/21
 */
public class UserPO {

    /**
     * 用户ID - 数据库主键
     */
    private Long id;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码 - 加密存储
     */
    private String password;

    /**
     * 手机号
     */
    private String phone;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 状态：0-禁用，1-启用
     */
    private Integer status;

    /**
     * 创建时间
     */
    private java.time.LocalDateTime createTime;

    /**
     * 更新时间
     */
    private java.time.LocalDateTime updateTime;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
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

    public java.time.LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(java.time.LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UserPO{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='[PROTECTED]'" +
                ", phone='" + phone + '\'' +
                ", email='" + email + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
