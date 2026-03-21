package com.richal.learn.layered.domain;

import java.math.BigDecimal;

/**
 * DO (Domain Object) - 领域对象
 *
 * 订单项领域对象
 *
 * @author Richal
 * @since 2026/03/21
 */
public class OrderItemDO {

    /**
     * 订单项ID
     */
    private Long id;

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
     * 计算小计金额
     *
     * @return 小计金额 = 单价 × 数量
     */
    public BigDecimal getSubTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    // ========== Getter/Setter ==========

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "OrderItemDO{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", unitPrice=" + unitPrice +
                ", quantity=" + quantity +
                '}';
    }
}
