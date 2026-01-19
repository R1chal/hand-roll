package com.richal.learn.strategy;

import java.util.ArrayList;
import java.util.List;

/**
 * 订单类
 * 表示一个购物订单，包含商品列表和总金额
 *
 * @author Richal
 */
public class Order {

    private List<Item> items;
    private double totalAmount;

    public Order() {
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }

    /**
     * 添加商品到订单
     *
     * @param name  商品名称
     * @param price 商品价格
     */
    public void addItem(String name, double price) {
        items.add(new Item(name, price));
        totalAmount += price;
    }

    /**
     * 获取订单总金额
     *
     * @return 订单总金额
     */
    public double getTotalAmount() {
        return totalAmount;
    }

    /**
     * 获取订单商品列表
     *
     * @return 商品列表
     */
    public List<Item> getItems() {
        return items;
    }

    /**
     * 打印订单详情
     */
    public void printOrderDetails() {
        System.out.println("========== 订单详情 ==========");
        for (int i = 0; i < items.size(); i++) {
            Item item = items.get(i);
            System.out.printf("%d. %s - %.2f 元\n", i + 1, item.getName(), item.getPrice());
        }
        System.out.println("------------------------------");
        System.out.printf("订单总额：%.2f 元\n", totalAmount);
        System.out.println("==============================\n");
    }

    /**
     * 商品内部类
     */
    public static class Item {
        private String name;
        private double price;

        public Item(String name, double price) {
            this.name = name;
            this.price = price;
        }

        public String getName() {
            return name;
        }

        public double getPrice() {
            return price;
        }
    }
}
