package com.richal.learn.layered.dto;

import java.math.BigDecimal;

/**
 * DTO (Data Transfer Object) - 数据传输对象
 *
 * 创建订单请求 DTO - 用于接收前端传来的参数
 *
 * @author Richal
 * @since 2026/03/21
 */
public class CreateOrderRequestDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 商品ID
     */
    private Long productId;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品单价
     */
    private BigDecimal unitPrice;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 收货地址
     */
    private String address;

    /**
     * 联系电话
     */
    private String contactPhone;

    /**
     * 备注
     */
    private String remark;

    // ========== Getter/Setter ==========

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public void setContactPhone(String contactPhone) {
        this.contactPhone = contactPhone;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "CreateOrderRequestDTO{" +
                "userId=" + userId +
                ", productId=" + productId +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                ", address='" + address + '\'' +
                ", contactPhone='" + contactPhone + '\'' +
                ", remark='" + remark + '\'' +
                '}';
    }
}
