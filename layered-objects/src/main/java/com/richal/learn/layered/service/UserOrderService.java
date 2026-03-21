package com.richal.learn.layered.service;

import com.richal.learn.layered.bo.OrderProcessBO;
import com.richal.learn.layered.domain.OrderDO;
import com.richal.learn.layered.domain.OrderItemDO;
import com.richal.learn.layered.domain.UserDO;
import com.richal.learn.layered.dto.CreateOrderRequestDTO;
import com.richal.learn.layered.dto.UserOrderDTO;
import com.richal.learn.layered.entity.OrderPO;
import com.richal.learn.layered.entity.UserPO;
import com.richal.learn.layered.mapper.ObjectConverter;
import com.richal.learn.layered.vo.UserDetailVO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 用户订单服务
 *
 * 演示各层对象在实际业务中的流转
 *
 * @author Richal
 * @since 2026/03/21
 */
public class UserOrderService {

    // 模拟数据库
    private final List<UserPO> userPOList = new ArrayList<>();
    private final List<OrderPO> orderPOList = new ArrayList<>();

    /**
     * 初始化模拟数据
     */
    public UserOrderService() {
        initMockData();
    }

    private void initMockData() {
        // 初始化用户数据
        UserPO user1 = new UserPO();
        user1.setId(1L);
        user1.setUsername("zhangsan");
        user1.setPassword("encrypted_password");
        user1.setPhone("13800138000");
        user1.setEmail("zhangsan@example.com");
        user1.setStatus(1);
        user1.setCreateTime(LocalDateTime.now().minusDays(30));
        user1.setUpdateTime(LocalDateTime.now());
        userPOList.add(user1);

        // 初始化订单数据
        OrderPO order1 = new OrderPO();
        order1.setId(1L);
        order1.setUserId(1L);
        order1.setOrderNo("ORD202503210001");
        order1.setAmount(10000L); // 100元 = 10000分
        order1.setStatus(3); // 已完成
        order1.setCreateTime(LocalDateTime.now().minusDays(10));
        orderPOList.add(order1);

        OrderPO order2 = new OrderPO();
        order2.setId(2L);
        order2.setUserId(1L);
        order2.setOrderNo("ORD202503210002");
        order2.setAmount(5000L); // 50元
        order2.setStatus(1); // 已支付
        order2.setCreateTime(LocalDateTime.now().minusDays(5));
        orderPOList.add(order2);
    }

    // ========== Service 方法 ==========

    /**
     * 获取用户详情（返回 VO）
     *
     * 流程：PO -> DO -> VO
     *
     * @param userId 用户ID
     * @return 用户详情 VO
     */
    public UserDetailVO getUserDetail(Long userId) {
        // 1. 从数据库获取 PO
        UserPO userPO = userPOList.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);

        if (userPO == null) {
            return null;
        }

        // 2. PO -> DO（领域对象）
        UserDO userDO = ObjectConverter.toUserDO(userPO);

        // 3. 获取用户订单
        List<OrderPO> userOrderPOs = orderPOList.stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());
        List<OrderDO> userOrderDOs = ObjectConverter.toOrderDOList(userOrderPOs);

        // 4. DO -> VO（视图对象）
        return convertToUserDetailVO(userDO, userOrderDOs);
    }

    /**
     * 创建订单
     *
     * 流程：DTO -> DO -> BO -> PO
     *
     * @param requestDTO 创建订单请求 DTO
     * @return 是否创建成功
     */
    public boolean createOrder(CreateOrderRequestDTO requestDTO) {
        // 1. 获取用户信息（PO -> DO）
        UserPO userPO = userPOList.stream()
                .filter(u -> u.getId().equals(requestDTO.getUserId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        UserDO userDO = ObjectConverter.toUserDO(userPO);
        // 设置余额（模拟）
        userDO.setBalance(new BigDecimal("1000.00"));

        // 2. 获取历史订单
        List<OrderPO> userOrderPOs = orderPOList.stream()
                .filter(o -> o.getUserId().equals(requestDTO.getUserId()))
                .collect(Collectors.toList());
        List<OrderDO> historicalOrders = ObjectConverter.toOrderDOList(userOrderPOs);

        // 3. 初始化 BO（业务对象）
        OrderProcessBO orderBO = new OrderProcessBO();
        orderBO.initialize(userDO, historicalOrders);

        System.out.println("会员等级：" + orderBO.getMemberInfo());

        // 4. 创建订单 DO
        OrderDO newOrder = new OrderDO();
        newOrder.setOrderNo("ORD" + System.currentTimeMillis());
        newOrder.setUser(userDO);

        // 添加订单项
        OrderItemDO item = new OrderItemDO();
        item.setProductName(requestDTO.getProductName());
        item.setUnitPrice(requestDTO.getUnitPrice());
        item.setQuantity(requestDTO.getQuantity());
        newOrder.addItem(item);

        // 5. 使用 BO 处理订单创建
        boolean success = orderBO.processOrderCreation(newOrder);

        if (success) {
            // 6. DO -> PO（保存到数据库）
            OrderPO orderPO = ObjectConverter.toOrderPO(newOrder);
            orderPOList.add(orderPO);

            System.out.println("订单创建成功，折扣后金额：" + newOrder.getAmount());
        }

        return success;
    }

    /**
     * 获取用户订单数据（用于远程调用，返回 DTO）
     *
     * 流程：PO -> DO -> DTO
     *
     * @param userId 用户ID
     * @return 用户订单 DTO
     */
    public UserOrderDTO getUserOrderData(Long userId) {
        // 1. 获取用户 PO
        UserPO userPO = userPOList.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst()
                .orElse(null);

        if (userPO == null) {
            return null;
        }

        // 2. 获取订单 PO
        List<OrderPO> orderPOs = orderPOList.stream()
                .filter(o -> o.getUserId().equals(userId))
                .collect(Collectors.toList());

        // 3. 转换为 DTO
        UserOrderDTO dto = new UserOrderDTO();
        dto.setUserId(userPO.getId());
        dto.setUsername(userPO.getUsername());
        dto.setPhone(userPO.getPhone());
        dto.setTransferTime(LocalDateTime.now());

        // 订单摘要
        List<UserOrderDTO.OrderSummaryDTO> orderSummaries = new ArrayList<>();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderPO po : orderPOs) {
            UserOrderDTO.OrderSummaryDTO summary = new UserOrderDTO.OrderSummaryDTO();
            summary.setOrderId(po.getId());
            summary.setOrderNo(po.getOrderNo());
            summary.setAmount(new BigDecimal(po.getAmount()).divide(new BigDecimal("100")));
            summary.setStatusDesc(getStatusDesc(po.getStatus()));
            summary.setCreateTime(po.getCreateTime());
            orderSummaries.add(summary);

            totalAmount = totalAmount.add(summary.getAmount());
        }

        dto.setOrders(orderSummaries);
        dto.setTotalOrderCount(orderSummaries.size());
        dto.setTotalOrderAmount(totalAmount);

        return dto;
    }

    // ========== 私有方法 ==========

    /**
     * 转换为 UserDetailVO
     */
    private UserDetailVO convertToUserDetailVO(UserDO userDO, List<OrderDO> orders) {
        UserDetailVO vo = new UserDetailVO();
        vo.setUserId(userDO.getId());
        vo.setUsername(userDO.getUsername());

        // 脱敏处理
        vo.setMaskedPhone(maskPhone(userDO.getPhone()));
        vo.setMaskedEmail(maskEmail(userDO.getEmail()));

        // 格式化余额
        vo.setFormattedBalance(userDO.getBalance() != null
                ? "¥" + userDO.getBalance().setScale(2, BigDecimal.ROUND_HALF_UP)
                : "¥0.00");

        // 状态描述
        vo.setStatusDesc(userDO.getStatus() != null ? userDO.getStatus().getDesc() : "未知");

        // 格式化时间
        vo.setRegisterTimeStr(userDO.getRegisterTime() != null
                ? userDO.getRegisterTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
                : "");

        // 转换订单列表
        List<UserDetailVO.UserOrderVO> orderVOs = new ArrayList<>();
        int pendingPayCount = 0;
        int completedCount = 0;
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (OrderDO order : orders) {
            UserDetailVO.UserOrderVO orderVO = new UserDetailVO.UserOrderVO();
            orderVO.setOrderId(order.getId());
            orderVO.setOrderNo(order.getOrderNo());
            orderVO.setAmountStr(order.getAmount() != null
                    ? "¥" + order.getAmount().setScale(2, BigDecimal.ROUND_HALF_UP)
                    : "¥0.00");
            orderVO.setStatusDesc(order.getStatus() != null ? order.getStatus().getDesc() : "未知");
            orderVO.setStatusColor(getStatusColor(order.getStatus()));
            orderVO.setCreateTimeStr(order.getCreateTime() != null
                    ? order.getCreateTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                    : "");
            orderVOs.add(orderVO);

            // 统计
            if (order.getStatus() == OrderDO.OrderStatus.PENDING_PAY) {
                pendingPayCount++;
            } else if (order.getStatus() == OrderDO.OrderStatus.COMPLETED) {
                completedCount++;
            }
            if (order.getAmount() != null) {
                totalAmount = totalAmount.add(order.getAmount());
            }
        }

        vo.setRecentOrders(orderVOs);

        // 统计信息
        UserDetailVO.OrderStatisticsVO stats = new UserDetailVO.OrderStatisticsVO();
        stats.setTotalCount(orders.size());
        stats.setPendingPayCount(pendingPayCount);
        stats.setCompletedCount(completedCount);
        stats.setTotalAmountStr("¥" + totalAmount.setScale(2, BigDecimal.ROUND_HALF_UP));
        vo.setOrderStatistics(stats);

        return vo;
    }

    /**
     * 手机号脱敏
     */
    private String maskPhone(String phone) {
        if (phone == null || phone.length() != 11) {
            return phone;
        }
        return phone.substring(0, 3) + "****" + phone.substring(7);
    }

    /**
     * 邮箱脱敏
     */
    private String maskEmail(String email) {
        if (email == null || !email.contains("@")) {
            return email;
        }
        int atIndex = email.indexOf("@");
        if (atIndex <= 1) {
            return email;
        }
        return email.charAt(0) + "***" + email.substring(atIndex);
    }

    /**
     * 获取状态颜色（前端展示用）
     */
    private String getStatusColor(OrderDO.OrderStatus status) {
        if (status == null) {
            return "gray";
        }
        switch (status) {
            case PENDING_PAY:
                return "orange";
            case PAID:
                return "blue";
            case SHIPPED:
                return "purple";
            case COMPLETED:
                return "green";
            case CANCELLED:
                return "gray";
            default:
                return "gray";
        }
    }

    /**
     * 获取状态描述
     */
    private String getStatusDesc(int statusCode) {
        switch (statusCode) {
            case 0:
                return "待支付";
            case 1:
                return "已支付";
            case 2:
                return "已发货";
            case 3:
                return "已完成";
            case 4:
                return "已取消";
            default:
                return "未知";
        }
    }
}
